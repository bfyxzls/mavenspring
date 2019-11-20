package com.lind.mavenspring;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioTest {
    @Test
    public void copyFile() throws IOException {
        // 设置输入源 & 输出地 = 文件
        String infile = "copy.txt";
        String outfile = "copy1.txt";

        // 1. 获取数据源 和 目标传输地的输入输出流（此处以数据源 = 文件为例）
        ClassLoader classLoader = NioTest.class.getClassLoader();
        File file = new File(classLoader.getResource(infile).getFile());
        File fileOutput = new File(classLoader.getResource(outfile).getFile());

        FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);

        // 2. 获取数据源的输入输出通道
        FileChannel fileChannelInput = fileInputStream.getChannel();
        FileChannel fileChannelOutput = fileOutputStream.getChannel();

        // 3. 创建缓冲区对象
        ByteBuffer buff = ByteBuffer.allocate(1024);

        while (true) {

            // 4. 从通道读取数据 & 写入到缓冲区
            // 注：若 以读取到该通道数据的末尾，则返回-1
            int r = fileChannelInput.read(buff);
            if (r == -1) {
                break;
            }
            // 5. 传出数据准备：调用flip()方法
            buff.flip();

            // 6. 从 Buffer 中读取数据 & 传出数据到通道
            fileChannelOutput.write(buff);

            // 7. 重置缓冲区
            buff.clear();

        }
    }

}

