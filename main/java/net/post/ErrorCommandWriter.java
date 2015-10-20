package net.post;

import net.ShowResourcePage;
import net.server.serverSolver.RequestSolver;
import net.server.serverSolver.normalServer.NormalServerSolver;
import net.tool.connection.event.ConnectionEvent;
import net.tool.connection.event.ConnectionEventManager;

/**
 * Created by xlo on 15-8-30.
 * it's the write to post solver.
 * when can't find any solver, this solver would be run
 */
public class ErrorCommandWriter extends NormalServerSolver implements ShowResourcePage {

    public ErrorCommandWriter(RequestSolver requestSolver) {
        this.requestSolver = requestSolver;
        ConnectionEventManager.getConnectionEventManager().addEventHandlerToItem(ConnectionEvent.connectEnd, this,
                (s, connectionSolver) -> closeSocket());
    }

    @Override
    public boolean checkAccept() {
        return true;
    }

    @Override
    protected boolean checkRequestExist() {
        return true;
    }

    @Override
    protected boolean checkRequestVisitable() {
        return true;
    }

    @Override
    protected boolean sendingHead() {
        ShowResourcePage.send404(false, this.requestSolver);
        return false;
    }

    @Override
    protected boolean afterSendHead() {
        return true;
    }

    @Override
    public void connect() {

    }
}
