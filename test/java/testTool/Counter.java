package testTool;

/**
 * Created by xlo on 2015/8/25.
 * it's the counter
 */
public class Counter {
    private int num, success, fail;

    public Counter(int num) {
        this.num = num;
    }

    public synchronized void add(int value) {
        this.num += value;
    }

    public synchronized void addSuccess(int value) {
        this.success += value;
    }

    public synchronized void addFail(int value) {
        this.fail += value;
    }

    public synchronized int get() {
        return this.num;
    }

    public synchronized int getSuccess() {
        return success;
    }

    public synchronized int getFail() {
        return fail;
    }
}
