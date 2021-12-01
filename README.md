## dubbo25-hessian-patch

由于多种历史原因，环境中还有很多dubbo 2.5.x的版本在使用。
但是由于dubbo的bug，会导致hessian协议的attachment的丢失。

这个补丁修改了

* com.alibaba.dubbo.rpc.filter.ContextFilter
* com.alibaba.dubbo.rpc.protocol.hessian.HessianProtocol$HessianHandler
* com.caucho.hessian.client.HessianProxy

### 如何使用
> 该修复只在skywalking 8.1.0下测试过

下载代码，`mvn package` 拷贝`dubbo25-hessian-patch-8.1.0.jar`到 `<SK_AGENT>/plugins/`即可