package com.sequenceiq.redbeams.sync;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import com.sequenceiq.redbeams.domain.stack.DBStack;
import com.sequenceiq.redbeams.service.stack.DBStackService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DBStackJobInizializerTest {

    @Mock
    private DBStackService dbStackService;

    @Mock
    private DBStackJobService dbStackJobService;

    @InjectMocks
    private DBStackJobInizializer victim;

    @BeforeEach
    public void initTests() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldDeleteAllJobsAndScheduleNewOnesForDbStacks() {
        DBStack dbStack = new DBStack();
        Set<DBStack> dbStacks = Set.of(dbStack);

        when(dbStackService.findAllForAutoSync()).thenReturn(dbStacks);

        victim.initJobs();

        verify(dbStackJobService).deleteAll();
        verify(dbStackJobService).schedule(dbStack);
    }
}