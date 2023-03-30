package etf.si.securechat.model.DTO;

import lombok.Data;


// represents user's request to join chat
@Data
public class ChatroomRequest {
    // user's public key, used for encryption
    private String key;
    // users's JWT generated after successful login
    private String token;

    @Override
    public String toString() {
        return
                "key=" + key +
                ", token='" + token ;
    }
}
