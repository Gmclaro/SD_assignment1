package GameOfRope.entities;

import GameOfRope.sharedRegions.*;

public class Coach extends Thread {
    /**
     * Coach ID
     */

    private int coachId;

    /**
     * Coach State
     */

    private int coachState;

    /**
     * Coach team id
     */

    private int teamId;

    /**
     * Reference to the Contestants Bench
     */

    private ContestantBench contestantBench;

    /**
     * Reference to the Playground
     */

    private Playground playground;

    /**
     * Reference to the Referee Site
     */

    private RefereeSite refereeSite;

    /**
     * Set the coach state
     */

    public void setCoachState(int state) {
        this.coachState = state;
    }

    /**
     * Get the coach state
     */

    public int getCoachState() {
        return this.coachState;
    }

    /**
     * Get the coach ID
     */

    public int getcoachId() {
        return this.coachId;
    }

    /**
     * Get the coach team
     */

    public int getcoachTeamID() {
        return this.teamId;
    }

    /**
     * Coach instantiation
     * 
     * @param coachID
     */

    public Coach(int coachId, int teamId, ContestantBench contestantBench, Playground playground, RefereeSite refereeSite) {
        super("Coach_" + coachId + "T" + teamId);
        this.coachId = coachId;
        this.coachState = CoachState.WAIT_FOR_REFEREE_COMMAND;
        this.contestantBench = contestantBench;
        this.playground = playground;
        this.refereeSite = refereeSite;
    }

    /**
     * Coach life cycle
     */
    @Override
    public void run() {
        /**
         * while(!endOfTheMatch()) {
         *  callContestants();
         *  informReferee();
         *  reviewNotes();
         * }
         */
    }



}
