package org.spectingular.spock.services;

import org.spectingular.spock.domain.Build;
import org.spectingular.spock.domain.Phase;
import org.spectingular.spock.domain.State;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;

@Service
public class BuildService extends BaseService{
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
     * Register the {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}.
     */
    public void register(final Build build) {
        build.setState(new State());
        repository.save(build);
    }

    /**
     * Update the {@link org.spectingular.spock.domain.Phase}.
     * @param buildNumber The build number.
     * @param state       The {@link org.spectingular.spock.domain.State}.
     * @throws IllegalArgumentException
     */
    public void update(final int buildNumber, final State state) throws IllegalArgumentException {
        findBuild(buildNumber, build -> {
            build.getState().setStopDate(new Date());
            build.getState().setSuccess(state.isSuccess());
            repository.save(build);
            return build;
        });
    }

}
