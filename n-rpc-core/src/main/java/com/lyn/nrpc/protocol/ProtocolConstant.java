package com.lyn.nrpc.protocol;

/**
 * 协议常量
 */
public class ProtocolConstant {
    /**
     * 协议头长度
     */
    public static final int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    public static final byte PROTOCOL_MAGIC = 0x01;

    /**
     * 协议版本号
     */
    public static final byte PROTOCOL_VERSION = 0x01;
}
