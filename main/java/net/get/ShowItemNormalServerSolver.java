package net.get;

import model.safeList.SafeModelManager;
import model.tool.ioAble.FileIOBuilder;
import model.tool.ioAble.IOAble;
import model.tool.ioAble.NormalByteIO;
import model.tool.ioAble.NormalFileIO;
import model.tool.streamConnector.NormalStreamConnector;
import model.tool.streamConnector.StreamConnector;
import model.tool.streamConnector.io.NormalStreamIONode;
import model.tool.streamConnector.io.StreamIONode;
import net.ShowResourcePage;
import net.server.serverSolver.normalServer.NormalServerSolver;
import net.tool.connection.event.ConnectionEvent;
import net.tool.connection.event.ConnectionEventManager;
import net.tool.head.writer.CustomReplyHeadWriter;
import model.tool.resource.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by xlo on 2015/8/19.
 * it's the server solver who show the file end with html, css, js and so on.
 */
public class ShowItemNormalServerSolver extends NormalServerSolver implements ShowResourcePage {
    protected IOAble IOBuilder;
    protected File file;
    protected String path;

    public ShowItemNormalServerSolver() {
        ConnectionEventManager.getConnectionEventManager().addEventHandlerToItem(ConnectionEvent.connectEnd, this,
                (s, connectionSolver) -> closeSocket());
    }

    @Override
    protected boolean checkRequestExist() {
        try {
            path = URLDecoder.decode(this.requestSolver.getRequestHeadReader().getUrl().getPath(), "UTF-8");
            this.file = new File("."+ path);
            if (!this.file.exists() || !this.file.isFile()) {
                if (ResourceManager.getResourceManager().haveResource(this.path)) {
                    return true;
                }
                ShowResourcePage.send404(this.file.getName().endsWith("html"), this.requestSolver);
                return false;
            }
            return true;
        } catch (IOException e) {
            ShowResourcePage.send404(this.file.getName().endsWith("html"), this.requestSolver);
            return false;
        }
    }

    @Override
    protected boolean checkRequestVisitable() {
        if (!SafeModelManager.getSafeModelManager().accept("file", this.file.getAbsolutePath())) {
            ShowResourcePage.send403(this.file.getName().endsWith("html"), this.requestSolver);
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendingHead() {
        CustomReplyHeadWriter replyHeadWriter = this.requestSolver.getReplyHeadWriter();
        replyHeadWriter.setReply(200);
        replyHeadWriter.setMessage("OK");
        replyHeadWriter.setVersion("HTTP/1.1");
        if (this.file.exists()) {
            replyHeadWriter.addMessage("Content-Length", "" + file.length());
        } else {
            replyHeadWriter.addMessage("Content-Length", "" + ResourceManager.getResourceManager().getResource(this.path).length);
        }
        if (this.path.endsWith("html")) {
            replyHeadWriter.addMessage("Content-Type", "text/html;charset=utf-8");
        }
        return replyHeadWriter.sendHead();
    }

    @Override
    protected boolean afterSendHead() {
        if (this.file.exists() && this.file.isFile()) {
            FileIOBuilder fileIOBuilder = new NormalFileIO();
            fileIOBuilder.setFile(this.file.getAbsolutePath());
            this.IOBuilder = fileIOBuilder;
        } else {
            NormalByteIO normalByteIO = new NormalByteIO();
            normalByteIO.setInitValue(ResourceManager.getResourceManager().getResource(this.path));
            this.IOBuilder = normalByteIO;
        }
        return this.IOBuilder.buildIO();
    }

    public void connect() {
        StreamConnector streamConnector = new NormalStreamConnector();
        StreamIONode streamIONode = new NormalStreamIONode();
        streamIONode.setInputStream(this.IOBuilder.getInputStream());
        streamIONode.addOutputStream(this.requestSolver.getSocketIoBuilder().getOutputStream());
        streamConnector.addMember(streamIONode);
        streamConnector.connect();
    }

}
