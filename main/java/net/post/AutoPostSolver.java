package net.post;

import model.config.PostConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.tool.LengthLimitReadServerSolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xlo on 2015/9/9.
 * it's the auto post solver by clojure
 */
public class AutoPostSolver extends LengthLimitReadServerSolver {

    protected PostConfig.PostInfo postInfo;

    private PostConfig postConfig;

    public AutoPostSolver(PostConfig.PostInfo postInfo) {
        this.postInfo = postInfo;
        this.postConfig = PostConfig.getConfig();
    }

    @Override
    public void solveMessage() throws Exception {
        Object managerObject = getManagerObject();
        List<Object> data = getData();
        Method method = getMethod(data);
        method.invoke(managerObject, data.toArray());
    }

    protected Method getMethod(List<Object> data) throws NoSuchMethodException, ClassNotFoundException {
        int size = data.size();
        Class[] methodParamType = new Class[size];
        for (int i = 0; i < size; i++) {
            methodParamType[i] = data.get(i).getClass();
        }
        return this.postConfig.getMethod(this.postInfo.getManager(), this.postInfo.getMethod(), methodParamType);
    }

    protected List<Object> getData() {
        List<Object> data = new LinkedList<>();
        if (this.postInfo.needAccess()) {
            String username = this.requestSolver.getRequestHeadReader().getMessage("Username");
            String password = this.requestSolver.getRequestHeadReader().getMessage("Password");
            data.add(username);
            data.add(password);
        }
        List<String> need = this.postInfo.getMethodData();
        List<String> defaultValue = this.postInfo.getDefaultValue();
        switch (this.postInfo.getDataType()) {
            case "file":
                data.add(this.requestSolver.getRequestHeadReader().getMessage("File-Type"));
                data.add(this.originalMessage);
                break;
            case "array":
                JSONArray array = JSONArray.fromObject(this.message);
                List<Object> arrayData = new ArrayList<>();
                for (Object object : array) {
                    solveObject(arrayData, need, defaultValue, (JSONObject) object);
                }
                String[] strings = new String[arrayData.size()];
                for (int i = 0; i < strings.length; i++) {
                    strings[i] = (String) arrayData.get(i);
                }
                data.add(strings);
                break;
            default:
                if (need.size() != 0) {
                    JSONObject object = JSONObject.fromObject(this.message);
                    solveObject(data, need, defaultValue, object);
                }
                break;
        }
        return data;
    }

    private void solveObject(List<Object> data, List<String> need, List<String> defaultValue, JSONObject object) {
        for (int i = 0; i < need.size(); i++) {
            if (object.containsKey(need.get(i))) {
                data.add(object.getString(need.get(i)));
            } else if (defaultValue.get(i) != null) {
                data.add(defaultValue.get(i));
            } else {
                throw new NullPointerException();
            }
        }
    }

    protected Object getManagerObject() throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] param = {this.requestSolver};
        Constructor<?> constructor = this.postConfig.getManagerConstructor(this.postInfo.getManager());
        return constructor.newInstance(param);
    }
}
