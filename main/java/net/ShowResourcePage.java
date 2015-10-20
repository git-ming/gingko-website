package net;

import model.tool.ioAble.NormalByteIO;
import model.tool.streamConnector.NormalStreamConnector;
import model.tool.streamConnector.StreamConnector;
import model.tool.streamConnector.io.NormalStreamIONode;
import model.tool.streamConnector.io.StreamIONode;
import net.server.serverSolver.RequestSolver;
import net.tool.head.writer.CustomReplyHeadWriter;
import model.tool.resource.ResourceManager;

/**
 * Created by xlo 2015/8/19.
 * every normal server solver in blog should extends it
 */
public interface ShowResourcePage {
    static boolean checkResource(String path) {
        return ShowResourcePage.class.getResource(path) != null;
    }

    static void send404(boolean html, RequestSolver requestSolver) {
        CustomReplyHeadWriter replyHeadWriter = requestSolver.getReplyHeadWriter();
        replyHeadWriter.setReply(404);
        replyHeadWriter.setMessage("not found");
        replyHeadWriter.setVersion("HTTP/1.1");
        if (!html) {
            replyHeadWriter.addMessage("Content-Length", "0");
            replyHeadWriter.sendHead();
        } else if (replyHeadWriter.sendHead()) {
            ResourceManager resourceManager = ResourceManager.getResourceManager();
            byte[] message = resourceManager.getResource("/NotFoundPage.html");

            replyHeadWriter.addMessage("Content-Type", "text/html;charset=utf-8");
            replyHeadWriter.addMessage("Content-Length", "" + message.length);

            StreamConnector streamConnector = new NormalStreamConnector();
            StreamIONode streamIONode = new NormalStreamIONode();
            NormalByteIO normalByteIO = new NormalByteIO();
            normalByteIO.setInitValue(message);
            normalByteIO.buildIO();
            streamIONode.setInputStream(normalByteIO.getInputStream());
            streamIONode.addOutputStream(requestSolver.getSocketIoBuilder().getOutputStream());
            streamConnector.addMember(streamIONode);
            streamConnector.connect();
        }
    }

    static void send403(boolean html, RequestSolver requestSolver) {
        CustomReplyHeadWriter replyHeadWriter = requestSolver.getReplyHeadWriter();
        replyHeadWriter.setReply(403);
        replyHeadWriter.setMessage("You don't have purview for this file");
        replyHeadWriter.setVersion("HTTP/1.1");
        if (!html) {
            replyHeadWriter.addMessage("Content-Length", "0");
            replyHeadWriter.sendHead();
        } else if (replyHeadWriter.sendHead()) {
            ResourceManager resourceManager = ResourceManager.getResourceManager();
            byte[] message = resourceManager.getResource("/ForbiddenPage.html");

            replyHeadWriter.addMessage("Content-Type", "text/html;charset=utf-8");
            replyHeadWriter.addMessage("Content-Length", "" + message.length);

            StreamConnector streamConnector = new NormalStreamConnector();
            StreamIONode streamIONode = new NormalStreamIONode();
            NormalByteIO normalByteIO = new NormalByteIO();
            normalByteIO.setInitValue(message);
            normalByteIO.buildIO();
            streamIONode.setInputStream(normalByteIO.getInputStream());
            streamIONode.addOutputStream(requestSolver.getSocketIoBuilder().getOutputStream());
            streamConnector.addMember(streamIONode);
            streamConnector.connect();
        }
    }
}
