package com.argusoft.path.tht.systemconfiguration.websocket.configuration;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Web Socket
 *
 * @author Ali
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private AuthenticationService authenticationService;

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor!=null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/notification/")) {
                        String token = accessor.getFirstNativeHeader("token");
                        String receiverId = destination.substring(destination.lastIndexOf("/") + 1);
                        if (token == null || !token.startsWith("Bearer ")) {
                            throw new AccessDeniedException("Invalid token.");
                        }
                        token = token.substring(7);
                        try {
                            OAuth2Authentication authority = authenticationService.loadAuthentication(token);
                            ContextInfo contextInfo = ((ContextInfo) authority.getPrincipal());
                            if (contextInfo == null || !contextInfo.getUsername().equals(receiverId)) {
                                throw new AccessDeniedException("User is not authorized to subscribe.");
                            }
                        } catch (AuthenticationException | InvalidTokenException e) {
                            throw new AccessDeniedException("User is not authorized to subscribe.");
                        }
                    }
                }
                return message;
            }
        });
    }
}
