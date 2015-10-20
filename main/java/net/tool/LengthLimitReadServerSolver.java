package net.tool;

import model.log.LogManager;
import model.tool.ioAble.NormalByteIO;
import model.tool.streamConnector.NormalStreamConnector;
import model.tool.streamConnector.StreamConnector;
import model.tool.streamConnector.io.LengthLimitStreamIONode;
import model.tool.streamConnector.io.StreamIONode;
import net.server.serverSolver.RequestSolver;
import net.server.serverSolver.normalServer.NormalKeepAliveServerSolver;
import net.tool.connection.event.ConnectionEvent;
import net.tool.connection.event.ConnectionEventManager;

/**
 * Created by xlo on 2015/8/24.
 * it's length limit read server solver
 */
public abstract class LengthLimitReadServerSolver extends NormalKeepAliveServerSolver {
    protected String message;
    protected byte[] originalMessage;
    protected NormalByteIO byteIO;
    protected long length;

    public LengthLimitReadServerSolver() {
        ConnectionEventManager.getConnectionEventManager().addEventHandlerToItem(ConnectionEvent.connectSuccess, this,
                (event, solver) -> {
                    trySolveMessage();
                    if (byteIO != null) byteIO.close();
                });
        ConnectionEventManager.getConnectionEventManager().addEventHandlerToItem(ConnectionEvent.connectFail, this,
                (event, solver) -> {
                    closeSocket();
                    if (byteIO != null) byteIO.close();
                });
    }

    @Override
    protected boolean checkRequestExist() {
        return true;
    }

    @Override
    protected boolean checkRequestVisitable() {
        String message = this.requestSolver.getRequestHeadReader().getMessage("Content-Length");
        try {
            this.length = Long.valueOf(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean sendingHead() {
        return true;
    }

    @Override
    protected boolean afterSendHead() {
        this.byteIO = new NormalByteIO();
        this.byteIO.setInitValue(new byte[0]);
        return this.byteIO.buildIO();
    }

    @Override
    public void connect() {
        StreamConnector connector = new NormalStreamConnector();

        if (length > 0) {
            StreamIONode ioNode = new LengthLimitStreamIONode(length);
            ioNode.setInputStream(this.requestSolver.getSocketIoBuilder().getInputStream());
            ioNode.addOutputStream(this.byteIO.getOutputStream());
            connector.addMember(ioNode);

            connector.connect();

            this.originalMessage = this.byteIO.getValue();
        } else {
            this.originalMessage = new byte[0];
        }

        LogManager.getLogManager().writeLog("blog read", this.message);
    }

    @Override
    public void startNextServerSolver() {
    }

    public LengthLimitReadServerSolver setRequestSolver(RequestSolver requestSolver) {
        this.requestSolver = requestSolver;
        return this;
    }

    protected void trySolveMessage() {
        try {
            this.message = new String(this.originalMessage);
            solveMessage();
        } catch (Exception e) {
            closeSocket();
        }
    }

    public abstract void solveMessage() throws Exception;
}
