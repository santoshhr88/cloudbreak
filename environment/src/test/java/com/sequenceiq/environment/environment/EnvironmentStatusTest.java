package com.sequenceiq.environment.environment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EnvironmentStatusTest {

    // @formatter:off
    // CHECKSTYLE:OFF
    static Object[][] isDeleteInProgressDataProvider() {
        return new Object[][] {
                // testCaseName                             underTest                                               deleteInProgressExpected
                { "CREATION_INITIATED",                     EnvironmentStatus.CREATION_INITIATED,                   false },
                { "DELETE_INITIATED",                       EnvironmentStatus.DELETE_INITIATED,                     true },
                { "UPDATE_INITIATED",                       EnvironmentStatus.UPDATE_INITIATED,                     false },
                { "NETWORK_CREATION_IN_PROGRESS",           EnvironmentStatus.NETWORK_CREATION_IN_PROGRESS,         false },
                { "NETWORK_DELETE_IN_PROGRESS",             EnvironmentStatus.NETWORK_DELETE_IN_PROGRESS,           true },
                { "FREEIPA_CREATION_IN_PROGRESS",           EnvironmentStatus.FREEIPA_CREATION_IN_PROGRESS,         false },
                { "FREEIPA_DELETE_IN_PROGRESS",             EnvironmentStatus.FREEIPA_DELETE_IN_PROGRESS,           true },
                { "RDBMS_DELETE_IN_PROGRESS",               EnvironmentStatus.RDBMS_DELETE_IN_PROGRESS,             true },
                { "IDBROKER_MAPPINGS_DELETE_IN_PROGRESS",   EnvironmentStatus.IDBROKER_MAPPINGS_DELETE_IN_PROGRESS, true },
                { "S3GUARD_TABLE_DELETE_IN_PROGRESS",       EnvironmentStatus.S3GUARD_TABLE_DELETE_IN_PROGRESS,     true },
                { "AVAILABLE",                              EnvironmentStatus.AVAILABLE,                            false },
                { "ARCHIVED",                               EnvironmentStatus.ARCHIVED,                             false },
                { "CREATE_FAILED",                          EnvironmentStatus.CREATE_FAILED,                        false },
                { "DELETE_FAILED",                          EnvironmentStatus.DELETE_FAILED,                        false },
                { "UPDATE_FAILED",                          EnvironmentStatus.UPDATE_FAILED,                        false },
        };
    }
    // CHECKSTYLE:ON
    // @formatter:on

    @ParameterizedTest(name = "{0}")
    @MethodSource("isDeleteInProgressDataProvider")
    void isDeleteInProgressTest(String testCaseName, EnvironmentStatus underTest, boolean deleteInProgressExpected) {
        assertThat(underTest.isDeleteInProgress()).isEqualTo(deleteInProgressExpected);
    }

    // @formatter:off
    // CHECKSTYLE:OFF
    static Object[][] isSuccessfullyDeletedDataProvider() {
        return new Object[][] {
                // testCaseName                             underTest                                               successfullyDeletedExpected
                { "CREATION_INITIATED",                     EnvironmentStatus.CREATION_INITIATED,                   false },
                { "DELETE_INITIATED",                       EnvironmentStatus.DELETE_INITIATED,                     false },
                { "UPDATE_INITIATED",                       EnvironmentStatus.UPDATE_INITIATED,                     false },
                { "NETWORK_CREATION_IN_PROGRESS",           EnvironmentStatus.NETWORK_CREATION_IN_PROGRESS,         false },
                { "NETWORK_DELETE_IN_PROGRESS",             EnvironmentStatus.NETWORK_DELETE_IN_PROGRESS,           false },
                { "FREEIPA_CREATION_IN_PROGRESS",           EnvironmentStatus.FREEIPA_CREATION_IN_PROGRESS,         false },
                { "FREEIPA_DELETE_IN_PROGRESS",             EnvironmentStatus.FREEIPA_DELETE_IN_PROGRESS,           false },
                { "RDBMS_DELETE_IN_PROGRESS",               EnvironmentStatus.RDBMS_DELETE_IN_PROGRESS,             false },
                { "IDBROKER_MAPPINGS_DELETE_IN_PROGRESS",   EnvironmentStatus.IDBROKER_MAPPINGS_DELETE_IN_PROGRESS, false },
                { "S3GUARD_TABLE_DELETE_IN_PROGRESS",       EnvironmentStatus.S3GUARD_TABLE_DELETE_IN_PROGRESS,     false },
                { "AVAILABLE",                              EnvironmentStatus.AVAILABLE,                            false },
                { "ARCHIVED",                               EnvironmentStatus.ARCHIVED,                             true },
                { "CREATE_FAILED",                          EnvironmentStatus.CREATE_FAILED,                        false },
                { "DELETE_FAILED",                          EnvironmentStatus.DELETE_FAILED,                        false },
                { "UPDATE_FAILED",                          EnvironmentStatus.UPDATE_FAILED,                        false },
        };
    }
    // CHECKSTYLE:ON
    // @formatter:on

    @ParameterizedTest(name = "{0}")
    @MethodSource("isSuccessfullyDeletedDataProvider")
    void isSuccessfullyDeletedTest(String testCaseName, EnvironmentStatus underTest, boolean successfullyDeletedExpected) {
        assertThat(underTest.isSuccessfullyDeleted()).isEqualTo(successfullyDeletedExpected);
    }

}
