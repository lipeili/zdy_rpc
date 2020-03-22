package com.lagou.util;


import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * @Description TODO
 * @Date 2020-03-21 22:25
 * @Created by videopls
 */
public class JSONSerializer implements Serializer {


    public byte[] serialize(Object object) throws IOException {
        return JSON.toJSONBytes(object);
    }

    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes, clazz);
    }

}
