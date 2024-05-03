package serverSide.sharedRegions;

import clientSide.entities.*;
import commonInfra.*;
import serverSide.entities.*;
import serverSide.main.SimulParse;

public class ContestantBenchInterface {

    //TODO: remove this
    public static int nReq = 0;

    private final ContestantBench contestantBench;

    public ContestantBenchInterface(ContestantBench contestantBench) {
        this.contestantBench = contestantBench;
    }

    public Message processAndReply(Message inMessage) throws MessageException {
        Message outMessage = null;

        System.out.println("inMessage:\n" + inMessage.toString());

        /* Validate messages */

        switch (inMessage.getMsgType()) {
            case MessageType.REQ_CALL_TRIAL:
                if (inMessage.getEntityState() < RefereeState.START_OF_THE_MATCH
                        || inMessage.getEntityState() > RefereeState.END_OF_THE_MATCH) {
                    throw new MessageException("Invalid Referee state!", inMessage);
                }
                break;
            case MessageType.REQ_SEAT_DOWN:
                if ((inMessage.getTeam() < 0) || (inMessage.getTeam() > SimulParse.COACH)) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (inMessage.getID() < 0 || inMessage.getID() > SimulParse.CONTESTANT_PER_TEAM) {
                    throw new MessageException("Invalid number of id !", inMessage);
                } else if (inMessage.getEntityState() < ContestantState.SEAT_AT_THE_BENCH
                        || inMessage.getEntityState() > ContestantState.DO_YOUR_BEST) {
                    throw new MessageException("Invalid number of state !", inMessage);
                }
                break;
            case MessageType.REQ_REVIEW_NOTES:
                if ((inMessage.getTeam() < 1) || (inMessage.getTeam() > SimulParse.COACH)) {
                    throw new MessageException("Invalid number of team !", inMessage);
                } else if (((ContestantBenchClientProxy) Thread.currentThread()).getCoachTeam() != inMessage
                        .getTeam()) {
                    throw new MessageException("Invalid team!", inMessage);
                }
                break;
            default:
                throw new MessageException("Invalid message type!", inMessage);

        }

        /* Process Messages */
        int team, id;

        switch (inMessage.getMsgType()) {
            case MessageType.REQ_CALL_TRIAL:
                ((ContestantBenchClientProxy) Thread.currentThread()).setRefereeState(inMessage.getEntityState());
                contestantBench.callTrial();
                outMessage = new Message(MessageType.REP_CALL_TRIAL,
                        ((ContestantBenchClientProxy) Thread.currentThread()).getRefereeState());
                break;

            case MessageType.REQ_SEAT_DOWN:
                team = inMessage.getTeam();
                id = inMessage.getID();
                ((ContestantBenchClientProxy) Thread.currentThread()).setContestantTeam(team);
                ((ContestantBenchClientProxy) Thread.currentThread()).setId(id);
                ((ContestantBenchClientProxy) Thread.currentThread()).setStrength(inMessage.getStrength());
                ((ContestantBenchClientProxy) Thread.currentThread()).setContestantState(inMessage.getEntityState());

                contestantBench.seatDown(team, id);

                outMessage = new Message(MessageType.REP_SEAT_DOWN, team, id,
                        ((ContestantBenchClientProxy) Thread.currentThread()).getContestantState());
                break;

            case MessageType.REQ_REVIEW_NOTES:
                team = ((ContestantBenchClientProxy) Thread.currentThread()).getCoachTeam();

                contestantBench.reviewNotes(team);
                outMessage = new Message(MessageType.REP_REVIEW_NOTES, team);

                break;
            default:
                throw new MessageException("Invalid message type!", inMessage);
        }

        System.out.println("\nCBI processAndReply: " + outMessage.toString());
        return (outMessage);
    }
}