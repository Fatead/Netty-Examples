package rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    //调用返回的结果，在channelRead函数中获取结果，在call方法中返回
    private String result;
    //向服务器端写入的参数
    private String param;

    /**
     * 与服务器的连接创建后就会被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" channelActive被调用");
        context = ctx;
    }

    /**
     * 收到服务器的数据后调用该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(" channelRead被调用");
        result = msg.toString();
        //唤醒等待的线程，继续执行call方法
        notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    /**
     * 1.被代理对象调用，发送数据给服务器
     * 2.wait
     * 3.等待被唤醒
     * 4.返回结果
     * @return
     * @throws Exception
     */
    public synchronized Object call() throws Exception{
        System.out.println("call1被调用");
        context.writeAndFlush(param);
        //等待ChannelRead获取服务器的结果被唤醒，在得到返回的结果前，执行该方法的线程会被阻塞
        wait();
        System.out.println("call2被调用");
        //将服务端返回的结果返回给调用者
        return result;
    }

    public void setParam(String param) {
        this.param = param;
    }

}
