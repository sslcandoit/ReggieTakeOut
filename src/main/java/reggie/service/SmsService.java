package reggie.service;

public interface SmsService {
    public void sendCode(String phone, String code);
}
