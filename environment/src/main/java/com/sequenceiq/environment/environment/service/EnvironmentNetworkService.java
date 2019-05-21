package com.sequenceiq.environment.environment.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.environment.domain.Environment;
import com.sequenceiq.environment.network.BaseNetwork;
import com.sequenceiq.environment.network.converter.EnvironmentNetworkConverter;
import com.sequenceiq.environment.network.dto.NetworkDto;
import com.sequenceiq.environment.network.repository.BaseNetworkRepository;

@Service
public class EnvironmentNetworkService {

    private final BaseNetworkRepository networkRepository;

    private final Map<CloudPlatform, EnvironmentNetworkConverter> environmentNetworkConverterMap;

    public EnvironmentNetworkService(BaseNetworkRepository networkRepository,
            Map<CloudPlatform, EnvironmentNetworkConverter> environmentNetworkConverterMap) {
        this.networkRepository = networkRepository;
        this.environmentNetworkConverterMap = environmentNetworkConverterMap;
    }

    public BaseNetwork createNetworkIfPossible(Environment environment, NetworkDto networkDto, CloudPlatform cloudPlatform) {
        BaseNetwork network = null;
        if (networkDto != null) {
            EnvironmentNetworkConverter environmentNetworkConverter = environmentNetworkConverterMap.get(cloudPlatform);
            if (environmentNetworkConverter != null) {
                BaseNetwork baseNetwork = environmentNetworkConverter.convert(networkDto, environment);
                network = save(baseNetwork);
            }
        }
        return network;
    }

    @SuppressWarnings("unchecked")
    public BaseNetwork save(BaseNetwork awsNetwork) {
        Object saved = networkRepository.save(awsNetwork);
        return (BaseNetwork) saved;
    }
}
