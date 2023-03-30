package etf.si.securechat.controllers;

import etf.si.securechat.model.DTO.ChatroomRequest;
import etf.si.securechat.model.DTO.MessageFragment;
import etf.si.securechat.model.DTO.UserData;
import etf.si.securechat.security.JwtUtil;
import etf.si.securechat.services.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

// handles STOMP messages received by WebSocket
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
    //WebSocket message sent to /chatroom/join endpoint will be handled by this method
    @MessageMapping("/chatroom/join")
    //received and processed messages are being forwarded  to /topic/chatroom topic (queue managed by message broker)
    //this particular topic will store online users' data (public key and username)
    @SendTo("/chatroom/users/active")
    public UserData joinChatroom(@Payload ChatroomRequest request) throws Exception {
        UserData user = userService.join(request);
        return user;
    }

    @MessageMapping("/chatroom/leave")
    //this particular topic will store data about users that recently have left app
    @SendTo("/chatroom/users/left")
    public String leaveChatroom(@Payload String token) throws Exception {
        String  user = userService.logout(token);
        // returning null -> send nothing to message broker (message queue/topic)
        return user;
    }

    //enables this particular controller's method to handle messages received at ApplicationDestionationPrefix+/chatroom endpoint
    @MessageMapping("/chatroom")
    //@SendTo cant be used because we have to dinamicaly create topic for user that is specified as msg recepient
    //This enables user to subscribe to user-specific topic where all messages sent to him will be stored
    public MessageFragment message(@Payload MessageFragment messageFragment) {
        System.out.println("Received: "+messageFragment);
        MessageFragment message=new MessageFragment();
        message.setContent(messageFragment.getContent());
        message.setSender(messageFragment.getSender());
        message.setNonce(messageFragment.getNonce());
        // check token validity
        try {
            boolean flag = jwtUtil.validate(messageFragment.getToken());
            if(!flag)
                throw new Exception();
        }catch(Exception e){
            System.out.println("Invalid token!");
            return null;
        }
        //dynamically creates topic for recipient
        //recipient must be subscribed to /user/recipient/inbox topic to receive messages sent to him
        simpMessageTemplate.convertAndSendToUser(messageFragment.getRecepient(), "/inbox", message);
        return message;
    }
}
