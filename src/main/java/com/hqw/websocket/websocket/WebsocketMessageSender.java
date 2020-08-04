package com.hqw.websocket.websocket;//package com.boyi.him.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebsocketMessageSender {


    @Autowired
    private MyWebSocketServer server;


    public void sendToUser(Object message, String orgId) throws Exception {

        server.sendMessage("this is a topic message ");
    }
}
