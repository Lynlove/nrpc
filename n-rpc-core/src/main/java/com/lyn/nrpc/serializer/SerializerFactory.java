package com.lyn.nrpc.serializer;

import com.lyn.nrpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂（用于获取序列化器对象）
 */
public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取序列化器
     * @param key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
