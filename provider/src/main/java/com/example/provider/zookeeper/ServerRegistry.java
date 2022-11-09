package com.example.provider.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName NettyServerRegistry
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/28 15:19
 */
@Component
@Slf4j
public class ServerRegistry {
	@Value("${rpc.registryAddress}")
	private String registryAddress;

	private static final String ZK_REGISTRY_PATH = "/rpc";

	public void register(String data) {
		if (data != null) {
			ZkClient client =new ZkClient(registryAddress,20000,20000);
			if (client != null) {
				AddRootNode(client);
				createNode(client, data);
			}
		}
	}

	private void AddRootNode(ZkClient client){
		boolean exists = client.exists(ZK_REGISTRY_PATH);
		if (!exists){
			client.createPersistent(ZK_REGISTRY_PATH);
			log.info("创建zookeeper主节点 {}",ZK_REGISTRY_PATH);
		}
	}

	private void createNode(ZkClient client, String data) {
		//data为服务的地址127.0.0.1:8989
		String path = client.create(ZK_REGISTRY_PATH + "/provider", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		log.info("创建zookeeper数据节点 ({} => {})", path, data);
	}
}
