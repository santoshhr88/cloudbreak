package com.sequenceiq.it.cloudbreak.action.v4.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.it.cloudbreak.RedbeamsClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.database.RedbeamsDatabaseTestDto;
import com.sequenceiq.it.cloudbreak.log.Log;

public class RedbeamsDatabaseDeleteAction implements Action<RedbeamsDatabaseTestDto, RedbeamsClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedbeamsDatabaseDeleteAction.class);

    @Override
    public RedbeamsDatabaseTestDto action(TestContext testContext, RedbeamsDatabaseTestDto testDto, RedbeamsClient client) throws Exception {
        Log.when(LOGGER, String.format("Database delete request Name: %s", testDto.getRequest().getName()));
        testDto.setResponse(
                client.getEndpoints()
                        .databaseV4Endpoint()
                        .deleteByName(client.getEnvironmentCrn(), testDto.getName()));
        Log.whenJson(LOGGER, " Database deleted successfully:\n", testDto.getResponse());
        return testDto;
    }

}
