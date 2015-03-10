package org.spectingular.spock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.spectingular.spock.dto.RunState;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static org.spectingular.spock.dto.RunState.*;

/**
 * State represents the state of something.
 */
public class State {
    @DateTimeFormat(pattern = "YYYY-MM-dd hh:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "YYYY-MM-dd hh:mm:ss")
    private Date stopDate;
    private boolean success;

    /**
     * Default constructor.
     */
    public State() {
        this.startDate = new Date();
    }

    /**
     * Gets the start date.
     * @return date The start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets the stop date.
     * @return date The stop date.
     */
    public Date getStopDate() {
        return stopDate;
    }

    /**
     * Sets the stop date.
     * @param stopDate The stop date.
     */
    public void setStopDate(final Date stopDate) {
        this.stopDate = stopDate;
    }

    /**
     * Indicator success.
     * @return true if successful, else false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the indicator success.
     * @param success Indicator success.
     */
    public void setSuccess(final boolean success) {
        this.success = success;
    }
}
