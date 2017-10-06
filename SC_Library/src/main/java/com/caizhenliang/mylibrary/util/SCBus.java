package com.caizhenliang.mylibrary.util;

import org.greenrobot.eventbus.EventBus;

/**
 * Using EventBus
 * Created by caizhenliang on 2017/9/16.
 */

public class SCBus extends EventBus {
    private static final SCBus ourInstance = new SCBus();

    public static SCBus getInstance() {
        return ourInstance;
    }

    private SCBus() {
    }

    /**
     * print event log
     *
     * @param event
     */
    @Override
    public void post(Object event) {
        SCLog.w("BusEvent", event.getClass().getSimpleName());
        super.post(event);
    }
}
