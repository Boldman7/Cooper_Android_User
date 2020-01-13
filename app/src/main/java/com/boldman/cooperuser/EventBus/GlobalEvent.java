package com.boldman.cooperuser.EventBus;

import org.greenrobot.eventbus.EventBus;

public class GlobalEvent {

    private static EventBus sBus;

    public static EventBus getBus() {

        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }
}
