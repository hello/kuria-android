package is.hellos.demos.models.notification;

import java.io.Serializable;

/**
 * Created by simonchen on 5/9/17.
 */

public class Notification implements Serializable {
    private final String title;
    private final String msg;
    private final int tag;
    private Class<?> targetClass;


    public Notification(String title, String msg, int tag, Class<?> targetClass) {
        this.title = title;
        this.msg = msg;
        this.tag = tag;
        this.targetClass = targetClass;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public int getTag() {
        return tag;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
