package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    public List<Phase> findByBuildNumber(final int buildNumber) {
        final Optional<Build> o = buildRepository.findByNumber(buildNumber);
        return o.isPresent() ? phaseRepository.findByBuild(o.get()) : null;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param buildNumber The build number.
     * @param name        The name.
     * @return phase The {@link org.spectingular.spock.domain.Phase}
     */
    public Optional<Phase> findByBuildNumberAndName(final int buildNumber, final String name) {
        final Optional<Build> o = buildRepository.findByNumber(buildNumber);
        return o.isPresent() ? phaseRepository.findByBuildAndName(o.get(), name) : null;
    }

    /**
     * Save the {@link org.spectingular.spock.domain.Phase}.
     * @param phase The {@link org.spectingular.spock.domain.Phase}.
     */
    public void persist(final Phase phase) {
        phaseRepository.save(phase);
    }
}
