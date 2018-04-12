package com.group42.client.controllers.fx;

import com.group42.client.protocol.IncomingServerMessage;
import java.util.function.Consumer;

/**
 * An abstract class that passes a response from the server to the
 * handler method, which is implemented in the child classes.
 */
public abstract class Controller {

    public static Consumer<IncomingServerMessage> onReceiveCallback;

    Controller() {
        Controller.onReceiveCallback = Controller.this::processResponse;
    }

    /**
     * process response on different scene.
     *
     * @param incomingServerMessage instance of response.
     */
    public abstract void processResponse(IncomingServerMessage incomingServerMessage);

}
