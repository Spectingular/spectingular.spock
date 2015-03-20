package org.spectingular.spock.services;


import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.Task;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting {@link org.spectingular.spock.domain.Task}s.
 */
interface TaskRepository extends Repository<Task, String> {

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the {@link org.spectingular.spock.domain.Phase} matching the given parameters.
     * @param phase The {@link org.spectingular.spock.domain.Phase}.
     * @param name  The name.
     * @return task The {@link org.spectingular.spock.domain.Task}.
     */
    Optional<Task> findByPhaseAndName(Phase phase, String name);

    /**
     * Gets the {@link org.spectingular.spock.domain.Task} for the given {@link org.spectingular.spock.domain.Phase}.
     * @param phase The {@link org.spectingular.spock.domain.Phase}.
     * @return task The {@link org.spectingular.spock.domain.Task}.
     */
    List<Task> findByPhase(Phase phase);

    /**
     * Persists the {@link org.spectingular.spock.domain.Task}.
     * @return task The {@link org.spectingular.spock.domain.Task}.
     */
    Task save(Task task);


}

