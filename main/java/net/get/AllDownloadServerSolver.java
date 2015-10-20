package net.get;

import model.safeList.SafeModelManager;
import model.tool.ioAble.FileIOBuilder;
import model.tool.ioAble.NormalFileIO;
import model.tool.streamConnector.NormalStreamConnector;
import model.tool.streamConnector.StreamConnector;
import model.tool.streamConnector.io.NormalStreamIONode;
import model.tool.streamConnector.io.StreamIONode;
import net.ShowResourcePage;
import net.server.serverSolver.normalServer.NormalServerSolver;
import net.tool.connection.event.ConnectionEvent;
import net.tool.connection.event.ConnectionEventManager;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by xlo on 2015/8/19.
 * it's the server solver download everything.
 */
public class AllDownloadServerSolver extends NormalServerSolver implements ShowResourcePage {
    protected File file;
    protected FileIOBuilder fileIOBuilder;
    protected String path;

    public AllDownloadServerSolver() {
        ConnectionEventManager.getConnectionEventManager().addEventHandlerToItem(ConnectionEvent.connectEnd, this,
                (event, solver) -> closeSocket());
    }

    @Override
    protected boolean checkRequestExist() {
        try {
            path = URLDecoder.decode(this.requestSolver.getRequestHeadReader().getUrl().getPath(), "UTF-8");
            this.file = new File("." + path);
            if (!this.file.exists() || !this.file.isFile()) {
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
        this.requestSolver.getReplyHeadWriter().setReply(200);
        this.requestSolver.getReplyHeadWriter().setMessage("OK");
        this.requestSolver.getReplyHeadWriter().setVersion("HTTP/1.1");
        this.requestSolver.getReplyHeadWriter().addMessage("Content-Length", "" + file.length());
        this.requestSolver.getReplyHeadWriter().addMessage("Content-Disposition", "attachment;filename=" + file.getName());
        return this.requestSolver.getReplyHeadWriter().sendHead();
    }

    @Override
    protected boolean afterSendHead() {
        this.fileIOBuilder = new NormalFileIO();
        this.fileIOBuilder.setFile(this.file.getAbsolutePath());
        return this.fileIOBuilder.buildIO();
    }

    @Override
    public void connect() {
        StreamConnector streamConnector = new NormalStreamConnector();
        StreamIONode streamIONode = new NormalStreamIONode();
        streamIONode.setInputStream(this.fileIOBuilder.getInputStream());
        streamIONode.addOutputStream(this.requestSolver.getSocketIoBuilder().getOutputStream());
        streamConnector.addMember(streamIONode);
        streamConnector.connect();
    }
}
