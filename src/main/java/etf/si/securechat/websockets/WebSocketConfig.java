package etf.si.securechat.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // we must specify prefixes for all topics that we are going to use in app
        config.enableSimpleBroker("/chatroom/users","/user");
        // specifies prefix for all WebSocket endpoints
        config.setApplicationDestinationPrefixes("/app");
        // enabling user to subscribe to topic that is specific to him
        // each message routed to this topic is forwarded to subscribed user
        config.setUserDestinationPrefix("/user");
    }
    //prefixes for all topics must be specified in config class
    //setAllowedOriginPatterns("*") - omogucava CORS zahtjeve
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // specifies endpoint that supports WebSocket and STOMP ( we allow SockJS as fallback option)
        // this endpoint is used for establisinh WebSocket connection (receives HTTP requests with switch/upgrade protocol header)
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS().setStreamBytesLimit(4*2160*2160+1000).setHttpMessageCacheSize(1024*1024+1000);;
    }
    // configuring message max length...
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000)
                .setSendBufferSizeLimit(4*2160*2160+1000)
                .setMessageSizeLimit(4*2160*2160+1000);
    }
}