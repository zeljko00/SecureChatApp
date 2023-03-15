package etf.si.securechat.services.implementations;

import etf.si.securechat.DAO.UserDAO;
import etf.si.securechat.model.DTO.JwtUser;
import etf.si.securechat.model.User;
import etf.si.securechat.services.JwtUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements JwtUserDetailsService {

    private final UserDAO userDAO;

    public JwtUserDetailsServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findUserByUsername(username);
        if (user != null) {
            JwtUser result=new JwtUser();
            result.setUsername(user.getUsername());
            result.setPassword(user.getPassword());
            result.setId(user.getId());
            return result;
        }
        else
            throw new UsernameNotFoundException(username);
    }
}
