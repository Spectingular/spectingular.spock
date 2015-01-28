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
public final class TaskService extends AbstractService {
    @Resource
    private TaskRepository taskRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Task}s for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return phases The {@link org.spectingular.spock.domain.Phase}s.
     */
    public List<Task> findByBuildNumberAndPhaseName(final int buildNumber, final String phaseName) throws IllegalArgumentException {
        return findPhase(buildNumber, phaseName, phase -> taskRepository.findByPhase(phase));
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return phase The {@link org.spectingular.spock.domain.Phase}
     */
    public Optional<Task> findByBuildNumberPhaseNameAndName(final int buildNumber, final String phaseName, final String taskName) throws IllegalArgumentException {
        return findPhase(buildNumber, phaseName, phase -> taskRepository.findByPhaseAndName(phase, taskName));
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param task        The {@link org.spectingular.spock.domain.Task}.
     * @throws IllegalArgumentException
     */
    public void registerTask(final int buildNumber, final String phaseName, final Task task) throws IllegalArgumentException {
        findPhase(buildNumber, phaseName, phase -> {
            task.setPhase(phase);
            task.setState(new State());
            taskRepository.save(task);
            return null;
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
    public void updateTask(final int buildNumber, final String phaseName, final String taskName, final State state) throws IllegalArgumentException {
        Optional<Task> o = findByBuildNumberPhaseNameAndName(buildNumber, phaseName, taskName);
        if (o.isPresent()) {
            final Task found = o.get();
            found.getState().setStopDate(new Date());
            found.getState().setSuccess(state.isSuccess());
            taskRepository.save(found);
        } else {
            throw new IllegalArgumentException(format("Task with name [%s] for phase with name [%s] and build with number [%d] cannot be found", taskName, phaseName, buildNumber));
        }
    }
}
