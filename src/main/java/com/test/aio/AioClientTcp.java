package com.test.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2017/2/23.
 */
public class AioClientTcp {
    private AsynchronousChannelGroup channelGroup;
    private CharsetDecoder decoder = Charset.forName("utf-8").newDecoder();

    public AioClientTcp() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        channelGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 5);
    }

    public void start(String ip, int port) throws IOException {
        for (int i = 0; i < 2000; i++) {
            AsynchronousSocketChannel socketChannel = null;
            if (socketChannel == null || !socketChannel.isOpen()) {
                socketChannel = AsynchronousSocketChannel.open(channelGroup);
            }
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.connect(new InetSocketAddress(ip, port), socketChannel, new AioClientConnectHandler(i));
        }
    }

    public static void main(String[] args) throws IOException {
        AioClientTcp aioTcpClient = new AioClientTcp();
        aioTcpClient.start("localhost", 4000);
    }
}
