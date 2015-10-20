package control;

import model.event.SendEvent;
import net.server.serverSolver.RequestSolver;

/**
 * Created by xlo on 2015/8/21.
 * it's the user access manager
 */
public class UserManager extends Manager {

    public UserManager(RequestSolver requestSolver) {
        super(requestSolver);
    }

    public void loginUser(String username, String password) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return (boolean) ManagerLogic.invoke(this.getClojureName(), username, password);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void register(String username, String password) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return (boolean) ManagerLogic.invoke(this.getClojureName(), username, password);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void userExist(String username) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return (boolean) ManagerLogic.invoke(this.getClojureName(), username, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }

    public void removeUser(String username, String password) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void changeUserAccess(String username, String password, String aimUsername, String accessType, int access) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, aimUsername, accessType, access);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void setMotto(String username, String password, String motto) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, motto);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void getMotto(String username) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return (boolean) ManagerLogic.invoke(this.getClojureName(), username, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }
}
