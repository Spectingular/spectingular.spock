package org.spectingular.spock.dto;

import org.spectingular.spock.domain.State;

import java.util.Date;

import static org.spectingular.spock.dto.RunState.get;

/** Task dto. */
public class TaskDto {
    private String name;
    private Date startDate;
    private Date stopDate;
    private RunState state;

    /** Default constructor. */
    public TaskDto() {
    }

    /**
     * Constructor.
     * @param name  The name.
     * @param state The {@link org.spectingular.spock.domain.State}.
     */
    public TaskDto(final String name, final State state) {
        this.name = name;
        this.startDate = state.getStartDate();
        this.stopDate = state.getStopDate();
        this.state = get(state);
    }

    /**
     * Gets the name.
     * @return name The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the start date.
     * @return startDate The start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets the stop date.
     * @return stopDate The stop date.
     */
    public Date getStopDate() {
        return stopDate;
    }

    /**
     * Gets the {@link org.spectingular.spock.dto.RunState}.
     * @return runState The {@link org.spectingular.spock.dto.RunState}.
     */
    public RunState getState() {
        return state;
    }
}
