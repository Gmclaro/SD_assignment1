package serverSide.sharedRegions;

import clientSide.entities.RefereeState;
import commonInfra.*;
import serverSide.entities.*;

public class RefereeSiteInterface {

    // TODO: javadoc

    private final RefereeSite refereeSite;

    public RefereeSiteInterface(RefereeSite refereeSite) {
        this.refereeSite = refereeSite;
    }

    public Message processAndReply(Message inMessage) throws MessageException {
        Message outMessage = null;

        switch (inMessage.getMsgType()) {
            case MessageType.REQ_ANNOUNCE_NEW_GAME:
                if ((inMessage.getEntityState() < RefereeState.START_OF_THE_MATCH)
                        || (inMessage.getEntityState() > RefereeState.END_OF_THE_MATCH)) {
                    throw new MessageException("Invalid Referee state!", inMessage);
                }
                break;
            default:
                throw new MessageException("DEFAULT!", inMessage);
        }

        switch (inMessage.getMsgType()) {
            case MessageType.REQ_ANNOUNCE_NEW_GAME:
                ((RefereeSiteClientProxy) Thread.currentThread()).setRefereeState(inMessage.getEntityState());
                refereeSite.announceNewGame();
                outMessage = new Message(MessageType.REP_ANNOUNCE_NEW_GAME, RefereeState.START_OF_A_GAME);

                break;

            default:
                break;
        }
        return (outMessage);
    }

}
