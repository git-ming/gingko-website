package net.post;

import model.config.ConfigManager;
import model.config.PostConfig;
import net.AimSolverChooser;
import net.server.serverSolver.RequestSolver;
import net.server.serverSolver.normalServer.AbstractServerSolver;
import net.server.serverSolver.normalServer.DynamicServerSolver;
import net.tool.LengthLimitReadServerSolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 2015/8/20.
 * it's the server solver solve the command post
 */
public class PostServerSolver extends DynamicServerSolver {
    public static Map<String, AimSolverChooser> chooserMap = new HashMap<>();
    protected static PostConfig postConfig = (PostConfig) ConfigManager.getConfigManager().getConfig(PostConfig.class);

    public PostServerSolver() {
        for (PostConfig.PostInfo postInfo : postConfig.getPostInfo()) {
            chooserMap.put(postInfo.getName(), new AimSolverChooser() {
                @Override
                public boolean isThisSolver(RequestSolver requestSolver) {
                    return requestSolver.getRequestHeadReader().getUrl().getPath().equals(postInfo.getUrl());
                }

                @Override
                public AbstractServerSolver getSolver(RequestSolver requestSolver) {
                    LengthLimitReadServerSolver lengthLimitReadServerSolver = new AutoPostSolver(postInfo);
                    return lengthLimitReadServerSolver.setRequestSolver(requestSolver);
                }
            });
        }
    }

    @Override
    public void buildAimSolver(RequestSolver requestSolver) {
        for (Map.Entry<String, AimSolverChooser> now : chooserMap.entrySet()) {
            if (now.getValue().isThisSolver(this.requestSolver)) {
                this.aimSolver = now.getValue().getSolver(this.requestSolver);
                break;
            }
        }
        if (this.aimSolver == null) {
            this.aimSolver = new ErrorCommandWriter(this.requestSolver);
        }
    }
}
