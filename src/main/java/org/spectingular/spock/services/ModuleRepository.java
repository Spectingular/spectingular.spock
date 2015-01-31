package org.spectingular.spock.services;


import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Module;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting {@link org.spectingular.spock.domain.Module}s.
 */
public interface ModuleRepository extends Repository<Module, String> {
    /**
     * Gets the {@link org.spectingular.spock.domain.Module} for the {@link org.spectingular.spock.domain.Build} matching the given parameters.
     * @param build The {@link org.spectingular.spock.domain.Build}
     * @param name  The name.
     * @return module The {@link org.spectingular.spock.domain.Module}.
     */
    Optional<Module> findByBuildAndName(Build build, String name);
    
    /**
     * Gets the {@link org.spectingular.spock.domain.Module}s for the given {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}
     * @return modules The {@link org.spectingular.spock.domain.Module}s.
     */
    List<Module> findByBuild(Build build);

    /**
     * Persists the {@link org.spectingular.spock.domain.Module}.
     * @return module The {@link org.spectingular.spock.domain.Module}.
     */
    Module save(Module module);
}

