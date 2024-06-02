package serverSide.objects;

import clientSide.entities.CoachState;
import clientSide.entities.ContestantState;
import clientSide.entities.RefereeState;
import commonInfra.Message;
import commonInfra.MessageException;
import commonInfra.MessageType;
import serverSide.entities.PlaygroundClientProxy;
import serverSide.main.SimulParse;



/**
 *   Interface to the Playground 
 *   It is responsible to validate and process the incoming message, execute the corresponding method on the
 *   Playground e and generate the outgoing message.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */

public class PlaygroundInterface {

    /**
     *  Reference to the Playground
     */
    private final Playground playground;

    /**
     *   Instantiation of an interface to the Playground.
     *
     *     @param playground Reference to the Playground
     */
    public PlaygroundInterface(Playground playground) {
        this.playground = playground;
    }

    /**
     *   Processing of the incoming messages
     *   Validation, execution of the corresponding method and generation of the outgoing message.
     *
     * 	   @param inMessage service request
     * 	   @return service reply
     * 	   @throws MessageException if incoming message was not valid
     */
    public Message processAndReply(Message inMessage) throws MessageException {
        Message outMessage = null;

        /* Validate messages */

        switch (inMessage.getMsgType()) {
            case MessageType.REQ_FOLLOW_COACH_ADVICE:
                if ((inMessage.getTeam() < 0) || (inMessage.getTeam() > SimulParse.COACH)) {
                    throw new MessageException("Invalid number of team !", inMessage);
                }
                break;

            case MessageType.REQ_WAIT_FOR_FOLLOW_COACH_ADVICE:
                if ((inMessage.getTeam() < 0) || (inMessage.getTeam() > SimulParse.COACH)) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (inMessage.getEntityState() < CoachState.WAIT_FOR_REFEREE_COMMAND
                        || inMessage.getEntityState() > CoachState.WATCH_TRIAL) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_START_TRIAL:
                if (inMessage.getEntityState() < RefereeState.START_OF_THE_MATCH
                        || inMessage.getEntityState() > RefereeState.END_OF_THE_MATCH) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_WAIT_FOR_START_TRIAL:

                if (inMessage.getTeam() < 0 || inMessage.getTeam() > SimulParse.COACH) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (inMessage.getID() < 0 || inMessage.getID() > SimulParse.CONTESTANT_PER_TEAM) {
                    throw new MessageException("Invalid number of id !", inMessage);
                } else if (inMessage.getEntityState() < ContestantState.SEAT_AT_THE_BENCH
                        || inMessage.getEntityState() > ContestantState.DO_YOUR_BEST) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_GET_READY:
                if (inMessage.getTeam() < 0 || inMessage.getTeam() > SimulParse.COACH) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (inMessage.getID() < 0 || inMessage.getID() > SimulParse.CONTESTANT_PER_TEAM) {
                    throw new MessageException("Invalid number of id !", inMessage);
                }
                break;
            case MessageType.REQ_WAIT_FOR_AM_DONE:
                // No validation needed
                break;
            case MessageType.REQ_ASSERT_TRIAL_DECISION:
                // No validation needed
                break;

            case MessageType.REQ_WAIT_FOR_ASSERT_TRIAL_DECISION_CONTESTANT:
                if (inMessage.getTeam() < 0 || inMessage.getTeam() > SimulParse.COACH) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (inMessage.getID() < 0 || inMessage.getID() > SimulParse.CONTESTANT_PER_TEAM) {
                    throw new MessageException("Invalid number of id !", inMessage);
                } else if (inMessage.getEntityState() < ContestantState.SEAT_AT_THE_BENCH
                        || inMessage.getEntityState() > ContestantState.DO_YOUR_BEST) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_WAIT_FOR_ASSERT_TRIAL_DECISION_COACH:
                if (inMessage.getTeam() < 0 || inMessage.getTeam() > SimulParse.COACH) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (inMessage.getEntityState() < CoachState.WAIT_FOR_REFEREE_COMMAND
                        || inMessage.getEntityState() > CoachState.WATCH_TRIAL) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_DECLARE_GAME_WINNER:
                if (inMessage.getEntityState() < RefereeState.START_OF_THE_MATCH
                        || inMessage.getEntityState() > RefereeState.END_OF_THE_MATCH) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_PLAYGROUND_SHUTDOWN:
                // No validation parameters
                break;

            default:
                throw new MessageException("Invalid message type!", inMessage);

        }

        /* Process Messages */
        int team, id, ropePosition;
        boolean continueGame;

        switch (inMessage.getMsgType()) {
            case MessageType.REQ_FOLLOW_COACH_ADVICE:
                team = inMessage.getTeam();

                playground.followCoachAdvice(team);

                outMessage = new Message(MessageType.REP_FOLLOW_COACH_ADVICE, team);
                break;

            case MessageType.REQ_WAIT_FOR_FOLLOW_COACH_ADVICE:
                team = inMessage.getTeam();

                ((PlaygroundClientProxy) Thread.currentThread()).setCoachState(inMessage.getEntityState());

                playground.waitForFollowCoachAdvice(team);

                outMessage = new Message(MessageType.REP_WAIT_FOR_FOLLOW_COACH_ADVICE, team,
                        ((PlaygroundClientProxy) Thread.currentThread()).getCoachState());
                break;
            case MessageType.REQ_START_TRIAL:

                ((PlaygroundClientProxy) Thread.currentThread()).setRefereeState(inMessage.getEntityState());

                playground.startTrial();

                outMessage = new Message(MessageType.REP_START_TRIAL,
                        ((PlaygroundClientProxy) Thread.currentThread()).getRefereeState());
                break;

            case MessageType.REQ_WAIT_FOR_START_TRIAL:

                team = inMessage.getTeam();
                id = inMessage.getID();

                ((PlaygroundClientProxy) Thread.currentThread()).setContestantState(inMessage.getEntityState());

                playground.waitForStartTrial(team, id);

                outMessage = new Message(MessageType.REP_WAIT_FOR_START_TRIAL, team, id,
                        ((PlaygroundClientProxy) Thread.currentThread()).getContestantState());
                break;
            case MessageType.REQ_GET_READY:

                team = inMessage.getTeam();
                id = inMessage.getID();

                playground.getReady(team, id);

                outMessage = new Message(MessageType.REP_GET_READY, team, id);
                break;
            case MessageType.REQ_WAIT_FOR_AM_DONE:

                playground.waitForAmDone();

                outMessage = new Message(MessageType.REP_WAIT_FOR_AM_DONE);
                break;

            case MessageType.REQ_ASSERT_TRIAL_DECISION:

                continueGame = playground.assertTrialDecision();

                outMessage = new Message(MessageType.REP_ASSERT_TRIAL_DECISION, continueGame);
                break;
            case MessageType.REQ_WAIT_FOR_ASSERT_TRIAL_DECISION_CONTESTANT:

                team = inMessage.getTeam();
                id = inMessage.getID();

                ((PlaygroundClientProxy) Thread.currentThread()).setContestantTeam(team);
                ((PlaygroundClientProxy) Thread.currentThread()).setID(id);
                ((PlaygroundClientProxy) Thread.currentThread()).setStrength(inMessage.getStrength());
                ((PlaygroundClientProxy) Thread.currentThread()).setContestantState(inMessage.getEntityState());

                playground.waitForAssertTrialDecision(team, id, inMessage.getStrength());

                outMessage = new Message(MessageType.REP_WAIT_FOR_ASSERT_TRIAL_DECISION_CONTESTANT, team, id,
                        ((PlaygroundClientProxy) Thread.currentThread()).getStrength(),
                        ((PlaygroundClientProxy) Thread.currentThread()).getContestantState());
                break;

            case MessageType.REQ_WAIT_FOR_ASSERT_TRIAL_DECISION_COACH:

                team = inMessage.getTeam();

                ((PlaygroundClientProxy) Thread.currentThread()).setCoachTeam(team);
                ((PlaygroundClientProxy) Thread.currentThread()).setContestantState(inMessage.getEntityState());

                playground.waitForAssertTrialDecision(team);

                outMessage = new Message(MessageType.REP_WAIT_FOR_ASSERT_TRIAL_DECISION_COACH, team,
                        ((PlaygroundClientProxy) Thread.currentThread()).getCoachState());
                break;
            case MessageType.REQ_DECLARE_GAME_WINNER:

                ((PlaygroundClientProxy) Thread.currentThread()).setRefereeState(inMessage.getEntityState());

                ropePosition = playground.declareGameWinner();

                outMessage = new Message(MessageType.REP_DECLARE_GAME_WINNER,
                        ((PlaygroundClientProxy) Thread.currentThread()).getRefereeState(), ropePosition);
                break;
            case MessageType.REQ_PLAYGROUND_SHUTDOWN:
                playground.shutdown();
                outMessage = new Message(MessageType.REP_PLAYGROUND_SHUTDOWN);
                break;
            default:
                throw new MessageException("Invalid message type!", inMessage);
        }

        System.out.println("\nPI processAndReply: " + outMessage.toString());
        return (outMessage);
    }
}