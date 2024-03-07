package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 用户服务
 * @date 2021/2/22 15:36
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //redis key前缀
    private static final String KEY_PREFIX = "user:verify:phone:";

    /**
     * 校验用户数据
     *
     * @param data 要校验的数据
     * @param type 1用户名 2手机号
     * @return true可用 false不可用
     */
    public Boolean checkData(String data, Integer type) {


        //构建查询条件
        User record = new User();

        //判断数据类型
        switch (type) {
            case 1:
                //设置为用户名
                record.setUsername(data);
                break;
            case 2:
                //设置手机号
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }

        //查询count就好了，如果值为0就可用
        return userMapper.selectCount(record) == 0;
    }


    /**
     * 发送纯数字6位验证码
     *
     * @param phone 手机号
     */
    public void sendCode(String phone) {

        //redis的key
        String key = KEY_PREFIX + phone;

        //发送mq消息，到sms微服就可以了
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);

        String code = NumberUtils.generateCode(6);

        //生成6位纯数字验证码
        map.put("code", code);


        //使用mq发送异步消息，触发listener，发送验证码
        //指定exchange，因为没有在配置文件中指定，如果在配置文件中指定了交换机，只需要指定routingKey即可
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);


        //将验证码保存到redis 5分钟内有效
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);


    }

    /**
     * 用户注册
     *
     * @param user 用户属性
     * @param code 验证码
     */
    public void register( User user, String code) {
        //redis的key
        String key = KEY_PREFIX + user.getPhone();

        //1.判断验证码是否正确
        String codeInRedis = redisTemplate.opsForValue().get(key);

        if (!(StringUtils.equals(code, codeInRedis))) {
            //验证码不相同，抛出异常（无效的验证码）
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);

        }

        //生成salt
        String salt = CodecUtils.generateSalt();
        //替换密码  ：使用md5加密，并生成随机的salt值
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //记录salt值
        user.setSalt(salt);
        user.setCreated(new Date());

        //2.进行用户的新增
        userMapper.insert(user);



    }

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名跟
     * @param password 密码
     * @return user对象
     */
    public User queryUser(String username, String password) {

        User record = new User();
        record.setUsername(username);

        //只根据用户名进行查询，(username在数据库中为索引列,两个条件影响效率)
        User user = userMapper.selectOne(record);
        if (user == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }


        //然后再取出密码，  校验密码
        String pwd = user.getPassword();
        //将用户输入的密码和user中的slat值 生成密文  和数据库中的密码进行比较
        //user中加密的salt值和用户输入的值，进行加密和user的密文进行比较
        if (!(StringUtils.equals(CodecUtils.md5Hex(password,user.getSalt()),pwd))){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);

        }

        return user;
    }
}
