package com.example.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将 RpcRequest 编码成字节序列发送
 * 消息格式: Length + Content
 * Length使用int存储,标识消息体的长度
 *
 * +--------+----------------+
 * | Length |  Content       |
 * |  4字节  |   Length个字节  |
 * +--------+----------------+
 */
public class JsonEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) {
		byte[] bytes = KryoUtil.writeToByteArray(obj);
		// 将消息体的长度写入消息头部
		out.writeInt(bytes.length);
		// 写入消息体
		out.writeBytes(bytes);
	}
}
