package com.sequenceiq.distrox.v1.distrox.converter;

import static com.sequenceiq.cloudbreak.util.ConditionBasedEvaluatorUtil.evaluateIfTrueDoOtherwise;
import static com.sequenceiq.cloudbreak.util.NullUtil.getIfNotNull;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.AwsNetworkV4InstanceGroupParameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.AzureNetworkV4InstanceGroupParameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.instancegroup.network.MockNetworkV4InstanceGroupParameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.network.InstanceGroupNetworkV4Request;
import com.sequenceiq.distrox.api.v1.distrox.model.network.InstanceGroupNetworkV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.network.aws.AwsInstanceGroupNetworkV1Parameters;
import com.sequenceiq.distrox.api.v1.distrox.model.network.azure.AzureInstanceGroupNetworkV1Parameters;
import com.sequenceiq.distrox.api.v1.distrox.model.network.mock.MockInstanceGroupNetworkV1Parameters;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.EnvironmentNetworkResponse;

@Component
public class InstanceGroupNetworkV1ToInstanceGroupNetworkV4Converter {

    private static final String NO_SUBNET_ID_FOUND_MESSAGE = "No subnet id found for this environment.";

    public InstanceGroupNetworkV4Request convertToNetworkV4Request(Pair<InstanceGroupNetworkV1Request, DetailedEnvironmentResponse> network) {
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

    private MockNetworkV4InstanceGroupParameters getMockNetworkParameters(Optional<MockInstanceGroupNetworkV1Parameters> mock, EnvironmentNetworkResponse value) {
        MockInstanceGroupNetworkV1Parameters params = mock.orElse(new MockInstanceGroupNetworkV1Parameters());
        return convertToMockNetworkParams(new ImmutablePair<>(params, value));
    }

    private AzureNetworkV4InstanceGroupParameters getAzureNetworkParameters(Optional<AzureInstanceGroupNetworkV1Parameters> azure, EnvironmentNetworkResponse value) {
        AzureInstanceGroupNetworkV1Parameters params = azure.orElse(new AzureInstanceGroupNetworkV1Parameters());
        return convertToAzureStackRequest(new ImmutablePair<>(params, value));
    }

    private AwsNetworkV4InstanceGroupParameters getAwsNetworkParameters(Optional<AwsInstanceGroupNetworkV1Parameters> key, EnvironmentNetworkResponse value) {
        AwsInstanceGroupNetworkV1Parameters params = key.orElse(new AwsInstanceGroupNetworkV1Parameters());
        return convertToAwsStackRequest(new ImmutablePair<>(params, value));
    }

    private MockNetworkV4InstanceGroupParameters convertToMockNetworkParams(Pair<MockInstanceGroupNetworkV1Parameters, EnvironmentNetworkResponse> source) {
        EnvironmentNetworkResponse value = source.getValue();
        MockInstanceGroupNetworkV1Parameters key = source.getKey();

        MockNetworkV4InstanceGroupParameters params = new MockNetworkV4InstanceGroupParameters();

        if (key != null) {
            String subnetId = key.getSubnetId();
            if (value != null) {
                evaluateIfTrueDoOtherwise(subnetId, StringUtils::isNotEmpty, params::setSubnetId,
                        s -> params.setSubnetId(value.getPreferedSubnetId()));
            }
        }

        return params;
    }

    private AzureNetworkV4InstanceGroupParameters convertToAzureStackRequest(Pair<AzureInstanceGroupNetworkV1Parameters, EnvironmentNetworkResponse> source) {
        EnvironmentNetworkResponse value = source.getValue();
        AzureInstanceGroupNetworkV1Parameters key = source.getKey();

        AzureNetworkV4InstanceGroupParameters response = new AzureNetworkV4InstanceGroupParameters();

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

    private AwsNetworkV4InstanceGroupParameters convertToAwsStackRequest(Pair<AwsInstanceGroupNetworkV1Parameters, EnvironmentNetworkResponse> source) {
        AwsInstanceGroupNetworkV1Parameters key = source.getKey();

        AwsNetworkV4InstanceGroupParameters response = new AwsNetworkV4InstanceGroupParameters();

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

    private AzureInstanceGroupNetworkV1Parameters convertToDistroXRequest(AzureNetworkV4InstanceGroupParameters source) {
        AzureInstanceGroupNetworkV1Parameters response = new AzureInstanceGroupNetworkV1Parameters();
        response.setSubnetId(source.getSubnetId());
        return response;
    }

    private AwsInstanceGroupNetworkV1Parameters convertToDistroXRequest(AwsNetworkV4InstanceGroupParameters source) {
        AwsInstanceGroupNetworkV1Parameters response = new AwsInstanceGroupNetworkV1Parameters();
        response.setSubnetId(source.getSubnetId());
        return response;
    }
}
