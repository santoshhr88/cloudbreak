package com.sequenceiq.cloudbreak.reactor.api.event.cluster;

import com.sequenceiq.cloudbreak.reactor.api.event.resource.AbstractClusterScaleRequest;

public class UpscaleClusterRequest extends AbstractClusterScaleRequest {

    private boolean repair;

    public UpscaleClusterRequest(Long stackId, String hostGroupName, boolean repair) {
        super(stackId, hostGroupName);
        this.repair = repair;
    }

    public boolean isRepair() {
        return repair;
    }

}
