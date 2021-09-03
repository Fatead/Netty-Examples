package rpc.customer;

import rpc.netty.NettyClient;
import rpc.publicinterface.HelloService;

public class ClientBootstrap {

    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {
        NettyClient customer = new NettyClient();
        //创建代理对象
        HelloService service = (HelloService) customer.getBean(HelloService.class, providerName);
        for (;;){
            Thread.sleep(2*1000);
            String result = service.hello("你好，RPC");
            System.out.println("调用结果为：" + result);
        }
    }

}
