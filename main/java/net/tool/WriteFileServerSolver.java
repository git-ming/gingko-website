package net.tool;

import model.cache.ImageCacheManager;
import model.tool.ioAble.FileIOBuilder;
import model.tool.ioAble.NormalFileIO;
import model.tool.streamConnector.NormalStreamConnector;
import model.tool.streamConnector.StreamConnector;
import model.tool.streamConnector.io.NormalStreamIONode;
import model.tool.streamConnector.io.StreamIONode;
import net.server.serverSolver.RequestSolver;
import net.tool.connection.event.ConnectionEvent;
import net.tool.connection.event.ConnectionEventManager;
import net.tool.head.writer.CustomReplyHeadWriter;

import java.io.File;

/**
 * Created by xlo on 2015/9/16.
 * it's the server solver send file
 */
public class WriteFileServerSolver extends WriteServerSolver {
    protected String path;
    protected File file;
    protected FileIOBuilder fileIOBuilder;

    public WriteFileServerSolver(RequestSolver requestSolver, Object... data) {
        super(requestSolver, data);
        this.path = data[0].toString();
        this.file = new File(this.path);

        ConnectionEventManager.getConnectionEventManager().addEventHandlerToItem(ConnectionEvent.connectEnd, this,
                (event, solver) -> {
                    fileIOBuilder.close();
                    closeSocket();
                    ImageCacheManager.getImageCacheManager().unUse(file.getAbsoluteFile());
                });
    }

    @Override
    protected boolean checkRequestExist() {
        return this.file.exists() && this.file.isFile();
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
        customReplyHeadWriter.addMessage("Content-Length", "" + this.file.length());
        return customReplyHeadWriter.sendHead();
    }

    @Override
    protected boolean afterSendHead() {
        this.fileIOBuilder = new NormalFileIO();
        this.fileIOBuilder.setFile(this.path);
        return this.fileIOBuilder.buildIO();
    }

    @Override
    public void connect() {
        StreamConnector connector = new NormalStreamConnector();
        StreamIONode ioNode = new NormalStreamIONode();

        ioNode.setInputStream(this.fileIOBuilder.getInputStream());
        ioNode.addOutputStream(this.requestSolver.getSocketIoBuilder().getOutputStream());
        connector.addMember(ioNode);
        connector.connect();
    }
}
