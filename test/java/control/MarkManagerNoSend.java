package control;

import testTool.Counter;

/**
 * Created by xlo on 2015/9/11.
 * it's the mark manager who not send reply
 */
public class MarkManagerNoSend extends MarkManager {
    private ManagerNoSend managerNoSend;

    public MarkManagerNoSend(Counter counter) {
        super(null);
        managerNoSend = new ManagerNoSend(counter);
        this.sendManager = managerNoSend;
    }

    public ManagerNoSend getManagerNoSend() {
        return managerNoSend;
    }
}
