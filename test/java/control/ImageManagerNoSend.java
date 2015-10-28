package control;

import testTool.Counter;

/**
 * Created by xlo on 2015/9/16.
 * it's the image manager no send
 */
public class ImageManagerNoSend extends ImageManager {
    private ManagerNoSend managerNoSend;

    public ImageManagerNoSend(Counter counter) {
        super(null);
        managerNoSend = new ManagerNoSend(counter);
        this.sendManager = managerNoSend;
    }

    public ManagerNoSend getManagerNoSend() {
        return managerNoSend;
    }
}
