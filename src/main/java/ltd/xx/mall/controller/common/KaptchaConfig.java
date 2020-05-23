package ltd.xx.mall.controller.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.google.code.kaptcha.util.Config;

import java.util.Properties;
/**
*   @Description 用于配置验证码
*   @Param null
*   @Return defaultKaptcha
*   @Author hyc
*   @Date 5.20
*/
@Component
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        com.google.code.kaptcha.impl.DefaultKaptcha defaultKaptcha = new com.google.code.kaptcha.impl.DefaultKaptcha();
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        //验证码边框配置
        properties.put("kaptcha.textproducer.font.color", "black");
        //验证码字体颜色
        properties.put("kaptcha.image.width", "150");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.textproducer.font.size", "30");
        //验证码字体大小
        properties.put("kaptcha.session.key", "verifyCode");
        //将验证码放入session
        properties.put("kaptcha.textproducer.char.length", "3");
        //验证码长度
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}
