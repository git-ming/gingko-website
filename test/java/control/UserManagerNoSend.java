package control;

import testTool.Counter;

/**
 * Created by xlo on 2015/8/25.
 * it's not send return
 */
public class UserManagerNoSend extends UserManager {
    private ManagerNoSend managerNoSend;

    public UserManagerNoSend(Counter counter) {
        super(null);
        managerNoSend = new ManagerNoSend(counter);
        this.sendManager = managerNoSend;
    }

    public ManagerNoSend getManagerNoSend() {
        return managerNoSend;
    }
}
