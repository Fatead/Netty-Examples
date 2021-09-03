package rpc.provider;

import rpc.netty.NettyServer;

public class ServerBootstrap {

    public static void main(String[] args) throws InterruptedException {
        NettyServer.startServer("127.0.0.1", 7000);
    }

}
