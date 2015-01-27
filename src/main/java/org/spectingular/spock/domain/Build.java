package org.spectingular.spock.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * Build represents an instance of a build system.
 */
@Document(collection = "builds")
public class Build {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(unique = true)
    private int number;

    /**
     * Gets the number.
     * @return number the number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the number.
     * @param number The number.
     */
    public void setNumber(final int number) {
        this.number = number;
    }
}
