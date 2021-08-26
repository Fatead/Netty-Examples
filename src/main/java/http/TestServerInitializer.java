package http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        //加入Netty提供的Http编-解码器HttpServerCodec
        channelPipeline.addLast("MyHttpServerCodeC",new HttpServerCodec());
        channelPipeline.addLast("MyTestHttpServerHandler",new TestHttpServerHandler());
        System.out.println("Initializer准备好了");
    }

}
