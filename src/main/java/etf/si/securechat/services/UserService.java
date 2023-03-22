package etf.si.securechat.services;

import etf.si.securechat.model.User;

import java.util.Optional;
import java.util.List;

public interface UserService {
    Optional<User> findCitizenById(long id);
    String login(String username, String password);
    boolean register(String username,String password);
    String logout(String token);
    String join(String token);
    List<String> activeUsers();
}
