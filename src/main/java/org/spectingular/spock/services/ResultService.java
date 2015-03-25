package org.spectingular.spock.services;

import org.spectingular.spock.domain.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class ResultService extends BaseService {
    @Resource
    private ResultRepository resultRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Result}s for the {@link org.spectingular.spock.domain.Task} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return result The {@link org.spectingular.spock.domain.Result}.
     */
    public Optional<Result> findByBuildNumberAndPhaseNameAndTaskName(final int buildNumber, final String phaseName, final String taskName) throws IllegalArgumentException {
        return findTask(buildNumber, phaseName, taskName, resultRepository::findByTask);
    }

    /**
     * Gets all the {@link org.spectingular.spock.domain.Result}s for the {@link org.spectingular.spock.domain.Task} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return result The {@link org.spectingular.spock.domain.Result}.
     */
    public Optional<Result> findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(final int buildNumber, final String moduleName, final String phaseName, final String taskName) throws IllegalArgumentException {
        return findTask(buildNumber, moduleName, phaseName, taskName, resultRepository::findByTask);
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Result}.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param result      The {@link org.spectingular.spock.domain.Result}.
     * @throws IllegalArgumentException
     */
    public void store(final int buildNumber, final String phaseName, final String taskName, final Result result) throws IllegalArgumentException {
        findTask(buildNumber, phaseName, taskName, task -> {
            result.setTask(task);
            resultRepository.save(result);
            return result;
        });
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Result}.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param moduleName  The module name.
     * @param taskName    The task name.
     * @param result      The {@link org.spectingular.spock.domain.Result}.
     * @throws IllegalArgumentException
     */
    public void store(final int buildNumber, final String moduleName, final String phaseName, final String taskName, final Result result) throws IllegalArgumentException {
        findTask(buildNumber, moduleName, phaseName, taskName, task -> {
            result.setTask(task);
            resultRepository.save(result);
            return result;
        });
    }

}
