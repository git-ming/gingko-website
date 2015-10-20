package control;

import model.event.SendEvent;
import net.server.serverSolver.RequestSolver;

/**
 * Created by xlo on 2015/9/16.
 * it's the image manager
 */
public class ImageManager extends Manager {
    public ImageManager(RequestSolver requestSolver) {
        super(requestSolver);
    }

    public void checkUploadAccess(String username, String password) {
        SendEvent event = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this);
            }
        };
        sendManager.addSendMessage(event);
        event.submit();
    }

    public void uploadImage(String username, String password, String type, byte[] image) {
        SendEvent event = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return accessConfig.isAccept(username, password, this) &&
                        (boolean) ManagerLogic.invoke(this.getClojureName(), type, image, sendManager, this);
            }
        };
        sendManager.addFailMessage(event);
        event.submit();
    }

    public void getImage(String fileName) {
        SendEvent event = new SendEvent() {
            @Override
            public boolean run() throws Exception {
                return (boolean) ManagerLogic.invoke(this.getClojureName(), fileName, sendManager, this);
            }
        };
        sendManager.addFailMessage(event);
        event.submit();
    }
}
