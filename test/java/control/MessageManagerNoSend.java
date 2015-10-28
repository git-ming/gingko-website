package control;

import testTool.Counter;

/**
 * Created by xlo on 2015/9/11.
 * it's the message manager who not send reply
 */
public class MessageManagerNoSend extends MessageManager {
    private ManagerNoSend managerNoSend;

    public MessageManagerNoSend(Counter counter) {
        super(null);
        managerNoSend = new ManagerNoSend(counter);
        this.sendManager = managerNoSend;
    }

    public ManagerNoSend getManagerNoSend() {
        return managerNoSend;
    }
}
