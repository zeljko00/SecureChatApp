package etf.si.securechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerBean {
    private Logger logger=LoggerFactory.getLogger("security");

    public void logSecurityRisk(String msg){
        logger.warn(msg);
    }

@Bean
    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
