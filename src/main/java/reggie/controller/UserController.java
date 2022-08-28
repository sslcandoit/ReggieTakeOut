package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.math.RandomUtils;
import cn.hutool.core.util.RandomUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.User;
import reggie.service.SmsService;
import reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private SmsService smsService;
    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R sendMsg(@RequestBody User user){
        String phone=user.getPhone();

        String code= RandomUtil.randomNumbers(6);
        log.info("code={}", code);

       // smsService.sendCode(phone, code);

        redisTemplate.opsForValue().set("code:"+phone, code, 5, TimeUnit.MINUTES);

        return R.success("发送验证码成功！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从redis(Session)中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);
        Object codeInRedis = redisTemplate.opsForValue().get((phone));

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInRedis != null && codeInRedis.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            redisTemplate.delete(phone);//如果登录成功，删除Redis缓存的验证码
            return R.success(user);
        }
        return R.error("登录失败");
    }

}
