package rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    //固定线程数的线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler client;
    private int count = 0;

    /**
     * 编写方法使用代理模式获取一个代理对象
     * newProxyInstance方法有三个参数
     * ClassLoader : 使用哪个类加载器去加载代理对象
     * Class</?>[] : 动态代理类需要实现的接口
     * InvocationHandler : 动态代理的方法在执行时，
     * 会调用InvocationHandler里面的invoke方法去执行
     */
    public Object getBean(final Class<?> serviceClass, final String providerName){
        //使用动态代理，在运行时创建一个给定接口的新类，代理的是接口，在运行时才知道具体的实现
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass},(proxy,method,args)->{
                    System.out.println("(proxy, method, args) 进入...." + (++count) + " 次");
                    if(client == null){
                        initClient();
                    }
                    //设置要发给服务器的消息
                    client.setParam(providerName + args[0]);
                    return executor.submit(client).get();
                });
    }


    /**
     * 初始化客户端
     */
    private static void initClient() throws InterruptedException {
        client = new NettyClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline channelPipeline = channel.pipeline();
                        channelPipeline.addLast(new StringEncoder());
                        channelPipeline.addLast(new StringDecoder());
                        channelPipeline.addLast(client);
                    }
                });
        bootstrap.connect("127.0.0.1",7000).sync();
    }

}
