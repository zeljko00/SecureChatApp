package etf.si.securechat.model.DTO;

import lombok.Data;

@Data
public class MessageFragment {
    private String recepient;
    private String content;
    private String token;
}
