package com.example.consumer.zookeeper;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ServiceDiscovery
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/28 15:35
 */
@Slf4j
@Component
public class ServiceDiscovery {

	@Value("${rpc.registryAddress}")
	private String registryAddress;

	@Resource
	private ChannelManage connectManage;

	// 服务地址列表
	private volatile List<String> addressList = new ArrayList<>();
	private static final String ZK_REGISTRY_PATH = "/rpc";
	private ZkClient client;

	@PostConstruct
	public void init(){
		client = connectServer();
		if (client != null) {
			watchNode(client);
		}
	}

	private ZkClient connectServer() {
		ZkClient client = new ZkClient(registryAddress,20000,20000);
		return client;
	}
	private void watchNode(final ZkClient client) {
		List<String> nodeList = client.subscribeChildChanges(ZK_REGISTRY_PATH, (s, nodes) -> {
			log.info("监听到子节点数据变化{}", JSONObject.toJSONString(nodes));
			addressList.clear();
			getNodeData(nodes);
			updateConnectedServer();
		});
		getNodeData(nodeList);
		log.info("已发现服务列表...{}", JSONObject.toJSONString(addressList));
		updateConnectedServer();
	}
	private void updateConnectedServer(){
		connectManage.updateConnectServer(addressList);
	}

	private void getNodeData(List<String> nodes){
		log.info("/rpc子节点数据为:{}", JSONObject.toJSONString(nodes));
		for(String node:nodes){
			String address = client.readData(ZK_REGISTRY_PATH+"/"+node);
			addressList.add(address);
		}
	}
}
