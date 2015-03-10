package org.spectingular.spock.dto;

import org.spectingular.spock.domain.State;

/** Run state represents the state of an action. */
public enum RunState {
    IN_PROGRESS,
    FINISHED_SUCCESSFULLY,
    FINISHED_WITH_FAILURES,
    IDLE;

    /**
     * Get the {@link org.spectingular.spock.dto.RunState} based on the given parameters.
     * @param state The {@link org.spectingular.spock.domain.State}.
     * @return runState The {@link org.spectingular.spock.dto.RunState}.
     */
    static RunState get(final State state) {
        return state.getStopDate() != null ? (state.isSuccess() ? FINISHED_SUCCESSFULLY : FINISHED_WITH_FAILURES) : IN_PROGRESS;
    }
}
