package org.spectingular.spock.services;

import org.spectingular.spock.domain.State;
import org.spectingular.spock.domain.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class TaskService extends BaseService {
    @Resource
    private TaskRepository taskRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Task}s for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return tasks The {@link org.spectingular.spock.domain.Task}s.
     */
    public List<Task> findByBuildNumberAndPhaseName(final int buildNumber, final String phaseName) throws IllegalArgumentException {
        return findPhase(buildNumber, phaseName, phase -> taskRepository.findByPhase(phase));
    }

    /**
     * Gets all the {@link org.spectingular.spock.domain.Task}s for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return tasks The {@link org.spectingular.spock.domain.Task}s.
     */
    public List<Task> findByBuildNumberAndModuleNameAndPhaseName(final int buildNumber, final String moduleName, final String phaseName) throws IllegalArgumentException {
        return findPhase(buildNumber, moduleName, phaseName, phase -> taskRepository.findByPhase(phase));
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return task The {@link org.spectingular.spock.domain.Task}
     */
    public Optional<Task> findByBuildNumberAndPhaseNameAndName(final int buildNumber, final String phaseName, final String taskName) throws IllegalArgumentException {
        return findPhase(buildNumber, phaseName, phase -> taskRepository.findByPhaseAndName(phase, taskName));
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return task The {@link org.spectingular.spock.domain.Task}
     */
    public Optional<Task> findByBuildNumberAndModuleNameAndPhaseNameAndName(final int buildNumber, final String moduleName, final String phaseName, final String taskName) throws IllegalArgumentException {
        return findPhase(buildNumber, moduleName, phaseName, phase -> taskRepository.findByPhaseAndName(phase, taskName));
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param task        The {@link org.spectingular.spock.domain.Task}.
     * @throws IllegalArgumentException
     */
    public void register(final int buildNumber, final String phaseName, final Task task) throws IllegalArgumentException {
        findPhase(buildNumber, phaseName, phase -> {
            task.setPhase(phase);
            task.setState(new State());
            taskRepository.save(task);
            return task;
        });
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param task        The {@link org.spectingular.spock.domain.Task}.
     * @throws IllegalArgumentException
     */
    public void register(final int buildNumber, final String moduleName, final String phaseName, final Task task) throws IllegalArgumentException {
        findPhase(buildNumber, moduleName, phaseName, phase -> {
            task.setPhase(phase);
            task.setState(new State());
            taskRepository.save(task);
            return task;
        });
    }

    /**
     * Update the {@link org.spectingular.spock.domain.Task}.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param state       The {@link org.spectingular.spock.domain.State}.
     * @throws IllegalArgumentException
     */
    public void update(final int buildNumber, final String phaseName, final String taskName, final State state) throws IllegalArgumentException {
        findByBuildNumberAndPhaseNameAndName(buildNumber, phaseName, taskName).map(o -> {
            final Task found = o;
            found.getState().setStopDate(new Date());
            found.getState().setSuccess(state.isSuccess());
            taskRepository.save(found);
            return found;
        }).orElseThrow(() -> new IllegalArgumentException(format("Task with name [%s] for phase with name [%s] and build with number [%d] cannot be found", taskName, phaseName, buildNumber)));

    }

    /**
     * Update the {@link org.spectingular.spock.domain.Task}.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param state       The {@link org.spectingular.spock.domain.State}.
     * @throws IllegalArgumentException
     */
    public void update(final int buildNumber, final String moduleName, final String phaseName, final String taskName, final State state) throws IllegalArgumentException {
        findByBuildNumberAndModuleNameAndPhaseNameAndName(buildNumber, moduleName, phaseName, taskName).map(o -> {
            final Task found = o;
            found.getState().setStopDate(new Date());
            found.getState().setSuccess(state.isSuccess());
            taskRepository.save(found);
            return found;
        }).orElseThrow(() -> new IllegalArgumentException(format("Task with name [%s] for phase with name [%s] and module with name [%s] and build with number [%d] cannot be found", taskName, phaseName, moduleName, buildNumber)));

    }
}
