package entities;

//import commonInfra.Strategy.*;
import commonInfra.View;
import commonInfra.Strategy;
import commonInfra.Strategy.StrategyType;
import sharedRegions.*;

public class Coach extends Thread {
    /**
     * Coach ID
     */

    private int team;

    /**
     * Coach State
     */

    private int state;

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
     * Hist of Stratgies
     * //TODO: strat
     */
    private Strategy coachStrategy;

    /**
     * Name of the Thread
     * 
     * @return String
     */

    public String whoAmI() {
        return "Coach(" + team + ")";
    }

    /**
     * Set the coach state
     */
    public void setEntityState(int state) {
        this.state = state;
    }

    /**
     * Get the coach state
     */

    public int getEntityState() {
        return this.state;
    }

    /**
     * Set the coach team
     */
    public void setTeam(int team) {
        this.team = team;
    }

    /**
     * Get the coach team
     */

    public int getTeam() {
        return this.team;
    }

    /**
     * Coach instantiation
     * 
     * @param coachID
     */

    public Coach(int team, ContestantBench contestantBench, Playground playground, RefereeSite refereeSite,
            StrategyType coachStrategy) {
        super("Coach(" + team + ")");
        this.team = team;
        this.state = CoachState.WAIT_FOR_REFEREE_COMMAND;
        this.contestantBench = contestantBench;
        this.playground = playground;
        this.refereeSite = refereeSite;
        this.coachStrategy = new Strategy(coachStrategy);
    }

    /**
     * Coach life cycle
     * 
     * orders == 0 -> end of the match, the Coach Thread ends
     */
    @Override
    public void run() {
        System.out.println(this.whoAmI() + " has started.");

        /**
         * Start of Coach life cycle
         */
        int orders;

        // int[] selected = reviewNotes(); // TODO: fix reviewNotes doesn't do anything
        int[] selected = new int[] { 1, 2, 3 };
        System.out.println(this.whoAmI() + " -> reviewNotes()");

        while (true) {
            orders = contestantBench.waitForCallTrial(team);
            System.out.println(this.whoAmI() + " -> waitForCallTrial()");

            if (orders == 0) {
                return;
            }

            contestantBench.callContestants(team, selected);
            System.out.println(this.whoAmI() + " -> callContestants()");

            playground.waitForFollowCoachAdvice(team);
            System.out.println(this.whoAmI() + " -> waitForFollowCoachAdvice()");

            refereeSite.informReferee();
            System.out.println(this.whoAmI() + " -> informReferee()");

            playground.waitForAssertTrialDecision(team);
            System.out.println(this.whoAmI() + " -> waitForAssertTrialDecision()");

            selected = reviewNotes();
            System.out.println(this.whoAmI() + " -> reviewNotes()");
        }

        // reviewNotes();

        /**
         * selected = {1,2,3}
         * while(RefereeSite.referee.getState() != RefereeState.END_OF_THE_MATCH) {
         * contestantBench.callContestants(selected,teamID);
         * // TODO: waitForFollowCoachAdvice -> with while inside of it in the
         * playground
         * refereeSite.informReferee();
         * selected = reviewNotes();
         * }
         */

    }

    /**
     * Based on the information from the match defines which is the next strategy to
     * the team
     * 
     */
    public int[] reviewNotes() {
        View[] contestants = contestantBench.getBench(this.team);
        return coachStrategy.getStrategy().selectTeam(contestants);
    }

}
