package net.tool;

import model.log.LogManager;
import model.tool.ioAble.NormalStringIO;
import model.tool.streamConnector.NormalStreamConnector;
import model.tool.streamConnector.StreamConnector;
import model.tool.streamConnector.io.NormalStreamIONode;
import model.tool.streamConnector.io.StreamIONode;
import net.server.serverSolver.RequestSolver;
import net.tool.head.writer.CustomReplyHeadWriter;

/**
 * Created by xlo on 2015/8/24.
 * it's the server solver who write message to client
 */
public class WriteMessageServerSolver extends WriteServerSolver {
    protected String message;

    public WriteMessageServerSolver(RequestSolver requestSolver, Object... data) {
        super(requestSolver, data);
        this.message = data[0].toString();
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
        CustomReplyHeadWriter customReplyHeadWriter = this.requestSolver.getReplyHeadWriter();
        customReplyHeadWriter.setReply(200);
        customReplyHeadWriter.setVersion("HTTP/1.1");
        customReplyHeadWriter.setMessage("ok");
        customReplyHeadWriter.addMessage("Content-Length", "" + message.length());
        return customReplyHeadWriter.sendHead();
    }

    @Override
    protected boolean afterSendHead() {
        return true;
    }

    @Override
    public void connect() {
        StreamConnector connector = new NormalStreamConnector();
        StreamIONode ioNode = new NormalStreamIONode();

        NormalStringIO ioBuilder = new NormalStringIO();
        ioBuilder.setInitValue(message);
        if (!ioBuilder.buildIO()) return ;

        ioNode.setInputStream(ioBuilder.getInputStream());
        ioNode.addOutputStream(this.requestSolver.getSocketIoBuilder().getOutputStream());
        connector.addMember(ioNode);
        connector.connect();
        ioBuilder.close();

        LogManager.getLogManager().writeLog("blog write", this.message);
    }
}
