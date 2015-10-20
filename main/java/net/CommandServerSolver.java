package net;

import net.get.GetServerSolver;
import net.post.ErrorCommandWriter;
import net.post.PostServerSolver;
import net.server.serverSolver.RequestSolver;
import net.server.serverSolver.normalServer.AbstractServerSolver;
import net.server.serverSolver.normalServer.DynamicServerSolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 2015/8/20.
 * it's check the command is get or post
 */
public class CommandServerSolver extends DynamicServerSolver {
    public static Map<String, AimSolverChooser> chooserMap = new HashMap<>();

    public CommandServerSolver() {
        chooserMap.put("get", new AimSolverChooser() {
            @Override
            public boolean isThisSolver(RequestSolver requestSolver) {
                return requestSolver.getRequestHeadReader().getCommand().equals("GET");
            }

            @Override
            public AbstractServerSolver getSolver(RequestSolver requestSolver) {
                GetServerSolver getServerSolver = new GetServerSolver() {
                    public GetServerSolver set(RequestSolver requestSolver) {
                        this.requestSolver = requestSolver;
                        return this;
                    }
                }.set(requestSolver);
                getServerSolver.buildAimSolver(requestSolver);
                return getServerSolver;
            }
        });

        chooserMap.put("post", new AimSolverChooser() {
            @Override
            public boolean isThisSolver(RequestSolver requestSolver) {
                return requestSolver.getRequestHeadReader().getCommand().equals("POST");
            }

            @Override
            public AbstractServerSolver getSolver(RequestSolver requestSolver) {
                PostServerSolver postServerSolver = new PostServerSolver() {
                    public PostServerSolver set(RequestSolver requestSolver) {
                        this.requestSolver = requestSolver;
                        return this;
                    }
                }.set(requestSolver);
                postServerSolver.buildAimSolver(requestSolver);
                return postServerSolver;
            }
        });
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
