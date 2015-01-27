package org.spectingular.spock.services;


import org.spectingular.spock.domain.Build;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting {@link org.spectingular.spock.domain.Build}s.
 */
public interface BuildRepository extends Repository<Build, String> {
    /**
     * Gets the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param number The number.
     * @return Build The {@link org.spectingular.spock.domain.Build}.
     */
    Optional<Build> findByNumber(int number);

    /**
     * Gets all the {@link org.spectingular.spock.domain.Build}s.
     * @return Builds The {@link org.spectingular.spock.domain.Build}s.
     */
    List<Build> findAll();

    /**
     * Persists the {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}.
     */
    void save(Build build);
}

