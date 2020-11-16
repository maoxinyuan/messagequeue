package com.pacific.messagequeue.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maoxy
 * @date 2019/1/17 15:57
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static <T> T toObject(String string, Class<T> clazz) {
        try {
            return JSON.parseObject(string, clazz);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    public static <T> String toJson(T t) {
        try {
            return JSON.toJSONString(t);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }
}
