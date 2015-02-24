package org.spectingular.spock.api.dto;

/** Run state represents the state of an action. */
public enum RunState {
    IN_PROGRESS,
    FINISHED_SUCCESSFULLY,
    FINISHED_WITH_FAILURES,
    IDLE
}
