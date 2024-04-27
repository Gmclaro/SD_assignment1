package serverSide.sharedRegions;

import serverSide.main.SimulParse;
import serverSide.entities.*;
import clientSide.entities.*;
import clientSide.stubs.*;

/**
 * Referee Site
 * Shared Memory Region
 */

public class RefereeSite {

    /**
     * Reference to the General Repository
     */
    private final GeneralRepositoryStub repo;

    /**
     * Number of teams ready
     */

    private int teamsReady;

    /**
     * Referee Site Constructor
     * 
     * @param repo General Repository
     */

    public RefereeSite(GeneralRepositoryStub repo) {
        this.repo = repo;
        teamsReady = 0;
    }

    /**
     * The referee announces a new game
     */
    public synchronized void announceNewGame() {
        repo.newGameStarted();

        ((RefereeSiteClientProxy) Thread.currentThread()).setRefereeState(RefereeState.START_OF_A_GAME);
        repo.setRefereeState(RefereeState.START_OF_A_GAME);
    }

    /**
     * The referee waits for the coaches to inform the teams are ready
     */

    public synchronized void informReferee() {
        teamsReady++;
        notifyAll();
    }

    /**
     * The referee waits for the coaches to inform the teams are ready
     */
    public synchronized void waitForInformReferee() {
        while (teamsReady < 2) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();

        /**
         * Reset the number of teams ready
         */
        teamsReady = 0;
    }
}