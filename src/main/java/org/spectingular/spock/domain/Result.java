package org.spectingular.spock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * Result represents a result of an executed task from a lifecycle phase.
 */
@CompoundIndexes({
        @CompoundIndex(name = "result_task", unique = true, def = "{'name': 1, 'task': 1}")
})
@Document(collection = "results")
public class Result {
    @Id
    private ObjectId id;
    @NotNull
    private DBObject data;
    @DBRef
    @JsonIgnore
    private Task task;

    /**
     * Gets the data.
     * @return data The data.
     */
    public DBObject getData() {
        return data;
    }

    /**
     * Sets the data.
     * @param data The data.
     */
    public void setData(final DBObject data) {
        this.data = data;
    }

    /**
     * Gets the {@link org.spectingular.spock.domain.Task}.
     * @return task the {@link org.spectingular.spock.domain.Task}.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Sets the {@link org.spectingular.spock.domain.Task}.
     * @param task The {@link org.spectingular.spock.domain.Task}.
     */
    public void setTask(final Task task) {
        this.task = task;
    }
}
