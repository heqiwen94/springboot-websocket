package com.hqw.websocket.ws.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OrgSessions {

    private static long websocketId = 100000000L;

    private Map<String, OrgSession> orgSessionMap;
    private Map<WebSocketSession,String> sessionOrgMap;

    @PostConstruct
    public void init(){
        orgSessionMap = new ConcurrentHashMap();
        sessionOrgMap = new ConcurrentHashMap<>();
    }

    public synchronized void removeWebSocketSession(WebSocketSession webSocketSession){
        if (webSocketSession == null) return;
        String org = sessionOrgMap.get(webSocketSession);
        sessionOrgMap.remove(webSocketSession);
        orgSessionMap.remove(org);
        log.info("org id:{}下线。", org);
    }

    public synchronized void putWebSocketSession(OrgSession orgSession){
        orgSessionMap.put(orgSession.getOrgId(), orgSession);
        sessionOrgMap.put(orgSession.getSession(), orgSession.getOrgId());
        log.info("org id:{}上线。", orgSession.getOrgId());
    }

    public synchronized WebSocketSession getSocketSession(String orgId){
        OrgSession orgSession = orgSessionMap.get(orgId);
        return orgSession != null ? orgSession.getSession():null;
    }

    public synchronized long getWebsocketId(){
        websocketId += 1;
        return websocketId;
    }

}
