package etf.si.securechat.model.DTO;

import lombok.Data;

import java.util.Arrays;
import java.util.Objects;
import java.util.List;

// represents data that is stored about users, that joined the chat
@Data
public class UserData {
    private String username;
    private String key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return username.equals(userData.getUsername());
    }
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return
                "{username=" + username +
                ", key=" + key+"}"
                ;
    }
}
