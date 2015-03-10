package org.spectingular.spock.dto;

import org.spectingular.spock.domain.State;

import java.util.Date;
import java.util.List;

import static org.spectingular.spock.dto.RunState.get;

/** Build dto. */
public class BuildDto {
    private int number;
    private Date startDate;
    private Date stopDate;
    private RunState state;

    private List<ModuleDto> modules;
    private List<PhaseDto> phases;

    /**
     * Constructor.
     * @param number The number.
     * @param state  The state.
     */
    public BuildDto(final int number, final State state) {
        this.number = number;
        this.startDate = state.getStartDate();
        this.stopDate = state.getStopDate();
        this.state = get(state);
    }

    /**
     * Gets the number.
     * @return number The number.
     */
    public int getNumber() {
        return number;
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

    /**
     * Sets the {@link org.spectingular.spock.dto.ModuleDto}s.
     * @param modules The {@link org.spectingular.spock.dto.ModuleDto}s.
     */
    public void setModules(final List<ModuleDto> modules) {
        this.modules = modules;
    }

    /**
     * Gets the modules.
     * @return modules The list of {@link org.spectingular.spock.dto.ModuleDto}s.
     */
    public List<ModuleDto> getModules() {
        return modules;
    }

    /**
     * Sets the {@link org.spectingular.spock.dto.PhaseDto}s
     * @param phases The {@link org.spectingular.spock.dto.PhaseDto}s.
     */
    public void setPhases(final List<PhaseDto> phases) {
        this.phases = phases;
    }

    /**
     * Gets the phases.
     * @return phases The list of {@link org.spectingular.spock.dto.PhaseDto}s.
     */
    public List<PhaseDto> getPhases() {
        return phases;
    }
}
