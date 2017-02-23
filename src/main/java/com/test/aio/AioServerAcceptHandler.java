package com.test.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by admin on 2017/2/23.
 */
public class AioServerAcceptHandler implements CompletionHandler {
    /**
     * Invoked when an operation has completed.
     *
     * @param result     The result of the I/O operation.
     * @param attachment
     */
    @Override
    public void completed(Object result, Object attachment) {
        AsynchronousSocketChannel socketChannel = (AsynchronousSocketChannel) result;
        AsynchronousServerSocketChannel serverSocketChannel = (AsynchronousServerSocketChannel) attachment;
        System.out.println("handler completed called");
        serverSocketChannel.accept(serverSocketChannel, this);
        try {
            System.out.println("有客户端连接：" + socketChannel.getRemoteAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer, byteBuffer, new AioServerReadHandler(socketChannel));
    }

    /**
     * Invoked when an operation fails.
     *
     * @param exc        The exception to indicate why the I/O operation failed
     * @param attachment
     */
    @Override
    public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
    }
}
