package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Service
public class BuildService {
    @Resource
    private BuildRepository repository;

    /**
     * Gets the {@link org.spectingular.spock.domain.Build} matching the given build number.
     * @param number The build number.
     * @return build The {@link org.spectingular.spock.domain.Build}.
     */
    public Optional<Build> findByNumber(final int number) {
        return repository.findByNumber(number).map(Optional::ofNullable).orElse(empty());
    }

    /**
     * Get all the {@link org.spectingular.spock.domain.Build}s.
     * @return builds The list of {@link org.spectingular.spock.domain.Build}s.
     */
    public List<Build> findAll() {
        return repository.findAll();
    }

    /**
     * Save the {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}.
     */
    public void persist(final Build build) {
        repository.save(build);
    }
}
