package control;

import model.config.AccessConfig;
import model.config.ConfigManager;
import model.config.ReturnCodeConfig;
import model.event.SendEvent;
import net.server.serverSolver.RequestSolver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.tool.WriteFileServerSolver;
import net.tool.WriteMessageServerSolver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by xlo on 2015/8/28.
 * it's the abstract manager
 */
public abstract class Manager {
    protected RequestSolver requestSolver;
    protected static ReturnCodeConfig returnCodeConfig = (ReturnCodeConfig) ConfigManager.getConfigManager().getConfig(ReturnCodeConfig.class);
    protected static AccessConfig accessConfig = (AccessConfig) ConfigManager.getConfigManager().getConfig(AccessConfig.class);
    protected Manager sendManager = Manager.this;

    public Manager(RequestSolver requestSolver) {
        this.requestSolver = requestSolver;
    }

    public void addSendMessage(SendEvent sendEvent) {
        addSuccessMessage(sendEvent);
        addFailMessage(sendEvent);
    }

    public void addSuccessMessage(SendEvent sendEvent) {
        String accept = "accept";
        JSONObject object = getJsonObjectAsReturn(accept);
        sendEvent.sendWhileSuccess(new WriteMessageServerSolver(requestSolver, object));
    }

    public void addSuccessMessage(SendEvent sendEvent, Map<String, Object> message) {
        JSONObject object = getJsonObject(message);
        JSONObject result = getJsonObjectAsReturn("accept");
        result.put("data", object);
        sendEvent.sendWhileSuccess(new WriteMessageServerSolver(requestSolver, result));
    }

    public void addSuccessMessage(SendEvent sendEvent, String message) {
        JSONObject object = JSONObject.fromObject(message);
        JSONObject result = getJsonObjectAsReturn("accept");
        result.put("data", object);
        sendEvent.sendWhileSuccess(new WriteMessageServerSolver(requestSolver, result));
    }

    public void addSuccessMessage(SendEvent sendEvent, List<Map<String, Object>> message) {
        JSONArray object = getJsonObject(message);
        JSONObject result = getJsonObjectAsReturn("accept");
        result.put("data", object);
        sendEvent.sendWhileSuccess(new WriteMessageServerSolver(requestSolver, result));
    }

    public void addSendFile(SendEvent sendEvent, String path) {
        sendEvent.sendWhileSuccess(new WriteFileServerSolver(requestSolver, path));
    }

    public void addFailMessage(SendEvent sendEvent) {
        String forbidden = "forbidden";
        JSONObject object = getJsonObjectAsReturn(forbidden);
        sendEvent.sendWhileFail(new WriteMessageServerSolver(requestSolver, object));
    }

    public void addFailMessage(SendEvent sendEvent, Map<String, Object> message) {
        JSONObject object = getJsonObject(message);
        JSONObject result = getJsonObjectAsReturn("forbidden");
        result.put("data", object);
        sendEvent.sendWhileFail(new WriteMessageServerSolver(requestSolver, result));
    }

    public void addFailMessage(SendEvent sendEvent, String message) {
        JSONObject object = JSONObject.fromObject(message);
        JSONObject result = getJsonObjectAsReturn("forbidden");
        result.put("data", object);
        sendEvent.sendWhileFail(new WriteMessageServerSolver(requestSolver, result));
    }

    public void addFailMessage(SendEvent sendEvent, List<Map<String, Object>> message) {
        JSONArray object = getJsonObject(message);
        JSONObject result = getJsonObjectAsReturn("forbidden");
        result.put("data", object);
        sendEvent.sendWhileFail(new WriteMessageServerSolver(requestSolver, result));
    }

    protected JSONObject getJsonObjectAsReturn(String forbidden) {
        JSONObject object = new JSONObject();
        object.put("return", returnCodeConfig.getCode(forbidden));
        return object;
    }

    protected JSONObject getJsonObject(Map<String, Object> message) {
        JSONObject object = new JSONObject();
        object.putAll(message);
        return object;
    }

    protected JSONArray getJsonObject(List<Map<String, Object>> message) {
        return message.stream().map(this::getJsonObject).collect(Collectors.toCollection(JSONArray::new));
    }
}
