package com.example.provider.netty;

import com.example.common.entity.RpcRequest;
import com.example.common.entity.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @ClassName NettyServerHandler
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/27 10:39
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	private Map<String, Object> rpcServices;

	public NettyServerHandler(Map<String, Object> rpcServices){
		this.rpcServices = rpcServices;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("客户端连接成功,{}", ctx.channel().remoteAddress());
	}

	public void channelInactive(ChannelHandlerContext ctx)   {
		log.info("客户端断开连接,{}", ctx.channel().remoteAddress());
		ctx.channel().close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		RpcRequest rpcRequest = (RpcRequest) msg;
		if ("heartBeat".equals(rpcRequest.getMethodName())) {
			log.info("客户端心跳信息..."+ctx.channel().remoteAddress());
		}else{
			log.info("接收到客户端请求, 请求接口:{}, 请求方法:{}", rpcRequest.getClassName(), rpcRequest.getMethodName());
			RpcResponse response = new RpcResponse();
			response.setRequestId(rpcRequest.getRequestId());
			Object result = null;
			try {
				result =handleRequest(rpcRequest);
				response.setResult(result);
			} catch (Exception e) {
				e.printStackTrace();
				response.setSuccess(false);
				response.setErrorMessage(e.getMessage());
			}
			log.info("服务器响应:{}", response);
			ctx.writeAndFlush(response);
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.info("连接异常");
		ctx.channel().close();
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			if (event.state()== IdleState.ALL_IDLE){
				log.info("客户端已超过60秒未读写数据, 关闭连接.{}",ctx.channel().remoteAddress());
				ctx.channel().close();
			}
		}else{
			super.userEventTriggered(ctx,evt);
		}
	}

	private Object handleRequest(RpcRequest rpcRequest) throws Exception{
		System.out.println(rpcServices.toString());
		Object bean = rpcServices.get(rpcRequest.getClassName());
		if(bean == null){
			throw new RuntimeException("未找到对应的服务: " + rpcRequest.getClassName());
		}
		Method method = bean.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
		method.setAccessible(true);
		return method.invoke(bean, rpcRequest.getParameters());
	}
}
