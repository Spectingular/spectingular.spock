package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.exceptions.NotFoundException;

import javax.annotation.Resource;
import javax.validation.constraints.Null;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Abstract service.
 */
public class AbstractService {
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
     * @throws org.spectingular.spock.exceptions.NotFoundException
     */
    protected <T> T findBuild(final int buildNumber, final Function<Build, T> fn) throws NotFoundException {
        return buildRepository.findByNumber(buildNumber)
                .map(o -> fn.apply(o))
                .orElseThrow(() -> new NotFoundException(format("Build with number [%d] cannot be found", buildNumber)));
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param fn          The {@link java.util.function.Function}.
     * @param <T>         The result.
     * @return result The result.
     * @throws org.spectingular.spock.exceptions.NotFoundException
     */
    protected <T> T findPhase(final int buildNumber, final String phaseName, final Function<Phase, T> fn) throws NotFoundException {
        return findBuild(buildNumber, (Function<Build, T>) build -> {
            return phaseRepository.findByBuildAndName(build, phaseName)
                    .map(o -> fn.apply(o))
                    .orElseThrow(() -> new NotFoundException(format("Phase with name [%s] for build with number [%d] cannot be found", phaseName, build.getNumber())));
        });
    }
}
