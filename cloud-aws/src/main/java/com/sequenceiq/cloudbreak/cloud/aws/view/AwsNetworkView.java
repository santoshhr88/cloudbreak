package com.sequenceiq.cloudbreak.cloud.aws.view;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.sequenceiq.cloudbreak.cloud.model.CloudStack;
import com.sequenceiq.cloudbreak.cloud.model.Group;
import com.sequenceiq.cloudbreak.cloud.model.Network;

public class AwsNetworkView {

    public static final String VPC_ID = "vpcId";

    public static final String REGION = "region";

    @VisibleForTesting
    static final String VPC_CIDR = "vpcCidr";

    @VisibleForTesting
    static final String VPC_CIDRS = "vpcCidrs";

    @VisibleForTesting
    static final String IGW = "internetGatewayId";

    @VisibleForTesting
    static final String SUBNET_ID = "subnetId";

    private final Network network;

    private final boolean isExistingSubnet;

    private final Set<String> subnets;

    public AwsNetworkView(Network network, CloudStack stack) {
        this.network = network;
        boolean tmpIsExistingSubnet = false;
        for (Group group : stack.getGroups()) {
            if (new AwsGroupNetworkView(group.getNetwork()).isExistingSubnet()) {
                tmpIsExistingSubnet = true;
                break;
            }
        }
        this.isExistingSubnet = tmpIsExistingSubnet;
        Set<String> tmpSubnets = new HashSet<>();
        for (Group group : stack.getGroups()) {
            AwsGroupNetworkView awsGroupNetworkView = new AwsGroupNetworkView(group.getNetwork());
            if (awsGroupNetworkView.isExistingSubnet()) {
                tmpSubnets.add(awsGroupNetworkView.getExistingSubnet());
            }
        }
        this.subnets = tmpSubnets;

    }

    public AwsNetworkView(Network network) {
        this.network = network;
        this.isExistingSubnet = isNotEmpty(this.network.getStringParameter(SUBNET_ID));
        this.subnets = Set.of(this.network.getStringParameter(SUBNET_ID).split(","));
    }

    public boolean isExistingVPC() {
        return isNotEmpty(network.getStringParameter(VPC_ID));
    }

    public boolean isExistingSubnet() {
        return isExistingSubnet;
    }

    public boolean isExistingIGW() {
        return isNotEmpty(network.getStringParameter(IGW));
    }

    public String getExistingSubnet() {
        return subnets.stream().findFirst().orElse(null);
    }

    public boolean isSubnetList() {
        return isExistingSubnet() && getExistingSubnet().contains(",");
    }

    public Set<String> getSubnetList() {
        return isExistingSubnet() ? subnets : Set.of();
    }

    public String getExistingIgw() {
        return network.getStringParameter(IGW);
    }

    public String getExistingVpc() {
        return network.getStringParameter(VPC_ID);
    }

    public String getExistingVpcCidr() {
        return network.getStringParameter(VPC_CIDR);
    }

    public List<String> getExistingVpcCidrs() {
        List<String> cidrs = network.getParameter(VPC_CIDRS, List.class);
        if (cidrs == null || cidrs.isEmpty()) {
            return List.of(getExistingVpcCidr());
        }
        return cidrs;
    }

}
