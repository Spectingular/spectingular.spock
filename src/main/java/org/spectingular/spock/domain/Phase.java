package org.spectingular.spock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * Phase represents a lifecycle phase in a build system.
 */
@CompoundIndexes({
        @CompoundIndex(name = "phase_build", unique = true, def = "{'name': 1, 'build': 1}"),
        @CompoundIndex(name = "phase_module", unique = true, def = "{'name': 1, 'module': 1}")
})
@Document(collection = "phases")
public class Phase {
    @Id
    private ObjectId id;
    @NotNull
    private String name;
    private State state;
    @DBRef
    @JsonIgnore
    private Build build;
    @DBRef
    @JsonIgnore
    private Module module;

    
    /**
     * Gets the name.
     * @return name The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the state.
     * @return state The state.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state.
     * @param state The state.
     */
    public void setState(final State state) {
        this.state = state;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Build}.
     * @return build The {@link org.spectingular.spock.domain.Build}.
     */
    public Build getBuild() {
        return build;
    }

    /**
     * Sets the {@link org.spectingular.spock.domain.Build}.
     * @param build The {@link org.spectingular.spock.domain.Build}.
     */
    public void setBuild(final Build build) {
        this.build = build;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Module}. 
     * @return module The {@link org.spectingular.spock.domain.Module}.
     */
    public Module getModule() {
        return module;
    }

    /**
     * Sets the {@link org.spectingular.spock.domain.Module}.
     * @param module The {@link org.spectingular.spock.domain.Module}.
     */
    public void setModule(final Module module) {
        this.module = module;
    }
}
