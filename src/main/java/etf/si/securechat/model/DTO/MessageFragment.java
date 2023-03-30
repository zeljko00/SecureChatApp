package etf.si.securechat.model.DTO;

import lombok.Data;

// represents part of message
@Data
public class MessageFragment {
    private String recepient;
    private String content;
    private String token;
    // used for encryption
    private String nonce;
    private String sender;

    @Override
    public String toString() {
        return "MessageFragment{" +
                "recepient='" + recepient + '\'' +
                ", content='" + (content.length()>100?content.substring(0,100):content) + '\'' +
                ", token='" + token + '\'' +
                ", nonce='" + nonce + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
