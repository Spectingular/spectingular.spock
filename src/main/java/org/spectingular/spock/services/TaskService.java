package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public final class TaskService {
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private PhaseRepository phaseRepository;
    @Resource
    private BuildRepository buildRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return phases The {@link org.spectingular.spock.domain.Phase}s.
     */
    public List<Task> findByBuildNumberAndPhaseName(final int buildNumber, final String phaseName) {
        final Optional<Build> ob = buildRepository.findByNumber(buildNumber);
        final List<Task> tasks;
        final Build build = ob.get();
        if (ob.isPresent()) {
            final Optional<Phase> op = phaseRepository.findByBuildAndName(build, phaseName);
            Phase phase = op.get();
            tasks = taskRepository.findByPhase(phase);
        } else {
            tasks = null;
        }

        return tasks;
    }

    /**
     * Save the {@link org.spectingular.spock.domain.Task}.
     * @param task The {@link org.spectingular.spock.domain.Task}.
     */
    public void persist(final Task task) {
        taskRepository.save(task);
    }
}
