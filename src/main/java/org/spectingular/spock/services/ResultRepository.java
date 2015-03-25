package org.spectingular.spock.services;


import org.spectingular.spock.domain.Result;
import org.spectingular.spock.domain.Task;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Repository for persisting {@link org.spectingular.spock.domain.Result}s.
 */
interface ResultRepository extends Repository<Result, String> {

    /**
     * Gets the {@link org.spectingular.spock.domain.Result} for the given {@link org.spectingular.spock.domain.Task}.
     * @param task The {@link org.spectingular.spock.domain.Task}.
     * @return result The {@link org.spectingular.spock.domain.Result}.
     */
    Optional<Result> findByTask(Task task);

    /**
     * Persists the {@link org.spectingular.spock.domain.Result}.
     * @return result The {@link org.spectingular.spock.domain.Result}.
     */
    Result save(Result result);


}

