package reggie.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix="tencent.sms")
public class SmsField implements InitializingBean {
    private String sdkAppId;
    private String secretId;
    private String secretKey;
    private String templateId;
    private String signName;

    public static String SDK_ID;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String TEMPLATE_CODE;
    public static String SIGN_NAME;

    @Override
    public void afterPropertiesSet() throws Exception{
        SDK_ID=sdkAppId;
        KEY_ID=secretId;
        KEY_SECRET=secretKey;
        TEMPLATE_CODE=templateId;
        SIGN_NAME=signName;
    }
}
