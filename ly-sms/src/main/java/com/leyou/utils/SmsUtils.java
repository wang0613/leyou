package com.leyou.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.leyou.conf.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 短信API产品的DEMO程序,工程中包含了一个SmsDemo类，直接通过
 * 执行main函数即可体验短信产品API功能(只需要将AK替换成开通了云通信-短信产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 * <p>
 * 备注:Demo工程编码采用UTF-8
 * 国际短信发送请勿参照此DEMO
 */
@Component
@EnableConfigurationProperties(SmsProperties.class) //使用配置
@Slf4j
public class SmsUtils {


    @Autowired
    private SmsProperties props;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public static final String KEY_PREFIX = "sms:phone:"; //key前缀
    public static final Long SMS_MIN_INTERVAL_IN_MILLIS = 60000L; //短信发送最小周期时间 1分钟

    //产品名称:云通信短信API产品,开发者无需替换
    final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 发送短息
     * @param phoneNumber   手机号
     * @param singName      签名
     * @param templateCode  模板
     * @param templateParam 模板参数
     * @return void
     * @throws ClientException 发送失败
     */
    public SendSmsResponse sendSms(String phoneNumber, String singName, String templateCode, String templateParam) {

        String key = KEY_PREFIX + phoneNumber;
        //TODO 使用redis 进行手机号的限流

        //读取发送短信的时间
        String lastTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(lastTime)) {
            //如果不为null，转化为Long值
            Long time = Long.valueOf(lastTime);
            //如果当前时间-发送的时间在1分钟内，不在发送
            if (System.currentTimeMillis() - time < SMS_MIN_INTERVAL_IN_MILLIS) {
                log.info("[短信服务]:发送短信频率过高，被拦截,手机号:{}",phoneNumber);
                return null;
            }

        }
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", props.getAccessKeyId(), props.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(singName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam(templateParam);


            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse resp = acsClient.getAcsResponse(request);

            //状态码ok为成功发送
            if (!resp.getCode().equals("OK")) {

                log.info("[短信服务]:发送短信失败,phoneNumber:{},原因:{}", phoneNumber, resp.getMessage());

            }
            //发送短信日志 成功
            log.info("[短信服务],发送短信验证码,手机号:{}",phoneNumber);

            //发送短信成功后，写入redis,key为手机号，value为当前时间，
            //并指定生存时间为1分钟，单位为minutes
            redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),1, TimeUnit.MINUTES);

            return resp;

        } catch (Exception e) {

            log.error("[短信服务] 发送短信异常,手机号码:{}", phoneNumber, e);
            return null;
        }
    }


}
