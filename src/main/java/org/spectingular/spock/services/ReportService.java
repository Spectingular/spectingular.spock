package org.spectingular.spock.services;

import org.spectingular.spock.dto.BuildDto;
import org.spectingular.spock.dto.ModuleDto;
import org.spectingular.spock.dto.PhaseDto;
import org.spectingular.spock.dto.TaskDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dto service
 */
@Service
public class ReportService {
    @Resource
    private BuildService buildService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private PhaseService phaseService;
    @Resource
    private TaskService taskService;
    @Resource
    private ResultService resultService;

    /**
     * Gets all the {@link org.spectingular.spock.dto.BuildDto}s.
     * @return builds The {@link org.spectingular.spock.dto.BuildDto}s.
     */
    public List<BuildDto> findBuilds() throws IllegalArgumentException {
        return buildService.findAll().stream().map(build -> new BuildDto(build.getNumber(), build.getState())).collect(Collectors.toList());
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.BuildDto} matching the given build number.
     * @param buildNumber The build number.
     * @return build The {@link org.spectingular.spock.dto.BuildDto}.
     */
    public Optional<BuildDto> findBuild(final int buildNumber) {
        return buildService.findByNumber(buildNumber).map(build -> {
            final BuildDto dto = new BuildDto(buildNumber, build.getState());
            dto.setModules(findModulesByBuildNumber(buildNumber));
            dto.setPhases(findPhasesByBuildNumber(buildNumber));
            return dto;
        });
    }

    /**
     * Gets all the {@link org.spectingular.spock.dto.ModuleDto}s for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @return modules The {@link org.spectingular.spock.dto.ModuleDto}s.
     */
    public List<ModuleDto> findModulesByBuildNumber(final int buildNumber) throws IllegalArgumentException {
        return moduleService.findByBuildNumber(buildNumber).stream().map(module -> {
            final ModuleDto dto = new ModuleDto(module.getName(), module.getState());
            dto.setPhases(findPhasesByBuildNumberAndModuleName(buildNumber, module.getName()));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.ModuleDto} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return module The {@link org.spectingular.spock.dto.ModuleDto}
     */
    public Optional<ModuleDto> findModuleByBuildNumberAndName(final int buildNumber, final String name) throws IllegalArgumentException {
        return moduleService.findByBuildNumberAndName(buildNumber, name).map(module -> {
            final ModuleDto dto = new ModuleDto(name, module.getState());
            dto.setPhases(findPhasesByBuildNumberAndModuleName(buildNumber, module.getName()));
            return dto;
        });
    }

    /**
     * Gets all the {@link org.spectingular.spock.dto.PhaseDto}s for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @return phases The {@link org.spectingular.spock.dto.PhaseDto}s.
     */
    public List<PhaseDto> findPhasesByBuildNumber(final int buildNumber) throws IllegalArgumentException {
        return phaseService.findByBuildNumber(buildNumber).stream().map(phase -> {
            final PhaseDto dto = new PhaseDto(phase.getName(), phase.getState());
            dto.setTasks(findTasksByBuildNumberAndPhaseName(buildNumber, phase.getName()));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.PhaseDto} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return phase The {@link org.spectingular.spock.dto.PhaseDto}
     */
    public Optional<PhaseDto> findPhaseByBuildNumberAndName(final int buildNumber, final String name) throws IllegalArgumentException {
        return phaseService.findByBuildNumberAndName(buildNumber, name).map(phase -> {
            final PhaseDto dto = new PhaseDto(name, phase.getState());
            dto.setTasks(findTasksByBuildNumberAndPhaseName(buildNumber, phase.getName()));
            return dto;
        });
    }

    /**
     * Gets all the {@link org.spectingular.spock.dto.PhaseDto}s for the {@link org.spectingular.spock.domain.Module} matching the given parameters.
     * @param buildNumber The build number.
     * @return phases The {@link org.spectingular.spock.dto.PhaseDto}s.
     */
    public List<PhaseDto> findPhasesByBuildNumberAndModuleName(final int buildNumber, final String moduleName) throws IllegalArgumentException {
        return phaseService.findByBuildNumberAndModuleName(buildNumber, moduleName).stream().map(phase -> {
            final PhaseDto dto = new PhaseDto(phase.getName(), phase.getState());
            dto.setTasks(findTasksByBuildNumberAndModuleNameAndPhaseName(buildNumber, moduleName, phase.getName()));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.PhaseDto} for the {@link org.spectingular.spock.domain.Module} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return phase The {@link org.spectingular.spock.dto.PhaseDto}.
     */
    public Optional<PhaseDto> findPhaseByBuildNumberAndModuleNameAndName(final int buildNumber, final String moduleName, final String name) throws IllegalArgumentException {
        return phaseService.findByBuildNumberAndModuleNameAndName(buildNumber, moduleName, name).map(phase -> {
            final PhaseDto dto = new PhaseDto(phase.getName(), phase.getState());
            dto.setTasks(findTasksByBuildNumberAndModuleNameAndPhaseName(buildNumber, moduleName, phase.getName()));
            return dto;
        });
    }

    /**
     * Gets all the {@link org.spectingular.spock.dto.TaskDto}s for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return tasks The {@link org.spectingular.spock.dto.TaskDto}s.
     */
    public List<TaskDto> findTasksByBuildNumberAndPhaseName(final int buildNumber, final String phaseName) throws IllegalArgumentException {
        return taskService.findByBuildNumberAndPhaseName(buildNumber, phaseName).stream().map(task -> new TaskDto(task.getName(), task.getState())).collect(Collectors.toList());
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.TaskDto} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @return task The {@link org.spectingular.spock.dto.TaskDto}
     */
    public Optional<TaskDto> findTaskByBuildNumberAndPhaseNameAndName(final int buildNumber, final String phaseName, final String taskName) throws IllegalArgumentException {
        return taskService.findByBuildNumberAndPhaseNameAndName(buildNumber, phaseName, taskName).map(task -> new TaskDto(taskName, task.getState()));
    }

    /**
     * Gets all the {@link org.spectingular.spock.dto.TaskDto}s for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return tasks The {@link org.spectingular.spock.dto.TaskDto}s.
     */
    public List<TaskDto> findTasksByBuildNumberAndModuleNameAndPhaseName(final int buildNumber, final String moduleName, final String phaseName) {
        return taskService.findByBuildNumberAndModuleNameAndPhaseName(buildNumber, moduleName, phaseName).stream().map(task -> new TaskDto(task.getName(), task.getState())).collect(Collectors.toList());
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.TaskDto} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @return task The {@link org.spectingular.spock.dto.TaskDto}.
     */
    public Optional<TaskDto> findTaskByBuildNumberAndModuleNameAndPhaseNameAndName(final int buildNumber, final String moduleName, final String phaseName, final String taskName) throws IllegalArgumentException {
        return taskService.findByBuildNumberAndModuleNameAndPhaseNameAndName(buildNumber, moduleName, phaseName, taskName).map(task -> new TaskDto(taskName, task.getState()));
    }

    /**
     * Gets the result data for the {@link org.spectingular.spock.domain.Task} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return data The data.
     */
    public Optional<String> findResultByBuildNumberAndPhaseNameAndTaskName(final int buildNumber, final String phaseName, final String taskName) throws IllegalArgumentException {
        return resultService.findByBuildNumberAndPhaseNameAndTaskName(buildNumber, phaseName, taskName).map(result -> result.getData().toString());
    }


    /**
     * Gets the result data for the {@link org.spectingular.spock.domain.Task} matching the given parameters.
     * @param buildNumber The build number.
     * @param moduleName  The module name.
     * @param phaseName   The phase name.
     * @param taskName    The task name.
     * @return data The data.
     */
    public Optional<String> findResultByBuildNumberAndModuleNameAndPhaseNameAndTaskName(final int buildNumber, final String moduleName, final String phaseName, final String taskName) throws IllegalArgumentException {
        return resultService.findByBuildNumberAndModuleNameAndPhaseNameAndTaskName(buildNumber, moduleName, phaseName, taskName).map(result -> result.getData().toString());
    }


}
