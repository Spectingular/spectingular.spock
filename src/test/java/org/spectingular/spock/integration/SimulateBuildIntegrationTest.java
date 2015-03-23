package org.spectingular.spock.integration;

import org.junit.Test;
import org.spectingular.spock.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration test. */
public class SimulateBuildIntegrationTest extends IntegrationTestBase {

    @Test
    public void simulateSuccessfulBuild() {
        registerBuild("10");
        registerPhase("10", "phase1", Object.class);
        registerTask("10", "phase1", "task1", Object.class);
        finishTask("10", "phase1", "task1", true, Object.class);
        registerTask("10", "phase1", "task2", Object.class);
        finishTask("10", "phase1", "task2", true, Object.class);
        finishPhase("10", "phase1", true, Object.class);
        registerPhase("10", "phase2", Object.class);
        registerTask("10", "phase2", "task1", Object.class);
        registerModule("10", "module1", Object.class);
        registerPhase("10", "module1", "phase1", Object.class);
        registerTask("10", "module1", "phase1", "task1", Object.class);
        finishTask("10", "module1", "phase1", "task1", true, Object.class);
        registerTask("10", "module1", "phase1", "task2", Object.class);
        finishTask("10", "module1", "phase1", "task2", true, Object.class);
        finishPhase("10", "module1", "phase1", true, Object.class);
        finishModule("10", "module1", true, Object.class);
        registerModule("10", "module2", Object.class);
        registerPhase("10", "module2", "phase1", Object.class);
        registerTask("10", "module2", "phase1", "task1", Object.class);
        finishTask("10", "module2", "phase1", "task1", true, Object.class);
        registerTask("10", "module2", "phase1", "task2", Object.class);
        finishTask("10", "module2", "phase1", "task2", true, Object.class);
        finishPhase("10", "module2", "phase1", true, Object.class);
        finishModule("10", "module2", true, Object.class);
        finishTask("10", "phase2", "task1", true, Object.class);
        registerTask("10", "phase2", "task2", Object.class);
        finishTask("10", "phase2", "task2", true, Object.class);
        finishPhase("10", "phase2", true, Object.class);
        finishBuild("10", true);

        final ResponseEntity<BuildDto> entity = getBuild("10");

        assertTrue(entity.getStatusCode().is2xxSuccessful());

        final BuildDto dto = entity.getBody();
        assertEquals(RunState.FINISHED_SUCCESSFULLY, dto.getState());

        final List<ModuleDto> modules = dto.getModules();
        assertEquals(2, modules.size());
        for (ModuleDto m : modules) {
            assertEquals(RunState.FINISHED_SUCCESSFULLY, m.getState());
            assertEquals(1, m.getPhases().size());
            final PhaseDto phase = m.getPhases().get(0);
            assertEquals(RunState.FINISHED_SUCCESSFULLY, phase.getState());

            final List<TaskDto> tasks = phase.getTasks();
            assertEquals(2, tasks.size());
            for (TaskDto t : tasks) {
                assertEquals(RunState.FINISHED_SUCCESSFULLY, t.getState());
            }
        }

        final List<PhaseDto> phases = dto.getPhases();
        assertEquals(2, phases.size());

        for (PhaseDto p : phases) {
            assertEquals(RunState.FINISHED_SUCCESSFULLY, p.getState());
            final List<TaskDto> tasks = p.getTasks();
            assertEquals(2, tasks.size());
            for (TaskDto t : tasks) {
                assertEquals(RunState.FINISHED_SUCCESSFULLY, t.getState());
            }
        }
    }

    @Test
    public void simulateFailingBuild() {
        registerBuild("10");
        registerPhase("10", "phase1", Object.class);
        registerTask("10", "phase1", "task1", Object.class);
        finishTask("10", "phase1", "task1", true, Object.class);
        registerTask("10", "phase1", "task2", Object.class);
        finishTask("10", "phase1", "task2", true, Object.class);
        finishPhase("10", "phase1", true, Object.class);
        registerPhase("10", "phase2", Object.class);
        registerTask("10", "phase2", "task1", Object.class);
        registerModule("10", "module1", Object.class);
        registerPhase("10", "module1", "phase1", Object.class);
        registerTask("10", "module1", "phase1", "task1", Object.class);
        finishTask("10", "module1", "phase1", "task1", true, Object.class);
        registerTask("10", "module1", "phase1", "task2", Object.class);
        finishTask("10", "module1", "phase1", "task2", true, Object.class);
        finishPhase("10", "module1", "phase1", true, Object.class);
        finishModule("10", "module1", true, Object.class);
        registerModule("10", "module2", Object.class);
        registerPhase("10", "module2", "phase1", Object.class);
        registerTask("10", "module2", "phase1", "task1", Object.class);
        finishTask("10", "module2", "phase1", "task1", true, Object.class);
        registerTask("10", "module2", "phase1", "task2", Object.class);
        finishTask("10", "module2", "phase1", "task2", true, Object.class);
        finishPhase("10", "module2", "phase1", true, Object.class);
        finishModule("10", "module2", true, Object.class);
        finishTask("10", "phase2", "task1", true, Object.class);
        registerTask("10", "phase2", "task2", Object.class);
        finishTask("10", "phase2", "task2", false, Object.class);
        finishPhase("10", "phase2", false, Object.class);
        finishBuild("10", false);

        final ResponseEntity<BuildDto> entity = getBuild("10");

        assertTrue(entity.getStatusCode().is2xxSuccessful());

        final BuildDto dto = entity.getBody();
        assertEquals(RunState.FINISHED_WITH_FAILURES, dto.getState());

        final List<ModuleDto> modules = dto.getModules();
        assertEquals(2, modules.size());
        for (ModuleDto m : modules) {
            assertEquals(RunState.FINISHED_SUCCESSFULLY, m.getState());
            assertEquals(1, m.getPhases().size());
            final PhaseDto phase = m.getPhases().get(0);
            assertEquals(RunState.FINISHED_SUCCESSFULLY, phase.getState());

            final List<TaskDto> tasks = phase.getTasks();
            assertEquals(2, tasks.size());
            for (TaskDto t : tasks) {
                assertEquals(RunState.FINISHED_SUCCESSFULLY, t.getState());
            }
        }

        final List<PhaseDto> phases = dto.getPhases();
        assertEquals(2, phases.size());

        final PhaseDto p = phases.get(0);
        assertEquals(RunState.FINISHED_SUCCESSFULLY, p.getState());
        final List<TaskDto> tasks = p.getTasks();
        assertEquals(2, tasks.size());
        for (TaskDto t : tasks) {
            assertEquals(RunState.FINISHED_SUCCESSFULLY, t.getState());
        }

        final PhaseDto p2 = phases.get(1);
        assertEquals(RunState.FINISHED_WITH_FAILURES, p2.getState());
        final List<TaskDto> tasks2 = p2.getTasks();
        assertEquals(2, tasks2.size());
        assertEquals(RunState.FINISHED_SUCCESSFULLY, tasks2.get(0).getState());
        assertEquals(RunState.FINISHED_WITH_FAILURES, tasks2.get(1).getState());
    }
}


