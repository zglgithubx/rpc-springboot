package com.example.provider.netty;

import com.example.common.util.JsonDecoder;
import com.example.common.util.JsonEncoder;
import com.example.provider.annotation.RpcService;
import com.example.provider.zookeeper.ServerRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName NettyServer
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/27 10:37
 */
@Component
@Slf4j
public class NettyServer implements ApplicationContextAware, InitializingBean {

	//创建两个线程组 boosGroup、workerGroup
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);
	// RPC服务实现容器
	private Map<String, Object> rpcServices = new HashMap<>();
	@Value("${rpc.port}")
	private int port;

	@Resource
	private ServerRegistry serverRegistry;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> services = applicationContext.getBeansWithAnnotation(RpcService.class);
		for (Map.Entry<String, Object> entry : services.entrySet()) {
			Object bean = entry.getValue();
			Class<?>[] interfaces = bean.getClass().getInterfaces();
			for (Class<?> inter : interfaces) {
				rpcServices.put(inter.getName(),  bean);
			}
		}
		log.info("加载RPC服务数量:{}", rpcServices.size());
	}

	@Override
	public void afterPropertiesSet() {
		start();
	}

	private void start(){
		final NettyServerHandler handler = new NettyServerHandler(rpcServices);
		new Thread(() -> {
			try {
				//创建服务端的启动对象，设置参数
				ServerBootstrap bootstrap = new ServerBootstrap();
				//设置两个线程组boosGroup和workerGroup
				bootstrap.group(bossGroup, workerGroup)
						.childHandler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								ChannelPipeline pipeline = ch.pipeline();
								//给pipeline管道设置处理器
								pipeline.addLast(new IdleStateHandler(0, 0, 60));
								pipeline.addLast(new JsonDecoder());
								pipeline.addLast(new JsonEncoder());
								pipeline.addLast(handler);
							}
						})
						.channel(NioServerSocketChannel.class);
				//绑定端口号，启动服务端
				ChannelFuture future = bootstrap.bind(port).sync();
				log.info("RPC 服务器启动, 监听端口:" + port);
				//对关闭通道进行监听
				serverRegistry.register("127.0.0.1:"+port);
				future.channel().closeFuture().sync();
			}catch (Exception e){
				e.printStackTrace();
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		}).start();

	}
}
