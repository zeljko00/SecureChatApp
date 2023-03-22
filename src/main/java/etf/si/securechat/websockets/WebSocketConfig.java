package etf.si.securechat.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //specifikujemo prefikse za topice koje cemo koristiti
        config.enableSimpleBroker("/chatroom/users","/user");
        //klijenti treba da salju poruke na endpoint specifikovan sa ApplicationDestinationPrefix-om + putanja specifikovana
        //@MessageMapping anotacijama (koja ustvari govori kojoj metodi kontrolera se rutira poruka)
        config.setApplicationDestinationPrefixes("/app");
        //omogucavamo korisniku da se pretplati na topic koji je specifičan za njegovu sesiju - sve poruke koje se
        //rutiraju topic sa ovim prefiksom se prosljedju samo jednom specificnom korisniku
        config.setUserDestinationPrefix("/user");
    }

    //setAllowedOriginPatterns("*") - omogucava CORS zahtjeve
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //registrujemo endpoint koji ce podrzavati prijem STOMP poruka i koji ce kao fallback opciju koristiti SockJS
        //ovaj endpoint ce se korisitit za uspostavljanje ws konekcije
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
//        registry.addEndpoint("/ws/info").setAllowedOrigins("http://localhost:3000").withSockJS();
    }

}