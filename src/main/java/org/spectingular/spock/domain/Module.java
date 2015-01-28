package org.spectingular.spock.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * Module represents an instance of a module that is build by the build system.
 */
@Document(collection = "modules")
public class Module {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(unique = true)
    private int name;

   
}
