package control;

import testTool.Counter;

/**
 * Created by xlo on 2015/9/2.
 * it's the blog manager who not send reply
 */
public class BlogManagerNoSend extends BlogManager {
    private ManagerNoSend managerNoSend;

    public BlogManagerNoSend(Counter counter) {
        super(null);
        managerNoSend = new ManagerNoSend(counter);
        this.sendManager = managerNoSend;
    }

    public ManagerNoSend getManagerNoSend() {
        return managerNoSend;
    }
}
