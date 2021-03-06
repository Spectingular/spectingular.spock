package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Module;
import org.spectingular.spock.domain.State;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class ModuleService extends BaseService {
    @Resource
    private ModuleRepository moduleRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Module}s for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return modules The {@link org.spectingular.spock.domain.Module}s.
     */
    public List<Module> findByBuildNumber(final int buildNumber) throws IllegalArgumentException {
        return findBuild(buildNumber, build -> moduleRepository.findByBuild(build));
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return module The {@link org.spectingular.spock.domain.Module}
     */
    public Optional<Module> findByBuildNumberAndName(final int buildNumber, final String name) throws IllegalArgumentException {
        return findBuild(buildNumber, build -> moduleRepository.findByBuildAndName(build, name));
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Module}.
     * @param buildNumber The build number.
     * @param module       The {@link org.spectingular.spock.domain.Module}.
     * @throws IllegalArgumentException
     */
    public void register(final int buildNumber, final Module module) throws IllegalArgumentException {
        findBuild(buildNumber, build -> {
                    module.setBuild(build);
                    module.setState(new State());
                    moduleRepository.save(module);
                    return module;
                }
        );
    }

    /**
     * Update the {@link org.spectingular.spock.domain.Module}.
     * @param buildNumber The build number.
     * @param moduleName   The module name.
     * @param state       The {@link org.spectingular.spock.domain.State}.
     * @throws IllegalArgumentException
     */
    public void update(final int buildNumber, final String moduleName, final State state) throws IllegalArgumentException {
        findByBuildNumberAndName(buildNumber, moduleName).
                map(o -> {
                    final Module found = o;
                    found.getState().setStopDate(new Date());
                    found.getState().setSuccess(state.isSuccess());
                    moduleRepository.save(found);
                    return found;
                }).
                orElseThrow(() -> new IllegalArgumentException(format("Module with name [%s] for build with number [%d] cannot be found", moduleName, buildNumber)));
    }

    /**
     * Gets all the {@link org.spectingular.spock.domain.Build}s for the {@link org.spectingular.spock.domain.Module} matching the given name.
     * @param moduleName The name.
     * @return builds The {@link org.spectingular.spock.domain.Build}s.
     */
    public List<Build> findBuildsByModuleName(final String moduleName) throws IllegalArgumentException {
        return moduleRepository.findByName(moduleName).stream().map(module -> module.getBuild()).collect(Collectors.toList());
    }
}
