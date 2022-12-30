package com.example.consumer.netty;

import com.example.common.entity.RpcRequest;
import com.example.common.entity.RpcResponse;
import com.example.common.util.JsonDecoder;
import com.example.common.util.JsonEncoder;
import com.example.consumer.zookeeper.ChannelManage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * @ClassName NettyClient
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/26 21:25
 */
@Slf4j
@Component
public class NettyClient {


	private EventLoopGroup group = new NioEventLoopGroup(1);
	private Bootstrap bootstrap = new Bootstrap();

	@Resource
	private NettyClientHandler nettyClientHandler;

	@Resource
	private ChannelManage channelManage;

	@PostConstruct
	public void init(){
		System.out.println("启动netty客户端");
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						//心跳机制
						pipeline.addLast(new IdleStateHandler(0, 0, 10));
						pipeline.addLast(new JsonEncoder());
						pipeline.addLast(new JsonDecoder());
						pipeline.addLast(nettyClientHandler);
					}
				});
	}

	@PreDestroy
	public void destroy(){
		group.shutdownGracefully();
	}

	public RpcResponse send(RpcRequest rpcRequest) throws InterruptedException {
		Channel channel  = channelManage.chooseChannel();
		if(channel!=null&&channel.isActive()){
			log.info("连接建立, 发送请求:{}", rpcRequest);
			SynchronousQueue<RpcResponse> queue = nettyClientHandler.sendRequest(rpcRequest,channel);
			// 阻塞等待获取响应
			return queue.take();
		}else{
			RpcResponse res = new RpcResponse();
			res.setSuccess(false);
			res.setErrorMessage("未正确连接到服务器.请检查相关配置信息!");
			return res;
		}
	}

	public Channel doConnect(SocketAddress address) throws InterruptedException {
		ChannelFuture future = bootstrap.connect(address);
		Channel channel = future.sync().channel();
		return channel;
	}

}
