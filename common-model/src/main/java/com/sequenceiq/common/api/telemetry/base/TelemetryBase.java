package com.sequenceiq.common.api.telemetry.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.sequenceiq.common.api.telemetry.doc.TelemetryModelDescription;

import io.swagger.annotations.ApiModelProperty;

public abstract class TelemetryBase implements Serializable {

    @ApiModelProperty(TelemetryModelDescription.TELEMETRY_REPORT_DEPLOYMENT_LOGS_ENABLED)
    private Boolean reportDeploymentLogs = Boolean.TRUE;

    @ApiModelProperty(TelemetryModelDescription.TELEMETRY_FLUENT_ATTRIBUTES)
    private Map<String, Object> fluentAttributes = new HashMap<>();

    public Boolean getReportDeploymentLogs() {
        return reportDeploymentLogs;
    }

    public void setReportDeploymentLogs(Boolean reportDeploymentLogs) {
        this.reportDeploymentLogs = reportDeploymentLogs;
    }

    public Map<String, Object> getFluentAttributes() {
        return fluentAttributes;
    }

    public void setFluentAttributes(Map<String, Object> fluentAttributes) {
        this.fluentAttributes = fluentAttributes;
    }
}
