package org.spectingular.spock.services;

import org.spectingular.spock.domain.*;
import org.spectingular.spock.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.*;
import static javax.ws.rs.core.Response.status;

@Service
public final class PhaseService {
    @Resource
    private PhaseRepository phaseRepository;
    @Resource
    private BuildRepository buildRepository;

    /**
     * Gets all the {@link org.spectingular.spock.domain.Phase}s for the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @return phases The {@link org.spectingular.spock.domain.Phase}s.
     */
    public List<Phase> findByBuildNumber(final int buildNumber) throws NotFoundException {
        return findBuild(buildNumber, build -> phaseRepository.findByBuild(build));
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return phase The {@link org.spectingular.spock.domain.Phase}
     */
    public Optional<Phase> findByBuildNumberAndName(final int buildNumber, final String name) throws NotFoundException {
        return findBuild(buildNumber, build -> phaseRepository.findByBuildAndName(build, name));
    }

    /**
     * Register the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param phase       The {@link org.spectingular.spock.domain.Phase}.
     * @throws NotFoundException
     */
    public void registerPhase(final int buildNumber, final Phase phase) throws NotFoundException {
        findBuild(buildNumber, new FindCallback<Void, Build>() {
            @Override
            public Void find(final Build build) {
                phase.setBuild(build);
                phase.setState(new State());
                phaseRepository.save(phase);
                return null;
            }
        });
    }

    /**
     * Update the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param phaseName   The {@link org.spectingular.spock.domain.Phase} name.
     * @param state       The {@link org.spectingular.spock.domain.State}.
     * @throws NotFoundException
     */
    public void updatePhase(final int buildNumber, final String phaseName, final State state) throws NotFoundException {
        Optional<Phase> o = findByBuildNumberAndName(buildNumber, phaseName);
        if (o.isPresent()) {
            final Phase found = o.get();
            found.getState().setStopDate(new Date());
            found.getState().setSuccess(state.isSuccess());
            phaseRepository.save(found);
        } else {
            throw new NotFoundException(format("Phase with name [%s] for build with number [%d] cannot be found", phaseName, buildNumber));
        }
    }

    /**
     * Find the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param buildNumber The build number.
     * @param callback    The callback.
     * @param <T>         The result.
     * @return result The result.
     * @throws org.spectingular.spock.exceptions.NotFoundException
     */
    private <T> T findBuild(final int buildNumber, final FindCallback<T, Build> callback) throws NotFoundException {
        final Optional<Build> o = buildRepository.findByNumber(buildNumber);
        if (o.isPresent()) {
            return callback.find(o.get());
        } else {
            throw new NotFoundException(format("Build with number [%d] cannot be found", buildNumber));
        }
    }
}
