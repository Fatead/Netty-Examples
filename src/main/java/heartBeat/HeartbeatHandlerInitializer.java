package heartBeat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartbeatHandlerInitializer extends ChannelInitializer<Channel> {

    private static final int READ_IDEL_TIMEOUT = 4; //读超时时间
    private static final int WRITE_IDEL_TIMEOUT = 5; //写超时时间
    private static final int ALL_IDEL_TIMEOUT = 7; //所有超时时间

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addLast(new IdleStateHandler(READ_IDEL_TIMEOUT,WRITE_IDEL_TIMEOUT,ALL_IDEL_TIMEOUT, TimeUnit.SECONDS));
        channelPipeline.addLast(new HeartbeatServerHandler());
    }

}
