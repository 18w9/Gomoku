package com.wxz.tools;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NetWork {

    public static int PORT = 9999;//定义服务器监听的端口号

    ServerSocket ss;//服务器端使用的ServerSocket对象
    Socket s;//客户端与服务器之间的Socket对象
    BufferedReader reader;// 用于从输入流中读取文本的 BufferedReader
    PrintWriter writer;// 用于向输出流写入文本的 PrintWriter

    // 构造函数，用于服务器端
    public NetWork(ServerSocket paramSS) {
        ss = paramSS;//初始化ServerSock    et对象
    }

    // 构造函数，用于客户端
    public NetWork(Socket paramS) {
        s = paramS;//初始化Socket对象
        try {
            // 初始化 BufferedReader 和 PrintWriter
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));//从文本中读取数据
            writer = new PrintWriter(s.getOutputStream(), true);//向文本中写入数据
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();// 打印异常信息
        }
    }

    // 作为服务器端工具侦听 服务器端连接客户端
    public void doAccept() {
        try {
            // 此语句会阻塞进程
            // ServerSocket 调用 accept 方法等待客户端连接
            s = ss.accept();
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new PrintWriter(s.getOutputStream(), true);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 发送消息
    public void send(String str) {
        writer.println(str);
    }

    // 接收消息
    public String receive() {
        String str = "";
        try {
            // 从输入流读取一行文本
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    // 关闭连接
    public void close() {
        try {
            //关闭Socket连接和流
            s.close();
            writer.close();
            reader.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}