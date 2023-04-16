package etf.si.securechat.services.implementations;

import etf.si.securechat.DAO.UserDAO;
import etf.si.securechat.model.DTO.ChatroomRequest;
import etf.si.securechat.model.DTO.JwtUser;
import etf.si.securechat.model.DTO.UserData;
import etf.si.securechat.model.User;
import etf.si.securechat.security.JwtUtil;
import etf.si.securechat.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final int MAX_LEN=30;
    private static final String[] FORBIDDEN_PATTERNS={"\"","'","create","#","alter","drop","set","update","delete","script","select","table"};
    private final UserDAO userDAO;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final List<UserData> activeUsers = new ArrayList<>();

    public UserServiceImpl(UserDAO userDAO, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userDAO = userDAO;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        encoder = new BCryptPasswordEncoder();
    }

    public Optional<User> findCitizenById(long id) {
        return userDAO.findById(id);
    }

    public String login(String username, String password) {
//        // user that already joined chat, and previously loged in, cant log in again
//        if (activeUsers.contains(username))
//            return null;
        if (username.length()> MAX_LEN || password.length()>MAX_LEN)
            return null;
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username, password
                            )
                    );
            JwtUser user = (JwtUser) authenticate.getPrincipal();
            // generating new JWT for authenticated user
            String token = jwtUtil.generateToken(user);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserData join(ChatroomRequest request) {
        // if token is invalid, exception will be thrown and joining will be disabled
        try {
            // extracting username from received token
            String username = jwtUtil.getUsernameFromToken(request.getToken());
            UserData user = new UserData();
            user.setUsername(username);
            user.setKey(request.getKey());
            System.out.println(username + " trying to join!");
//            System.out.println(activeUsers);
            synchronized (activeUsers) {
                // user can join chat only if he hadn't already done that
                if (activeUsers.contains(user) == false && jwtUtil.validate(request.getToken())) {
                    activeUsers.add(user);
                    System.out.println(username+" joined!");
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String logout(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            UserData user = new UserData();
            user.setUsername(username);
            System.out.println(activeUsers);
            synchronized (activeUsers){
            if (activeUsers.contains(user)  && jwtUtil.validate(token)) {
                activeUsers.remove(user);
                System.out.println(username+" left!");
                return username;
            }}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password) {
        if (username.length()> MAX_LEN || password.length()>MAX_LEN)
            return false;
        boolean secure=Arrays.stream(FORBIDDEN_PATTERNS).map(p -> {
            if(username.toLowerCase().contains(p) || password.toLowerCase().contains(p))
                return false;
            else return true;
        }).reduce(true,(b1,b2)-> { return b1 && b2; });
        if(!secure){
            System.out.println("Security risk!");
            return false;
        }
        User user = userDAO.findUserByUsername(username);
        if (user == null) {
            User newUser = new User();
            newUser.setUsername(username);
            String passwordHash = encoder.encode(password);
            newUser.setPassword(passwordHash);
            userDAO.saveAndFlush(newUser);
            return true;
        } else
            return false;
    }

    public List<UserData> activeUsers() {
        return activeUsers;
    }
}
