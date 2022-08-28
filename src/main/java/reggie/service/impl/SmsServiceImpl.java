package reggie.service.impl;

import reggie.entity.SmsField;
import reggie.service.SmsService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {


    @Override
    public void sendCode(String phone, String code) {
        try {

            /* 必要步骤：
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
             * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
             * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
             * 以免泄露密钥对危及你的财产安全。
             *
             *
             *
             * SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi */
            Credential cred = new Credential(SmsField.KEY_ID, SmsField.KEY_SECRET);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(60);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            /* 非必要步骤:
             * 实例化一个客户端配置对象，可以指定超时时间等配置
             */
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            SendSmsRequest req = new SendSmsRequest();

            // 设置 appid
            String appid = SmsField.SDK_ID;
            req.setSmsSdkAppid(appid);

            // 设置 signName 设置签名
            String signName = SmsField.SIGN_NAME;
            req.setSign(signName);

            // 设置 templateID 模板id
            String templateID = SmsField.TEMPLATE_CODE;
            req.setTemplateID(templateID);

            String[] templateParams = {code};

            phone = "+86" + phone;
            String[] phoneNumbers = {phone};
            req.setPhoneNumberSet(phoneNumbers);
            //执行发送
            req.setTemplateParamSet(templateParams);
            SendSmsResponse res = client.SendSms(req);
            //输出结果
            log.info(SendSmsResponse.toJsonString(res));
            //发送成功后，可以在此处将验证码存入Redis

            //没有自己调试的时候，可以暂时在控制台输出
            log.info(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


