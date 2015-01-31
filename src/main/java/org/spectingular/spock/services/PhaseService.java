package org.spectingular.spock.services;

import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class PhaseService extends BaseService {
    @Resource
    private PhaseRepository phaseRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return phases The {@link org.spectingular.spock.domain.Phase}s.
     */
    public List<Phase> findByBuildNumber(final int buildNumber) throws IllegalArgumentException {
        return findBuild(buildNumber, build -> phaseRepository.findByBuild(build));
    }

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return phases The {@link org.spectingular.spock.domain.Phase}s.
     */
    public List<Phase> findByBuildNumberAndModuleName(final int buildNumber, final String moduleName) throws IllegalArgumentException {
        return findModule(buildNumber, moduleName, module -> phaseRepository.findByModule(module));
    }


    /**
     * Gets the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return phase The {@link org.spectingular.spock.domain.Phase}
     */
    public Optional<Phase> findByBuildNumberAndName(final int buildNumber, final String name) throws IllegalArgumentException {
        return findBuild(buildNumber, build -> phaseRepository.findByBuildAndName(build, name));
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param phase       The {@link org.spectingular.spock.domain.Phase}.
     * @throws IllegalArgumentException
     */
    public void registerPhase(final int buildNumber, final Phase phase) throws IllegalArgumentException {
        findBuild(buildNumber, build -> {
                    phase.setBuild(build);
                    phase.setState(new State());
                    phaseRepository.save(phase);
                    return phase;
                }
        );
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param moduleName  the module name.
     * @param phase       The {@link org.spectingular.spock.domain.Phase}.
     * @throws IllegalArgumentException
     */
    public void registerPhase(final int buildNumber, final String moduleName, final Phase phase) throws IllegalArgumentException {
        findModule(buildNumber, moduleName, module -> {
                    phase.setModule(module);
                    phase.setState(new State());
                    phaseRepository.save(phase);
                    return phase;
                }
        );
    }

    /**
     * Update the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param state       The {@link org.spectingular.spock.domain.State}.
     * @throws IllegalArgumentException
     */
    public void updatePhase(final int buildNumber, final String phaseName, final State state) throws IllegalArgumentException {
        findByBuildNumberAndName(buildNumber, phaseName).
                map(o -> {
                    final Phase found = o;
                    found.getState().setStopDate(new Date());
                    found.getState().setSuccess(state.isSuccess());
                    phaseRepository.save(found);
                    return found;
                }).
                orElseThrow(() -> new IllegalArgumentException(format("Phase with name [%s] for build with number [%d] cannot be found", phaseName, buildNumber)));
    }
}
