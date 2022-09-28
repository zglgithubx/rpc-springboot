package com.example.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 将响应消息解码成 RpcResponse
 */
public class JsonDecoder extends LengthFieldBasedFrameDecoder {

	public JsonDecoder(){
		super(Integer.MAX_VALUE, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf msg = (ByteBuf) super.decode(ctx, in);
		byte[] bytes = new byte[msg.readableBytes()];
		msg.readBytes(bytes);
		return KryoUtil.readFromByteArray(bytes);
	}
}
