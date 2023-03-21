package etf.si.securechat.controllers;

import etf.si.securechat.model.DTO.ChatroomRequest;
import etf.si.securechat.model.DTO.MessageFragment;
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


    public MessageController(SimpMessagingTemplate simpMessageTemplate, UserService userService) {
        this.simpMessageTemplate = simpMessageTemplate;
        this.userService = userService;
    }

    //enables this particular controller's method to handle messages received at ApplicationDestionationPrefix+/chatroom endpoint
    @MessageMapping("/chatroom/join")
    //sends received message (or data extracted from it) to /topic/chatroom topic
    //prefixes for all topics must be specifiend in config class
    //this particular topic will store online users' usernames
    @SendTo("/chatroom/users/active")
    public String joinChatroom(@Payload ChatroomRequest request) throws Exception {
        System.out.println("Joining :"+request.getUsername());
        //provjera tokena
        return request.getUsername();
    }

    @MessageMapping("/chatroom/leave")
    //sends received message (or data extracted from it) to /topic/chatroom topic
    //prefixes for all topics must be specifiend in config class
    //this particular topic will store online users' usernames
    @SendTo("/chatroom/users/left")
    public String leaveChatroom(@Payload ChatroomRequest request) throws Exception {
        System.out.println("Leaving: "+request.getUsername());
        //provjera tokena
        userService.logout(request.getUsername());
        return request.getUsername();
    }

    @MessageMapping("/chatroom")
    //@SendTo cant be used because we have to dinamicaly create topic for user that is specified as msg recepient
    //This enables user to subscribe to user-specific topic where all messages sent to him will be stored
    public String message(@Payload MessageFragment messageFragment){
        System.out.println(messageFragment.getContent());
        //dinamicaly creates topic for recepient
        //recepinet must be subscribed to /user/recepient_username/inbox topic to receive messages sent to him

        //provjera tokena

        simpMessageTemplate.convertAndSendToUser(messageFragment.getRecepient(),"/inbox",messageFragment.getContent());
        return messageFragment.getContent();
    }
}
