package control;

import testTool.Counter;

/**
 * Created by xlo on 2015/11/6.
 * it's the zan manager no send
 */
public class ZanManagerNoSend extends ZanManager {
    private ManagerNoSend managerNoSend;

    public ZanManagerNoSend(Counter counter) {
        super(null);
        managerNoSend = new ManagerNoSend(counter);
        this.sendManager = managerNoSend;
    }

    public ManagerNoSend getManagerNoSend() {
        return managerNoSend;
    }
}
