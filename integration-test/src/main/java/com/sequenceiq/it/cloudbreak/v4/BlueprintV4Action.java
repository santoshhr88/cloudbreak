package com.sequenceiq.it.cloudbreak.v4;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.sequenceiq.cloudbreak.api.endpoint.v4.blueprint.responses.BlueprintV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.blueprint.responses.BlueprintV4ViewResponse;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.cloudbreak.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.CloudbreakTest;
import com.sequenceiq.it.cloudbreak.Entity;
import com.sequenceiq.it.cloudbreak.dto.blueprint.BlueprintTestDto;
import com.sequenceiq.it.cloudbreak.log.Log;

public class BlueprintV4Action {

    private BlueprintV4Action() {
    }

    public static void post(IntegrationTestContext integrationTestContext, Entity entity) {
        BlueprintTestDto blueprintEntity = (BlueprintTestDto) entity;
        CloudbreakClient client;
        client = integrationTestContext.getContextParam(CloudbreakClient.CLOUDBREAK_CLIENT, CloudbreakClient.class);
        Long workspaceId = integrationTestContext.getContextParam(CloudbreakTest.WORKSPACE_ID, Long.class);
        Log.log(" post "
                .concat(blueprintEntity.getName())
                .concat(" private blueprint. "));
        blueprintEntity.setResponse(
                client.getCloudbreakClient()
                        .blueprintV4Endpoint()
                        .post(workspaceId, blueprintEntity.getRequest()));

        integrationTestContext.putCleanUpParam(blueprintEntity.getName(), blueprintEntity.getResponse().getCrn());
    }

    public static void get(IntegrationTestContext integrationTestContext, Entity entity) throws IOException {
        BlueprintTestDto blueprintEntity = (BlueprintTestDto) entity;
        CloudbreakClient client;
        client = integrationTestContext.getContextParam(CloudbreakClient.CLOUDBREAK_CLIENT,
                CloudbreakClient.class);
        Long workspaceId = integrationTestContext.getContextParam(CloudbreakTest.WORKSPACE_ID, Long.class);
        Log.log(" getByName "
                .concat(blueprintEntity.getName())
                .concat(" private blueprint by Name. "));
        blueprintEntity.setResponse(
                client.getCloudbreakClient()
                        .blueprintV4Endpoint().getByName(workspaceId, blueprintEntity.getName()));
        Log.whenJson(" getByName "
                .concat(blueprintEntity.getName())
                .concat(" blueprint response: "),
                blueprintEntity.getResponse());
    }

    public static void getAll(IntegrationTestContext integrationTestContext, Entity entity) {
        BlueprintTestDto blueprintEntity = (BlueprintTestDto) entity;
        CloudbreakClient client;
        client = integrationTestContext.getContextParam(CloudbreakClient.CLOUDBREAK_CLIENT,
                CloudbreakClient.class);
        Long workspaceId = integrationTestContext.getContextParam(CloudbreakTest.WORKSPACE_ID, Long.class);
        Log.log(" getByName all private blueprints. ");
        Collection<BlueprintV4ViewResponse> blueprints = client.getCloudbreakClient().blueprintV4Endpoint()
                .list(workspaceId, true).getResponses();
        Set<BlueprintV4Response> detailedBlueprints = blueprints.stream()
                .map(bp -> client.getCloudbreakClient().blueprintV4Endpoint()
                        .getByName(workspaceId, bp.getName())).collect(Collectors.toSet());
        blueprintEntity.setResponses(detailedBlueprints);
    }

    public static void delete(IntegrationTestContext integrationTestContext, Entity entity) {
        BlueprintTestDto blueprintEntity = (BlueprintTestDto) entity;
        CloudbreakClient client;
        client = integrationTestContext.getContextParam(CloudbreakClient.CLOUDBREAK_CLIENT,
                CloudbreakClient.class);
        Long workspaceId = integrationTestContext.getContextParam(CloudbreakTest.WORKSPACE_ID, Long.class);
        Log.log(" deleteByName "
                .concat(blueprintEntity.getName())
                .concat(" private blueprint with Name. "));
        client.getCloudbreakClient().blueprintV4Endpoint().deleteByName(workspaceId, blueprintEntity.getName());
    }

    public static void createInGiven(IntegrationTestContext integrationTestContext, Entity entity) {
        post(integrationTestContext, entity);
    }

}
