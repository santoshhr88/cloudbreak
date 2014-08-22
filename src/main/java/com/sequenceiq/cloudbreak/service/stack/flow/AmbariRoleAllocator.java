package com.sequenceiq.cloudbreak.service.stack.flow;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Reactor;
import reactor.event.Event;

import com.sequenceiq.cloudbreak.conf.ReactorConfig;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.Status;
import com.sequenceiq.cloudbreak.repository.RetryingStackUpdater;
import com.sequenceiq.cloudbreak.repository.StackRepository;
import com.sequenceiq.cloudbreak.service.stack.event.AmbariRoleAllocationComplete;
import com.sequenceiq.cloudbreak.service.stack.event.StackCreationFailure;
import com.sequenceiq.cloudbreak.service.stack.event.domain.CoreInstanceMetaData;

@Service
public class AmbariRoleAllocator {

    private static final String DOCKER_SUBNET_PREFIX = "172.17.1";

    private static final Logger LOGGER = LoggerFactory.getLogger(AmbariRoleAllocator.class);

    @Autowired
    private StackRepository stackRepository;

    @Autowired
    private RetryingStackUpdater stackUpdater;

    @Autowired
    private Reactor reactor;

    public void allocateRoles(Long stackId, Set<CoreInstanceMetaData> coreInstanceMetaData) {
        try {
            Stack stack = stackRepository.findById(stackId);
            if (!stack.isMetadataReady()) {
                if (coreInstanceMetaData.size() != stack.getNodeCount()) {
                    throw new WrongMetadataException(String.format(
                            "Size of the collected metadata set does not equal the node count of the stack. [stack: '%s']", stack.getId()));
                }
                Set<InstanceMetaData> instanceMetaDatas = prepareInstanceMetaData(stack, coreInstanceMetaData);
                stackUpdater.updateStackMetaData(stackId, instanceMetaDatas);
                stackUpdater.updateMetadataReady(stackId);
                LOGGER.info("Publishing {} event [StackId: '{}']", ReactorConfig.AMBARI_ROLE_ALLOCATION_COMPLETE_EVENT, stackId);
                reactor.notify(ReactorConfig.AMBARI_ROLE_ALLOCATION_COMPLETE_EVENT, Event.wrap(new AmbariRoleAllocationComplete(stackId,
                        getAmbariIp(instanceMetaDatas))));
            } else {
                LOGGER.info("Metadata of stack '{}' is already created, ignoring '{}' event.", stackId, ReactorConfig.METADATA_SETUP_COMPLETE_EVENT);
            }
        } catch (WrongMetadataException e) {
            LOGGER.error(e.getMessage(), e);
            notifyStackCreateFailed(stackId, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unhandled exception occured while creating stack.", e);
            notifyStackCreateFailed(stackId, "Unhandled exception occured while creating stack.");
        }
    }

    public void updateInstanceMetadata(Long stackId, Set<CoreInstanceMetaData> coreInstanceMetaData) {
        try {
            Stack stack = stackRepository.findOneWithLists(stackId);
            Set<InstanceMetaData> originalMetadata = stack.getInstanceMetaData();
            Set<InstanceMetaData> instanceMetaData = prepareInstanceMetaData(stack, coreInstanceMetaData, stack.getInstanceMetaData().size() + 1);
            originalMetadata.addAll(instanceMetaData);
            stackUpdater.updateStackMetaData(stackId, originalMetadata);
            stackUpdater.updateMetadataReady(stackId);
            stackUpdater.updateNodeCount(stackId, originalMetadata.size());
            stackUpdater.updateStackStatus(stackId, Status.AVAILABLE);
        } catch (WrongMetadataException e) {
            LOGGER.error(e.getMessage(), e);
            // TODO: handle update failure
        } catch (Exception e) {
            LOGGER.error("Unhandled exception occured while creating stack.", e);
            // TODO: handle update failure
        }
    }

    private String getAmbariIp(Set<InstanceMetaData> instanceMetaDatas) {
        for (InstanceMetaData instanceMetaData : instanceMetaDatas) {
            if (Boolean.TRUE.equals(instanceMetaData.getAmbariServer())) {
                return instanceMetaData.getPublicIp();
            }
        }
        return null;
    }

    private Set<InstanceMetaData> prepareInstanceMetaData(Stack stack, Set<CoreInstanceMetaData> coreInstanceMetaData) {
        return prepareInstanceMetaData(stack, coreInstanceMetaData, 0);
    }

    private Set<InstanceMetaData> prepareInstanceMetaData(Stack stack, Set<CoreInstanceMetaData> coreInstanceMetaData, int startIndex) {
        Set<InstanceMetaData> instanceMetaData = new HashSet<>();
        int instanceIndex = startIndex;
        String ambariIp = null;
        for (CoreInstanceMetaData coreInstanceMetaDataEntry : coreInstanceMetaData) {
            InstanceMetaData instanceMetaDataEntry = new InstanceMetaData();
            instanceMetaDataEntry.setPrivateIp(coreInstanceMetaDataEntry.getPrivateIp());
            instanceMetaDataEntry.setPublicIp(coreInstanceMetaDataEntry.getPublicIp());
            instanceMetaDataEntry.setInstanceId(coreInstanceMetaDataEntry.getInstanceId());
            instanceMetaDataEntry.setVolumeCount(coreInstanceMetaDataEntry.getVolumeCount());
            instanceMetaDataEntry.setLongName(coreInstanceMetaDataEntry.getLongName());
            instanceMetaDataEntry.setInstanceIndex(instanceIndex);
            instanceMetaDataEntry.setDockerSubnet(DOCKER_SUBNET_PREFIX + instanceIndex);
            if (instanceIndex == 0) {
                instanceMetaDataEntry.setAmbariServer(Boolean.TRUE);
                ambariIp = instanceMetaDataEntry.getPublicIp();
                if (ambariIp == null) {
                    throw new WrongMetadataException(String.format("Public IP of Ambari server cannot be null [stack: '%s', instanceId: '%s' ]",
                            stack.getId(), coreInstanceMetaDataEntry.getInstanceId()));
                }
            } else {
                instanceMetaDataEntry.setAmbariServer(Boolean.FALSE);
            }
            instanceIndex++;
            instanceMetaDataEntry.setStack(stack);
            instanceMetaData.add(instanceMetaDataEntry);
        }
        return instanceMetaData;
    }

    private void notifyStackCreateFailed(Long stackId, String cause) {
        LOGGER.info("Publishing {} event [StackId: '{}']", ReactorConfig.STACK_CREATE_FAILED_EVENT, stackId);
        StackCreationFailure stackCreationFailure = new StackCreationFailure(stackId, cause);
        reactor.notify(ReactorConfig.STACK_CREATE_FAILED_EVENT, Event.wrap(stackCreationFailure));
    }

}
