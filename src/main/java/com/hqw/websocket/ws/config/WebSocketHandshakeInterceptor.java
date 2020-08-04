package com.hqw.websocket.ws.config;

import com.boyi.him.ws.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 自定义 WebSocketHandshakeInterceptor 拦截器；可以获取到httpsession
 */
@Slf4j
@Component
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        log.info("Before Handshake");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String org = servletRequest.getHeaders().getFirst(Constants.ORG_KEY);
            if(org == null){
                return false;
            }
            HttpSession session = servletRequest.getServletRequest().getSession(true);
            if (session != null) {
                OrgSession orgSession = (OrgSession) session.getAttribute(Constants.WS_SESSION_USER);
                if (orgSession==null) {
                    orgSession = new OrgSession();
                    orgSession.setOrgId(org);
                }
                attributes.put(Constants.WS_SESSION_USER,orgSession);
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        log.info("After Handshake");
        super.afterHandshake(request, response, wsHandler, ex);
    }

}