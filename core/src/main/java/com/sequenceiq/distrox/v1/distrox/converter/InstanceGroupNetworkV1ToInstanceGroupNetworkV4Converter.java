package com.sequenceiq.distrox.v1.distrox.converter;

import static com.sequenceiq.cloudbreak.util.ConditionBasedEvaluatorUtil.evaluateIfTrueDoOtherwise;
import static com.sequenceiq.cloudbreak.util.NullUtil.getIfNotNull;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.InstanceGroupAwsNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.InstanceGroupAzureNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.InstanceGroupMockNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.network.InstanceGroupNetworkV4Request;
import com.sequenceiq.distrox.api.v1.distrox.model.network.InstanceGroupNetworkV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.network.aws.InstanceGroupAwsNetworkV1Parameters;
import com.sequenceiq.distrox.api.v1.distrox.model.network.azure.InstanceGroupAzureNetworkV1Parameters;
import com.sequenceiq.distrox.api.v1.distrox.model.network.mock.InstanceGroupMockNetworkV1Parameters;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.EnvironmentNetworkResponse;

@Component
public class InstanceGroupNetworkV1ToInstanceGroupNetworkV4Converter {

    private static final String NO_SUBNET_ID_FOUND_MESSAGE = "No subnet id found for this environment.";

    public InstanceGroupNetworkV4Request convertToInstanceGroupNetworkV4Request(Pair<InstanceGroupNetworkV1Request, DetailedEnvironmentResponse> network) {
        EnvironmentNetworkResponse value = network.getValue().getNetwork();
        InstanceGroupNetworkV1Request key = network.getKey();
        if (key == null) {
            key = new InstanceGroupNetworkV1Request();
        }

        InstanceGroupNetworkV4Request request = new InstanceGroupNetworkV4Request();

        switch (network.getValue().getCloudPlatform()) {
            case "AWS":
                request.setAws(getAwsNetworkParameters(Optional.ofNullable(key.getAws()), value));
                break;
            case "AZURE":
                request.setAzure(getAzureNetworkParameters(Optional.ofNullable(key.getAzure()), value));
                break;
            case "MOCK":
                request.setMock(getMockNetworkParameters(Optional.ofNullable(key.getMock()), value));
                break;
            default:
        }
        return request;
    }

    private InstanceGroupMockNetworkV4Parameters getMockNetworkParameters(Optional<InstanceGroupMockNetworkV1Parameters> mock, EnvironmentNetworkResponse value) {
        InstanceGroupMockNetworkV1Parameters params = mock.orElse(new InstanceGroupMockNetworkV1Parameters());
        return convertToMockNetworkParams(new ImmutablePair<>(params, value));
    }

    private InstanceGroupAzureNetworkV4Parameters getAzureNetworkParameters(Optional<InstanceGroupAzureNetworkV1Parameters> azure, EnvironmentNetworkResponse value) {
        InstanceGroupAzureNetworkV1Parameters params = azure.orElse(new InstanceGroupAzureNetworkV1Parameters());
        return convertToAzureStackRequest(new ImmutablePair<>(params, value));
    }

    private InstanceGroupAwsNetworkV4Parameters getAwsNetworkParameters(Optional<InstanceGroupAwsNetworkV1Parameters> key, EnvironmentNetworkResponse value) {
        InstanceGroupAwsNetworkV1Parameters params = key.orElse(new InstanceGroupAwsNetworkV1Parameters());
        return convertToAwsStackRequest(new ImmutablePair<>(params, value));
    }

    private InstanceGroupMockNetworkV4Parameters convertToMockNetworkParams(Pair<InstanceGroupMockNetworkV1Parameters, EnvironmentNetworkResponse> source) {
        EnvironmentNetworkResponse value = source.getValue();
        InstanceGroupMockNetworkV1Parameters key = source.getKey();

        InstanceGroupMockNetworkV4Parameters params = new InstanceGroupMockNetworkV4Parameters();

        if (key != null) {
            String subnetId = key.getSubnetId();
            if (value != null) {
                evaluateIfTrueDoOtherwise(subnetId, StringUtils::isNotEmpty, params::setSubnetId,
                        s -> params.setSubnetId(value.getPreferedSubnetId()));
            }
        }

        return params;
    }

    private InstanceGroupAzureNetworkV4Parameters convertToAzureStackRequest(Pair<InstanceGroupAzureNetworkV1Parameters, EnvironmentNetworkResponse> source) {
        EnvironmentNetworkResponse value = source.getValue();
        InstanceGroupAzureNetworkV1Parameters key = source.getKey();

        InstanceGroupAzureNetworkV4Parameters response = new InstanceGroupAzureNetworkV4Parameters();

        if (key != null) {
            String subnetId = key.getSubnetId();
            if (!Strings.isNullOrEmpty(subnetId)) {
                response.setSubnetId(subnetId);
            } else if (source.getValue() != null) {
                response.setSubnetId(source.getValue().getPreferedSubnetId());
            }
        }

        return response;
    }

    private InstanceGroupAwsNetworkV4Parameters convertToAwsStackRequest(Pair<InstanceGroupAwsNetworkV1Parameters, EnvironmentNetworkResponse> source) {
        InstanceGroupAwsNetworkV1Parameters key = source.getKey();

        InstanceGroupAwsNetworkV4Parameters response = new InstanceGroupAwsNetworkV4Parameters();

        if (key != null) {
            String subnetId = key.getSubnetId();
            if (!Strings.isNullOrEmpty(subnetId)) {
                response.setSubnetId(key.getSubnetId());
            } else if (source.getValue() != null) {
                response.setSubnetId(source.getValue().getPreferedSubnetId());
            }
        }

        return response;
    }

    public InstanceGroupNetworkV1Request convertToNetworkV1Request(InstanceGroupNetworkV4Request network) {
        InstanceGroupNetworkV1Request response = new InstanceGroupNetworkV1Request();
        response.setAws(getIfNotNull(network.getAws(), this::convertToDistroXRequest));
        response.setAzure(getIfNotNull(network.getAzure(), this::convertToDistroXRequest));
        return response;
    }

    private InstanceGroupAzureNetworkV1Parameters convertToDistroXRequest(InstanceGroupAzureNetworkV4Parameters source) {
        InstanceGroupAzureNetworkV1Parameters response = new InstanceGroupAzureNetworkV1Parameters();
        response.setSubnetId(source.getSubnetId());
        return response;
    }

    private InstanceGroupAwsNetworkV1Parameters convertToDistroXRequest(InstanceGroupAwsNetworkV4Parameters source) {
        InstanceGroupAwsNetworkV1Parameters response = new InstanceGroupAwsNetworkV1Parameters();
        response.setSubnetId(source.getSubnetId());
        return response;
    }
}
