package etf.si.securechat.services;

import etf.si.securechat.model.DTO.ChatroomRequest;
import etf.si.securechat.model.DTO.UserData;
import etf.si.securechat.model.User;

import java.util.Optional;
import java.util.List;

public interface UserService {
    Optional<User> findCitizenById(long id);
    String login(String username, String password);
    boolean register(String username,String password);
    String logout(String token);
    UserData join(ChatroomRequest request);
    List<UserData> activeUsers();
}
