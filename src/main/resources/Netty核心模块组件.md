# Netty核心模块组件

### 1. Bootstrap、 ServerBootstrap
一个Netty应用通常应该由一个Bootstrap开始，主要作用是配置整个Netty程序，串联各个组件，Netty中的Bootstrap
类是客户端程序的启动引导类，ServerBootstrap是服务器端启动引导类

### 2. Future、ChannelFuture
Netty中所有的IO操作都是异步的，不能立刻得知消息是否被正确处理，但是可以过一会等它执行完或者直接注册一个监听，
具体的实现就是通过Future和ChannelFutures，他们可以注册一个监听，当操作执行成功或失败时监听会自动触发注册
的监听事件。

### 3. Channel
1. Netty网络通信的组件，能够用于执行网络的I/O操作
2. 通过Channel可以获得当前网络连接的通道的状态
3. 通过Channel可以获得网络连接的配置参数
4. 不同协议、不同阻塞类型的连接都有不同的Channel类型与之对应，常见的Channel类型
 + NioSocketChannel，异步的客户端TCP、 Socket连接
 + NioServerSocketChannel，异步的服务器端TCP、 Socket连接
 + NioDatagramChannel，异步的UDP连接
 + NioSctpChannel，异步的客户端 Sctp 连接
 + NioSctpServerChannel，异步的 Sctp 服务器端连接，这些通道涵盖了 UDP 和 TCP 网络 IO 以及文件 IO

### 4. Selector
1. Netty基于Selector对象实现I/O多路复用，通过Selector一个线程可以监听多个连接的Channel事件
2. 当向一个Selector中注册Channel后，Selector 内部的机制就可以自动不断地查询（Select）这些注册的 Channel 是否有已就绪的 I/O 事件（例如可读，可写，网络连接完成等），这样程序就可以很简单地使用一个线程高效地管理多个 Channel

### 5. ChannelHandler及其实现类
1. ChannelHandler是一个接口，处理I/O事件或者拦截I/O操作，并将其转发到ChannelPipeline中的下一处理程序
2. ChannelInboundHandler用于处理入站I/O事件
3. ChannelOutboundHandler用于处理出站I/O事件


### 6. Pipeline和ChannelPipeline
1. ChannelPipeline是一个Handler的集合，它负责处理和拦截inbound和outbound的事件和操作，
ChannelPipeline是保存ChannelHandler的双向链表，用于处理或拦截Channel的入站事件和出战操作。
   

### 7. ChannelHandlerContext
1. 保存Channel相关的所有上下文信息，同时关联一个ChannelHandler对象
2. ChannelHandlerContext中包含一个具体的事件处理器ChannelHandler，同时ChannelHandlerContext中也绑定了对应的pipeline和channel的信息


### 8. ChannelOption
Netty在创建Channel实例后，通过设置ChannelOption参数来对Channel参数进行设置

### 9. EventLoopGroup和其实现类NioEventLoopGroup
1. EventLoopGroup是一组EventLoop的抽象，Netty为了更好的利用多核CPU的资源，
一般会有多个EventLoop同时工作，每个EventLoop维护着一个Selector实例
   
2. EventLoopGroup提供next接口，可以从组里面按照一定规则获取其中一个EventLoop来处理任务

3. 通常一个服务端口即一个ServerSocketChannel对应一个Selector和一个EventLoop线程，BossEventLoop负责接收客户端的连接并将
SocketChannel交给WorkerEventLoopGroup来进行IO处理
   
### 10. Unpooled类
Netty提供的一个用于操作缓冲区的工具类