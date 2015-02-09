package org.spectingular.spock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * Module represents an instance of a module that is build by the build system.
 */
@CompoundIndexes({
        @CompoundIndex(name = "module_build", unique = true, def = "{'name': 1, 'build': 1}")
})
@Document(collection = "modules")
public class Module {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(unique = true)
    private String name;
    private State state;
    @DBRef
    @JsonIgnore
    private Build build;


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
     * Gets the build.
     * @return build The {@link Build}.
     */
    public Build getBuild() {
        return build;
    }

    /**
     * Sets the {@link Build}.
     * @param build The {@link Build}.
     */
    public void setBuild(final Build build) {
        this.build = build;
    }


}
