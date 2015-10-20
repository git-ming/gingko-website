package net.get;

import control.ImageManager;
import net.tool.LengthLimitReadServerSolver;

import java.net.URLDecoder;

/**
 * Created by xlo on 15-9-26.
 * it's the solver to give image in db
 */
public class DBImageSolver extends LengthLimitReadServerSolver {

    @Override
    protected boolean checkRequestVisitable() {
        this.length = 0;
        return true;
    }

    @Override
    public void solveMessage() throws Exception {
        String path = URLDecoder.decode(requestSolver.getRequestHeadReader().getUrl().getPath(), "UTF-8");
        path = path.substring(path.lastIndexOf('/') + 1);
        ImageManager imageManager = new ImageManager(this.requestSolver);
        imageManager.getImage(path);
    }
}
