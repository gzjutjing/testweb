package com.test.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

/**
 * Created by admin on 2017/2/23.
 */
public class AioClientConnectHandler implements CompletionHandler {
    private Integer content=0;

    public AioClientConnectHandler(Integer content) {
        this.content = content;
    }

    /**
     * Invoked when an operation has completed.
     *
     * @param result     The result of the I/O operation.
     * @param attachment
     */
    @Override
    public void completed(Object result, Object attachment) {
        AsynchronousSocketChannel socketChannel=(AsynchronousSocketChannel)attachment;
        try {
            socketChannel.write(ByteBuffer.wrap(String.valueOf(content).getBytes())).get();
            ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
            socketChannel.read(byteBuffer,byteBuffer,new AioServerReadHandler(socketChannel));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
