package com.test.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.Date;

/**
 * Created by admin on 2017/2/21.
 */
public class FileChannel {
    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("f:/fglog.txt", "rw");
        java.nio.channels.FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(50);
        int p = fileChannel.read(byteBuffer);
        while (p != -1) {
            byteBuffer.flip();
            String b = new String(byteBuffer.array());
            System.out.println(b);
            byteBuffer.clear();
            p = fileChannel.read(byteBuffer);
        }
        String newData = "new data" + new Date();
        byteBuffer.put(newData.getBytes());
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {

            fileChannel.write(byteBuffer);
        }
        fileChannel.close();
        randomAccessFile.close();

        Pipe pipe = Pipe.open();
        Pipe.SinkChannel sinkChannel = pipe.sink();
        String data = "new date=" + new Date();
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(48);
        byteBuffer1.clear();
        byteBuffer1.put(data.getBytes());
        byteBuffer1.flip();
        while (byteBuffer1.hasRemaining()) {
            sinkChannel.write(byteBuffer1);
        }
        ///
        Pipe.SourceChannel sourceChannel = pipe.source();
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(48);
        int f = sourceChannel.read(byteBuffer2);
        while(f!=-1){
            String s=new String(byteBuffer2.array());
            System.out.println(s);
            byteBuffer2.clear();
            f = sourceChannel.read(byteBuffer2);
        }
        sinkChannel.close();
        sourceChannel.close();

    }
}
