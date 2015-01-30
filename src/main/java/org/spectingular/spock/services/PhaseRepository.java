package org.spectingular.spock.services;


import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository for persisting {@link org.spectingular.spock.domain.Phase}s.
 */
public interface PhaseRepository extends Repository<Phase, String> {
    /**
     * Gets the {@link org.spectingular.spock.domain.Phase} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param build The {@link org.spectingular.spock.domain.Build}
     * @param name  The name.
     * @return phase The {@link org.spectingular.spock.domain.Phase}.
     */
    Optional<Phase> findByBuildAndName(Build build, String name);

    /**
     * Gets the {@link org.spectingular.spock.domain.Phase}s for the given {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}
     * @return phases The {@link org.spectingular.spock.domain.Phase}s.
     */
    List<Phase> findByBuild(Build build);

    /**
     * Persists the {@link org.spectingular.spock.domain.Phase}.
     * @return phase The {@link org.spectingular.spock.domain.Phase}.
     */
    Phase save(Phase phase);


}

