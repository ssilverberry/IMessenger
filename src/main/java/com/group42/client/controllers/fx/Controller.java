package com.group42.client.controllers.fx;

import com.group42.client.protocol.IncomingServerMessage;
import java.util.function.Consumer;

/**
 * Abstract class for receive callback from server.
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
