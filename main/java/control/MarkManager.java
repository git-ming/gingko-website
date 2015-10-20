package control;

import model.event.SendEvent;
import net.server.serverSolver.RequestSolver;

/**
 * Created by xlo on 2015/9/9.
 * it's the manager of mark user
 */
public class MarkManager extends Manager {
    public MarkManager(RequestSolver requestSolver) {
        super(requestSolver);
    }

    public void markUser(String username, String password, String aimUser) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, aimUser);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void unMarkUser(String username, String password, String aimUser) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, aimUser);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void isMarked(String username, String password, String aimUser) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, aimUser, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }

    public void getMarkedList(String username, String password) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), "from", username, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }

    public void getMarkedMeList(String username, String password) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), "to", username, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }
}
