### 简介
根据RPC框架原理，自己手写的一个RPC框架。
### 框架技术方案
* 服务注册与发现：zookeeper
* 网络传输：netty
* 序列化：kryo
* 动态代理：JDK动态代理
### 启动流程
#### 服务端（提供者）
* 启动容器
* 将带有@RpcService的bean，放入Map<String, Object> services中，以ClassName为key,Bean对象为Value
* 将服务端Netty服务注册到Zookeeper中
#### 客户端（消费者）
* 启动容器
* 在RpcBeanDefinitionRegistryPostProcessor中传入需要扫描的接口路径（com.example.common.service），
  RpcScanner将接口路径下的接口和代理工厂类组装成BeanDefinition，以便之后调用时生成代理类。
* 启动netty服务
* 启动zookeeper客户端，连接服务端，监听节点变化，如果有节点，解析节点数据，netty客户端和服务端建立连接
* 连接成功后，加入连接管理器
### 调用流程
* 客户端调用提供者接口中的方法，实际执行RpcFactory<T>中的invoke方法，组装请求数据RpcRequest（类名，方法名，方法返回类型，方法传递参数
* 调用nettyClient.send(rpcRequest)方法，然后序列化数据为二进制，通过Netty的channel通道将数据传输到服务端。
* 服务端反序列化二进制数据为Java对象，通过反射调用类中的方法，将返回结果组装成RpcResponse对象
* 将RpcResponse对象序列化为二进制数据，通过Netty的channel通道传输到客户端
* 客户端调用getResult()方法获取返回结果
### 使用
* 1.在服务端的需要提供的服务加上@RpcService注解
```java
@RpcService
public class UserServiceImpl implements UserService {
	@Override
	public User findUserById(Integer id) {
		return new User(id,"张三");
	}
}
```
* 2.在com.example.consumer.remote包中声明服务提供者接口
```java
public interface UserService {
	public User findUserById(Integer id);
}
```
* 3.在客户端中调用远程服务
```java
@RestController
@RequiredArgsConstructor
public class TestController {
	private final UserService userService;
	@GetMapping("/test")
	public String testRpc(){
		userService.findUserById(123);
		return "success";
	}
}
```
### 最后
此框架仅供学习使用

