package com.boldman.cooperuser.EventBus;

public final class MessageEvent {

    private MessageEvent() {
        throw new UnsupportedOperationException("This class is non-instantiable");
    }

    public static class ReceivedMessage{
        public int state;
        public ReceivedMessage(int received) {
            state = received;
        }
    }
}
