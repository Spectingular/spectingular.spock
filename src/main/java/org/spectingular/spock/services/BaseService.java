package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Module;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.Task;

import javax.annotation.Resource;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * Base service.
 */
public class BaseService {
    @Resource
    private BuildRepository buildRepository;
    @Resource
    private PhaseRepository phaseRepository;
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private ModuleRepository moduleRepository;

    /**
     * Find the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws IllegalArgumentException
     */
    protected <T> T findBuild(final int buildNumber, final Function<Build, T> fn) throws IllegalArgumentException {
        return buildRepository.findByNumber(buildNumber)
                .map(fn::apply)
                .orElseThrow(() -> new IllegalArgumentException(format("Build with number [%d] cannot be found", buildNumber)));
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws IllegalArgumentException
     */
    protected <T> T findModule(final int buildNumber, final String moduleName, final Function<Module, T> fn) throws IllegalArgumentException {
        return findBuild(buildNumber, (Function<Build, T>) build ->
                moduleRepository.findByBuildAndName(build, moduleName)
                        .map(fn::apply)
                        .orElseThrow(() -> new IllegalArgumentException(format("Module with name [%s] for build with number [%d] cannot be found", moduleName, buildNumber))));
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws IllegalArgumentException
     */
    protected <T> T findPhase(final int buildNumber, final String phaseName, final Function<Phase, T> fn) throws IllegalArgumentException {
        return findBuild(buildNumber, (Function<Build, T>) build -> phaseRepository.findByBuildAndName(build, phaseName)
                .map(fn::apply)
                .orElseThrow(() -> new IllegalArgumentException(format("Phase with name [%s] for build with number [%d] cannot be found", phaseName, buildNumber))));
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws IllegalArgumentException
     */
    protected <T> T findPhase(final int buildNumber, final String moduleName, final String phaseName, final Function<Phase, T> fn) throws IllegalArgumentException {
        return findModule(buildNumber, moduleName, (Function<Module, T>) module ->
                phaseRepository.findByModuleAndName(module, phaseName)
                        .map(fn::apply)
                        .orElseThrow(() -> new IllegalArgumentException(format("Phase with name [%s] for module with name [%s] and build with number [%d] cannot be found", phaseName, moduleName, buildNumber))));
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws IllegalArgumentException
     */
    protected <T> T findTask(final int buildNumber, final String phaseName, final String taskName, final Function<Task, T> fn) throws IllegalArgumentException {
        return findPhase(buildNumber, phaseName, (Function<Phase, T>) phase -> taskRepository.findByPhaseAndName(phase, taskName)
                .map(fn::apply)
                .orElseThrow(() -> new IllegalArgumentException(format("Task with name [%s] for phase with name [%s] and build with number [%d] cannot be found", taskName, phaseName, buildNumber))));
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws IllegalArgumentException
     */
    protected <T> T findTask(final int buildNumber, final String moduleName, final String phaseName, final String taskName, final Function<Task, T> fn) throws IllegalArgumentException {
        return findPhase(buildNumber, moduleName, phaseName, (Function<Phase, T>) phase -> taskRepository.findByPhaseAndName(phase, taskName)
                .map(fn::apply)
                .orElseThrow(() -> new IllegalArgumentException(format("Task with name [%s] for phase with name [%s] and module with name [%s]and build with number [%d] cannot be found", taskName, phaseName, moduleName, buildNumber))));
    }
}
