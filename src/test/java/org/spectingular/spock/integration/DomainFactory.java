package org.spectingular.spock.integration;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.spectingular.spock.domain.*;

import java.util.Date;

/** Factory for creating domain objects. */
public final class DomainFactory {
    public static Build build(final String number) {
        final Build build = new Build();
        build.setNumber(Integer.parseInt(number));
        return build;
    }

    public static State state(final boolean success) {
        final State state = new State();
        state.setSuccess(success);
        state.setStopDate(new Date());
        return state;
    }

    public static Module module(final String name) {
        final Module module = new Module();
        module.setName(name);
        return module;
    }

    public static Phase phase(final String name) {
        final Phase phase = new Phase();
        phase.setName(name);
        return phase;
    }

    public static Task task(final String name) {
        final Task task = new Task();
        task.setName(name);
        return task;
    }

    public static Result result(final String data) {
        final Result result = new Result();
        result.setData((DBObject) JSON.parse(data));
        return result;
    }
}
