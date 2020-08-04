package com.hqw.websocket.ws;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebsocketMessageSender {


    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private SimpMessagingTemplate template;

    public void sendToUser(Object message, String orgId) throws Exception {
        SimpUser user = userRegistry.getUser(orgId);
        if (null == user) {
            return;
        }
        log.info("websocket:{}", user.getName());
        String json = JSON.toJSONString(message);
        template.convertAndSendToUser(user.getName(), "/queue/message", json);
    }
}
