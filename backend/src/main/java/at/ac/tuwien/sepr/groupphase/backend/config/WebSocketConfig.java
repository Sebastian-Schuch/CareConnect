package at.ac.tuwien.sepr.groupphase.backend.config;

import at.ac.tuwien.sepr.groupphase.backend.security.WebSocketTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.lang.invoke.MethodHandles;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final WebSocketTokenFilter webSocketTokenFilter;

    public WebSocketConfig(WebSocketTokenFilter webSocketTokenFilter) {
        this.webSocketTokenFilter = webSocketTokenFilter;
    }

    /**
     * configures the WebSocket endpoints that clients can connect to.
     * In this code, it registers the "/api/v1/websocket" endpoint and enables SockJS fallback options.
     *
     * @param registry A registry for registering Stomp endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        LOG.info("new Websocket connection established");
        registry.addEndpoint("/api/v1/chat").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenFilter);
    }
}
