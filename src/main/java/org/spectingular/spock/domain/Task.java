package org.spectingular.spock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * Task represents an execution task from a lifecycle phase.
 */
@Document(collection = "tasks")
public class Task {
    @Id
    private ObjectId id;
    @NotNull
    private String name;
    @DBRef
    @JsonIgnore
    private Phase phase;
    private State state;

    /**
     * Gets the name.
     * @return name The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Phase}.
     * @return phase the {@link org.spectingular.spock.domain.Phase}.
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Sets the {@link org.spectingular.spock.domain.Phase}.
     * @param phase The {@link org.spectingular.spock.domain.Phase}.
     */
    public void setPhase(final Phase phase) {
        this.phase = phase;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.State}.
     * @return state The {@link org.spectingular.spock.domain.State}.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the {@link org.spectingular.spock.domain.State}
     * @param state The {@link org.spectingular.spock.domain.State}.
     */
    public void setState(final State state) {
        this.state = state;
    }
}
