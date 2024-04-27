package clientSide.stubs;

import commonInfra.ClientCom;
import commonInfra.Message;
import commonInfra.MessageType;

public class GeneralRepositoryStub {
  /**
   * Name of the plataform where is located the general repo server
   */
  private String serverHostName;

  /**
   * Port number for listening to service requests
   */
  private int serverPortNumb;

  /**
   * Instantiation of a general repo stub.
   *
   * @param serverHostName name of the platform where is located the general repo
   *                       server
   * @param serverPortNumb port number for listening to service requests
   */
  public GeneralRepositoryStub(String serverHostName, int serverPortNumb) {
    this.serverHostName = serverHostName;
    this.serverPortNumb = serverPortNumb;
  }

  
  public void setRefereeState(int state) {
    ClientCom com;
    Message outMessage, inMessage;

    com = new ClientCom(serverHostName, serverPortNumb);

    while (!com.open()) {
      try {
        Thread.currentThread().sleep((long) (10));
      } catch (InterruptedException e) {
      }
    }

    outMessage = new Message(MessageType.REQ_LOG_SET_REFEREE_STATE,state);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if (inMessage.getMsgType() != MessageType.REP_LOG_SET_REFEREE_STATE){
      System.out.println("Thread " + Thread.currentThread().getName() + ":Type error in setRefereeState()");
      System.out.println(inMessage.toString());
      System.exit(1);
    }
  }

  // TODO : Missing set Entity states



  // TOOD: newGameStated
  public void newGameStarted() {
    ClientCom com;
    Message outMessage, inMessage;

    com = new ClientCom(serverHostName, serverPortNumb);

    while (!com.open()) {
      try {
        Thread.currentThread().sleep((long) (10));
      } catch (InterruptedException e) {
      }
    }

    outMessage = new Message(MessageType.REQ_NEW_GAME_STARTED);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if (inMessage.getMsgType() != MessageType.REP_NEW_GAME_STARTED) {
      System.out.println("Thread " + Thread.currentThread().getName() + ":Type error in newGameStarted()");
      System.out.println(inMessage.toString());
      System.exit(1);
    }

    com.close();
  }

  // TODO: setNewTrial
  public void setNewTrial() {

  }

  // TODO: setActiveContestant
  public void setActiveContestant(int team, int id) {

  }

  // TODO: setRopePosition
  public void setRopePosition(int ropePosition) {

  }

  // TODO : setEndOfGame
  public void setEndOfGame() {

  }

  // TODO: showGameResult
  public void showGameResult(int ropePosition) {

  }

  // TODO: setMatchWinner
  public void setMatchWinner(int[] scores) {

  }

  public void shutdown() {
    ClientCom com;
    Message inMessage,outMessage;

    com = new ClientCom(serverHostName, serverPortNumb);

    while(!com.open()) {
        try {
            Thread.currentThread ().sleep ((long) (10));
        }
        catch (InterruptedException e) {}
    }

    outMessage = new Message(MessageType.REQ_GENERAL_REPOSITORY_SHUTDOWN);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if(inMessage.getMsgType() != MessageType.REP_GENERAL_REPOSITORY_SHUTDOWN){
        System.out.println("Thread "+ Thread.currentThread().getName()+ ":Invalid message type!");
        System.out.println(inMessage.toString());
        System.exit(1);
    }

    com.close();
}

}