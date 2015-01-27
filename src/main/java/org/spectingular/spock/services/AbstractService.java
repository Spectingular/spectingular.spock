package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.exceptions.NotFoundException;

import javax.annotation.Resource;
import java.util.Optional;

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
     * @param callback    The callback.
     * @param <T>         The result.
     * @return result The result.
     * @throws org.spectingular.spock.exceptions.NotFoundException
     */
    protected <T> T findBuild(final int buildNumber, final FindCallback<T, Build> callback) throws NotFoundException {
        final Optional<Build> o = buildRepository.findByNumber(buildNumber);
        if (o.isPresent()) {
            return callback.find(o.get());
        } else {
            throw new NotFoundException(format("Build with number [%d] cannot be found", buildNumber));
        }
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param phaseName   The phase name.
     * @param callback    The callback.
     * @param <T>         The result.
     * @return result The result.
     * @throws org.spectingular.spock.exceptions.NotFoundException
     */
    protected <T> T findPhase(final int buildNumber, final String phaseName, final FindCallback<T, Phase> callback) throws NotFoundException {
        return findBuild(buildNumber, new FindCallback<T, Build>() {
            @Override
            public T find(Build build) throws NotFoundException {
                final Optional<Phase> o = phaseRepository.findByBuildAndName(build, phaseName);
                if (o.isPresent()) {
                    return callback.find(o.get());
                } else {
                    throw new NotFoundException(format("Phase with name [%s] for build with number [%d] cannot be found", phaseName, build.getNumber()));
                }
            }
        });
    }
}
