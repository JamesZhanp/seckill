package com.james.secondkill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 09 21:27
 */
@Service
public class RedisService {
    /**
    *  通过连接池对象活得redis的对象
    *  */
    @Autowired
    JedisPool jedisPool;

    /**
     *  redis的get操作，通过key获取存储在redis中的对象
     * @param prefix key的前缀
     * @param key 业务层传入的key
     * @param clazz 存储在redis中的对象泪行
     * @param <T> 值定对象对应的泪行
     * @return 存储在redis中的对象
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
              //生成真正的存储与redis的key
            String realKey = prefix.getPrefix() + key;
             //通过key获取存储于redis中的对象
            String strValue = jedis.get(realKey);
            T objValue = stringToBean(strValue, clazz);
            return objValue;
        }finally {
            //释放jedis连接对象
            returnToPool(jedis);
        }
    }


    /**
     * redis的set操作
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String strValue = beanToString(value);

            if (strValue == null || strValue.length() == 0){
                return false;
            }

            String realKey = prefix.getPrefix();

            int seconds = prefix.expireSeconds();
            if (seconds <= 0){
                jedis.set(realKey, strValue);
            }else{
                jedis.setex(realKey, seconds, strValue);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在与redis中
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 自增
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 自减
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */

    public <T> Long decr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除缓存中的数据
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long del = jedis.del(realKey);
            return del > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将对象转换成json字符串
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value){
        if (value == null){
            return null;
        }

        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class){
            return "" + value;
        }else if(clazz == long.class || clazz == Long.class){
            return "" + value;
        }else if(clazz == String.class){
            return (String) value;
        }else{
            return JSON.toJSONString(value);
        }
    }

    /**
     * 将json自付出按转换成对应类型的class
     * @param strValue
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String strValue, Class<T> clazz){

        if ((strValue == null) || (strValue.length() == 0)){
            return null;
        }
        // int or Integer
        if ((clazz == int.class) || (clazz == Integer.class))
        {return (T) Integer.valueOf(strValue);}
            // long or Long
        else if ((clazz == long.class) || (clazz == Long.class))
        {return (T) Long.valueOf(strValue);}
            // String
        else if (clazz == String.class)
        {   return (T) strValue;}
        // 对象类型
        return JSON.toJavaObject(JSON.parseObject(strValue),clazz);
    }

    /**
     * 将redis连接对象释放
     * @param jedis
     */
    private void returnToPool(Jedis jedis){
        if (jedis != null){
            jedis.close();
        }
    }
}
