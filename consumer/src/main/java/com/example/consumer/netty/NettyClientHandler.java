package com.example.consumer.netty;

import com.example.common.entity.RpcRequest;
import com.example.common.entity.RpcResponse;
import com.example.consumer.zookeeper.ChannelManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * @ClassName NettyClientHandler
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/27 09:25
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	private ConcurrentHashMap<String,SynchronousQueue<RpcResponse>> results = new ConcurrentHashMap<>();
	@Resource
	private ChannelManage channelManage;

	public void channelInactive(ChannelHandlerContext ctx)   {
		InetSocketAddress address =(InetSocketAddress) ctx.channel().remoteAddress();
		log.info("与RPC服务器断开连接."+address);
		ctx.channel().close();
		channelManage.removeChannel(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RpcResponse rpcResponse = (RpcResponse) msg;
		log.info("收到服务器响应:{}", rpcResponse);
		if(!rpcResponse.isSuccess()){
			throw new RuntimeException("调用结果异常,异常信息:" + rpcResponse.getErrorMessage());
		}
		// 取出结果容器,将response放进queue中
		SynchronousQueue<RpcResponse> rpcResponses = results.get(rpcResponse.getRequestId());
		rpcResponses.put(rpcResponse);
		results.remove(rpcResponse.getRequestId());
	}

	public SynchronousQueue<RpcResponse> sendRequest(RpcRequest request, Channel channel) {
		SynchronousQueue<RpcResponse> queue = new SynchronousQueue<>();
		results.put(request.getRequestId(), queue);
		channel.writeAndFlush(request);
		return queue;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			if (event.state() == IdleState.ALL_IDLE){
				log.info("发送心跳包");
				RpcRequest request = new RpcRequest();
				request.setMethodName("heartBeat");
				ctx.channel().writeAndFlush(request);
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		log.info("异常:{}", cause.getMessage());
		ctx.channel().close();
	}
}
