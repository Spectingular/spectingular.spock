package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;

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
                .map(o -> fn.apply(o))
                .orElseThrow(() -> new IllegalArgumentException(format("Build with number [%d] cannot be found", buildNumber)));
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
        return findBuild(buildNumber, (Function<Build, T>) build -> {
            return phaseRepository.findByBuildAndName(build, phaseName)
                    .map(o -> fn.apply(o))
                    .orElseThrow(() -> new IllegalArgumentException(format("Phase with name [%s] for build with number [%d] cannot be found", phaseName, build.getNumber())));
        });
    }
}