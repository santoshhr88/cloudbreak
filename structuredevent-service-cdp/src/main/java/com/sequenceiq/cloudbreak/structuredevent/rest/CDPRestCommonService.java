package com.sequenceiq.cloudbreak.structuredevent.rest;

import static com.sequenceiq.cloudbreak.structuredevent.rest.urlparser.CDPRestUrlParser.ID_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.anonymizer.AnonymizerUtil;
import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;
import com.sequenceiq.cloudbreak.structuredevent.event.rest.RestCallDetails;
import com.sequenceiq.cloudbreak.structuredevent.event.rest.RestRequestDetails;

@Component
public class CDPRestCommonService {

    public static final String NAME_PATH = "name";

    public static final String NAMES_PATH = "names";

    public static final String RESOURCE_CRN_PATH = "crn";

    public static final String CRNS_PATH = "crns";

    public Map<String, String> addClusterCrnAndNameIfPresent(RestCallDetails restCallDetails, Map<String, String> restParams, String nameField,
            String crnField) {
        Map<String, String> params = new HashMap<>();
        RestRequestDetails restRequest = restCallDetails.getRestRequest();
        Json requestJson = getJson(restRequest.getBody());
        Json responseJson = getJson(restCallDetails.getRestResponse().getBody());
        String resourceCrn = getCrn(requestJson, responseJson, restRequest, restParams, crnField);
        String name = getName(requestJson, responseJson, restRequest, restParams, nameField);

        checkNameOrCrnProvided(restRequest, resourceCrn, name);

        if (StringUtils.isNotEmpty(name)) {
            params.put(nameField, name);
        }

        if (StringUtils.isNotEmpty(resourceCrn)) {
            params.put(crnField, resourceCrn);
        }
        return params;
    }

    private String getName(Json requestJson, Json responseJson, RestRequestDetails request, Map<String, String> restParams, String nameField) {
        if (StringUtils.isEmpty(restParams.get(nameField))) {
            return getResourceId(requestJson, responseJson, request, NAME_PATH, NAMES_PATH, restParams, "name");
        }
        return restParams.get(nameField);
    }

    private String getCrn(Json requestJson, Json responseJson, RestRequestDetails request, Map<String, String> restParams, String crnField) {
        if (StringUtils.isEmpty(restParams.get(crnField))) {
            return getResourceId(requestJson, responseJson, request, RESOURCE_CRN_PATH, CRNS_PATH, restParams, "crn");
        }
        return restParams.get(crnField);
    }

    private String getResourceId(Json requestJson, Json responseJson, RestRequestDetails request, String path, String pluralPath, Map<String, String> restParams,
            String idType) {
        String id = null;
        if (requestJson != null) {
            id = getValueFromJson(requestJson, path);
            if (StringUtils.isEmpty(id)) {
                id = getListValue(request, requestJson, pluralPath, restParams, idType);
            }
        }
        if (responseJson != null && StringUtils.isEmpty(id)) {
                id = Optional.ofNullable(getValueFromJson(responseJson, path))
                        .orElse(getListValue(request, responseJson, pluralPath, restParams, idType));
        }
        return id;
    }

    private String getListValue(RestRequestDetails restRequest, Json json, String path, Map<String, String> restParams, String idType) {
        String method = restRequest.getMethod();
        if ("DELETE".equals(method)) {
            List<String> values;
            if (json.isArray() && idType.equals(restParams.get(ID_TYPE))) {
                values = json.asArray();
            } else if (json.isObject() && json.getMap().containsKey("responses")) {
                values = ((List<Object>) json.getMap().get("responses"))
                        .stream()
                        .map(obj -> (String) new Json(obj).getMap().get(idType))
                        .collect(Collectors.toList());
            } else if (json.isObject()) {
                values = json.getValue(path);
            } else {
                values = List.of();
            }
            if (CollectionUtils.isNotEmpty(values)) {
                return String.join(",", values);
            }
        }
        return null;
    }

    private Json getJson(String body) {
        if (body != null && StringUtils.isNotEmpty(body.trim())) {
            if (!JsonUtil.isValid(body)) {
                throw new IllegalArgumentException("Invalid json: " + AnonymizerUtil.anonymize(body));
            }
            return new Json(body);
        }
        return null;
    }

    private String getValueFromJson(Json json, String path) {
        if (json.isObject()) {
            return json.getValue(path);
        }
        return null;
    }

    private void checkNameOrCrnProvided(RestRequestDetails restRequest, String resourceCrn, String name) {
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(resourceCrn)) {
            throw new UnsupportedOperationException(String.format("Cannot determine the resource crn or name, so we does not support for auditing for method: "
                    + "%s, uri: %s, body: %s", restRequest.getMethod(), restRequest.getRequestUri(), restRequest.getBody()));
        }
    }
}
