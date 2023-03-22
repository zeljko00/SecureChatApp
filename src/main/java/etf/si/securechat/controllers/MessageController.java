package etf.si.securechat.controllers;

import etf.si.securechat.model.DTO.MessageFragment;
import etf.si.securechat.security.JwtUtil;
import etf.si.securechat.services.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    private final SimpMessagingTemplate simpMessageTemplate;
    private final UserService userService;
    private final JwtUtil jwtUtil;


    public MessageController(SimpMessagingTemplate simpMessageTemplate, UserService userService, JwtUtil jwtUtil) {
        this.simpMessageTemplate = simpMessageTemplate;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @MessageMapping("/chatroom/join")
    //this controller method is not exposed as endpoint
    //sends received message (or data extracted from it) to /topic/chatroom topic
    //prefixes for all topics must be specifiend in config class
    //this particular topic will store online users' usernames
    @SendTo("/chatroom/users/active")
    public String joinChatroom(@Payload String token) throws Exception {
        String user = null;
        try {
            user = userService.join(token);
            if (user != null)
                System.out.println(user + " joined!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @MessageMapping("/chatroom/leave")
    //sends received message (or data extracted from it) to /topic/chatroom topic
    //prefixes for all topics must be specifiend in config class
    //this particular topic will store online users' usernames
    @SendTo("/chatroom/users/left")
    public String leaveChatroom(@Payload String token) throws Exception {
        String user = null;
        try {
            user = userService.logout(token);
            if (user != null)
                System.out.println(user + " left!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // returning null == send nothing to message broker
        return user;
    }

    //enables this particular controller's method to handle messages received at ApplicationDestionationPrefix+/chatroom endpoint
    @MessageMapping("/chatroom")
    //@SendTo cant be used because we have to dinamicaly create topic for user that is specified as msg recepient
    //This enables user to subscribe to user-specific topic where all messages sent to him will be stored
    public String message(@Payload MessageFragment messageFragment) {
        System.out.println(messageFragment.getContent());
        //provjera tokena
        //dinamicaly creates topic for recepient
        //recepinet must be subscribed to /user/recepient/inbox topic to receive messages sent to him
        simpMessageTemplate.convertAndSendToUser(messageFragment.getRecepient(), "/inbox", messageFragment.getContent());
        return messageFragment.getContent();
    }
}
