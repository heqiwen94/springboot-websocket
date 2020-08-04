package com.hqw.websocket.ws.config;

import com.boyi.him.ws.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker // (能够在 WebSocket 上启用 STOMP)
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${ws.applicationDestinationPrefixes:/ws2}")
    private String applicationDestinationPrefixes;

    @Autowired
    private WebSocketHandshakeInterceptor handshakeInterceptor;

    @Autowired
    private StompInterceptor stompInterceptor;

    @Autowired
    private OrgSessions orgs;

    /**
     * 添加Endpoint、自定义拦截器、允许跨域、允许使用socketjs
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 配置消息代理
     * applicationDestinationPrefixes：为配置应用服务器的地址前缀；
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");//设置服务器队列、广播消息的基础路径
        registry.setApplicationDestinationPrefixes(applicationDestinationPrefixes,"gw"); //设置客户端订阅消息的基础路径
    }

    /**
     * 可以设置输入消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
     * 自定义拦截器，在消息发送之前调用
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }

    /**
     * 配置发送与接收的消息参数
     *
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                WebSocketHandlerDecorator decorator = new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        OrgSession orgSession = (OrgSession) session.getAttributes().get(Constants.WS_SESSION_USER);
                        if (orgSession == null) {
                            throw new RuntimeException("can't connection");
                        }
                        orgSession.setSession(session);
                        orgs.putWebSocketSession(orgSession);

                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        orgs.removeWebSocketSession(session);
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
                return decorator;
            }
        });

    }
}