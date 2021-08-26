package tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty抽象出两组线程池，BossGroup专门负责接收客户端的连接，WorkerGroup专门负责网络的读写
 * BossGroup和WorkGroup的类型都是NioEventLoopGroup
 * NioEventLoopGroup相当于一个事件循环组，这个组中包含多个事件循环，每一个事件循环是一个NioEventLoop
 * NioEventLoop表示一个不断循环的执行处理任务的线程，每个NioEventLoop都有一个Selector，用于监听绑定在其上的socket网络通讯
 * NioEventLoopGroup可以有多个线程，可以含有多个NioEventLoop
 *
 * 每个BossNioEventLoop循环执行的步骤有三步
 *  + 轮询accept事件
 *  + 处理accept事件，与client建立连接，生成NioSocketChannel，并将其注册到某个work-NioEventLoop上的Selector
 *  + 处理任务队列中的任务，即runAllTasks
 *
 *  每个Worker-NioEventLoop循环执行的步骤
 *  + 轮询read，write事件
 *  + 处理I/O事件，即read和write事件，在对应的NioSocketChannel处理
 *  + 处理任务队列中的任务，即runAllTasks
 *
 *  每个work-NIOEventLoop处理业务时，会使用pipeline，pipeline中包含了channel，即通过pipeline可以获取对应的管道，管道中维护了很多处理器
 *  NioEventLoop内部采用串行化设计，从消息的 读取->解码->处理->编码->发送， 始终由NioEventLoop负责
 *  NioEventLoopGroup下包含多个NioEventLoop
 *  + 每个NioEventLoop中包含一个Selector，一个taskQueue
 *  + 每个NioEventLoop的Selector上可以注册监听多个NioChannel
 *  + 每个NioChannel都会绑定在唯一的NioEventLoop上
 *  + 每个NioChannel都绑定有一个自己的ChannelPipeline
 *
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        /*
        创建两个线程组 bossGroup 和 workerGroup
        默认线程数： cpu核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)    //设置两个线程组
                    .channel(NioServerSocketChannel.class)  //将服务器默认的通道实现设置为NioServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // childHandler对应workerGroup
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("客户 SocketChannel hashcode :" + socketChannel.hashCode());
                            //给pipeline设置处理器
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给WorkerGroup的EventLoop对应的管道设置处理器
            System.out.println("服务器已经准备好了...");
            //绑定一个端口并同步，生成一个ChannelFuture对象，启动服务器并绑定端口
            ChannelFuture cf = bootstrap.bind(6668).sync();
            //给Channel Future注册监听器，监控我们关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("监听端口 6668 成功");
                    }else {
                        System.out.println("监听端口 6668 失败");
                    }
                }
            });
            //对于关闭通道事件进行监听
            cf.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
