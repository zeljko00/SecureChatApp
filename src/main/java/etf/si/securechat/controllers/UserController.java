package etf.si.securechat.controllers;

import etf.si.securechat.model.DTO.UserData;
import etf.si.securechat.model.User;
import etf.si.securechat.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final Base64.Decoder decoder;
    private final MessageController messageController;

    public UserController(UserService userService, MessageController messageController) {
        this.userService = userService;
        this.messageController = messageController;
        this.decoder = Base64.getDecoder();
    }
    // extracts credentials from Authorization header, decodes them, checks validity and returns generated JWT
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        String[] tokens = auth.split(" ");
        byte[] data = tokens[1].getBytes();
        byte[] decodedData = decoder.decode(data);
        String credentials = new String(decodedData);
        tokens = credentials.split(":");
        String username = tokens[0];
        String password = tokens[1];
        String token = userService.login(username, password);
        if (token != null)
            return new ResponseEntity<>(token, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<UserData>> activeUsers() {
        return new ResponseEntity<>(userService.activeUsers(), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        boolean result = userService.register(user.getUsername(), user.getPassword());
        if (result)
            return new ResponseEntity<>(userService.login(user.getUsername(), user.getPassword()), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}