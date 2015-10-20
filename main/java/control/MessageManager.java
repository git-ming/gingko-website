package control;

import model.event.SendEvent;
import net.server.serverSolver.RequestSolver;

/**
 * Created by xlo on 2015/8/31.
 * it's the message manager
 */
public class MessageManager extends Manager {
    public MessageManager(RequestSolver requestSolver) {
        super(requestSolver);
    }

    public void addMessage(String username, String password, String message, String aimUser, String preview) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, message, aimUser, preview);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void getMessage(String username, String password, String id) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, id, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }

    public void readMessage(String username, String password, String id) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, id);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void readAllMessage(String username, String password, String... id) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                if (!accessConfig.isAccept(username, password, this)) {
                    return false;
                }
                for (String now : id) {
                    if (!(boolean) ManagerLogic.invoke(this.getClojureName(), username, password, now)) {
                        return false;
                    }
                }
                return true;
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void getAllMessage(String username, String password) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, username, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }

    public void getUserAllMessage(String username, String password, String aim) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, aim, sendManager, this);
            }
        };
        sendManager.addFailMessage(sendEvent);
        sendEvent.submit();
    }

    public void removeMessage(String username, String password, String id) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this)
                        && (boolean) ManagerLogic.invoke(this.getClojureName(), username, password, id);
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }

    public void removeAllMessage(String username,String password, String... id) {
        SendEvent sendEvent = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                if (!accessConfig.isAccept(username, password, this)) {
                    return false;
                }
                for (String now : id) {
                    if (!(boolean) ManagerLogic.invoke(this.getClojureName(), username, password, now)) {
                        return false;
                    }
                }
                return true;
            }
        };
        sendManager.addSendMessage(sendEvent);
        sendEvent.submit();
    }
}
