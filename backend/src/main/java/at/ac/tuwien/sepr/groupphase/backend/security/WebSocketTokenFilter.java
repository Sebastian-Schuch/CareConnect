package at.ac.tuwien.sepr.groupphase.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class WebSocketTokenFilter implements ChannelInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JwtAuthorizationFilter jwtUtils;


    public WebSocketTokenFilter(JwtAuthorizationFilter jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = parseJwt(accessor);
            if (token != null) {
                UsernamePasswordAuthenticationToken user = null;
                try {
                    user = jwtUtils.getAuthTokenFromString(token);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
                if (user != null) {
                    if (user.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("PATIENT"))) {
                        LOGGER.info("User connected: {}", user.getName());
                        accessor.setUser(user);
                    } else if (user.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("DOCTOR"))) {
                        LOGGER.info("User connected: {}", user.getName());
                        accessor.setUser(user);
                    } else {
                        LOGGER.warn("Connection refused for: {}", user.getName());
                        return null;
                    }

                }

            }
        }
        return message;
    }

    private String parseJwt(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }
}
