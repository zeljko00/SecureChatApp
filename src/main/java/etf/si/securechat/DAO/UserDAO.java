package etf.si.securechat.DAO;

import etf.si.securechat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
}
