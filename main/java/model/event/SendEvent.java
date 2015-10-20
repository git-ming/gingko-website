package model.event;

import control.Manager;
import model.db.event.Event;
import net.tool.WriteServerSolver;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xlo on 15-8-23.
 * it's the event for db
 */
public abstract class SendEvent extends Event {
    protected Set<WriteServerSolver> successSend, failSend, commitSend;
    protected String className, methodName;

    public SendEvent() {
        this.successSend = new HashSet<>();
        this.failSend = new HashSet<>();
        this.commitSend = new HashSet<>();
        this.className = "";
        this.methodName = "";

        for (int i = 0; i < Thread.currentThread().getStackTrace().length; i++) {
            String nowName = Thread.currentThread().getStackTrace()[i].getClassName();
            String nowMethod = Thread.currentThread().getStackTrace()[i].getMethodName();
            try {
                if (Manager.class.isAssignableFrom(Class.forName(nowName))) {
                    this.className = nowName;
                    this.methodName = nowMethod;
                    break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendWhileSuccess(WriteServerSolver serverSolver) {
        this.successSend.add(serverSolver);
    }

    public void sendWhileFail(WriteServerSolver serverSolver) {
        this.failSend.add(serverSolver);
    }

    public void sendWhileCommit(WriteServerSolver serverSolver) {
        this.commitSend.add(serverSolver);
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClojureName() {
        return this.className+"$"+this.methodName;
    }

    @Override
    protected void whenSucceed() {
        super.whenSucceed();
        this.successSend.forEach(WriteServerSolver::run);
    }

    @Override
    protected void whenFailed() {
        super.whenFailed();
        this.failSend.forEach(WriteServerSolver::run);
    }

    @Override
    protected void whenCommit() {
        super.whenCommit();
        this.commitSend.forEach(WriteServerSolver::run);
    }
}