package com.test.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by admin on 2017/2/23.
 */
public class AioClientReadHandler implements CompletionHandler {
    private AsynchronousSocketChannel socketChannel;
    private CharsetDecoder charsetDecoder = Charset.forName("utf-8").newDecoder();

    public AioClientReadHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * Invoked when an operation has completed.
     *
     * @param result     The result of the I/O operation.
     * @param attachment
     */
    @Override
    public void completed(Object result, Object attachment) {
        Integer i = (Integer) result;
        ByteBuffer byteBuffer = (ByteBuffer) attachment;
        if (i > 0) {
            byteBuffer.flip();
            try {
                System.out.println("收到" + socketChannel.getRemoteAddress().toString() + "的消息：" + charsetDecoder.decode(byteBuffer));
                byteBuffer.compact();
                socketChannel.read(byteBuffer, byteBuffer, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (i == -1) {
            try {
                System.out.println("断线：" + socketChannel.getRemoteAddress().toString());
                byteBuffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
