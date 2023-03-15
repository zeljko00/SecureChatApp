package etf.si.securechat.services.implementations;

import etf.si.securechat.DAO.UserDAO;
import etf.si.securechat.model.DTO.JwtUser;
import etf.si.securechat.model.User;
import etf.si.securechat.security.JwtUtil;
import etf.si.securechat.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final List<String> activeUsers=new ArrayList<>();

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
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username, password
                            )
                    );
            JwtUser user = (JwtUser) authenticate.getPrincipal();
            String token = jwtUtil.generateToken(user);
            System.out.println("Generated token: " + token);
            activeUsers.add(username);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void logout(String username,String password){
        User user=userDAO.findUserByUsername(username);
        if(user!=null && user.getPassword().equals(password))
            activeUsers.remove(username);
    }

    public boolean register(String username, String password) {
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
}