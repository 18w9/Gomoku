package com.wxz.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetWLANip {

    private boolean isWiFiConnected=false;//标识是否连接到wifi
    private String myIP=null;//本机的WiFi ip
    private String connecterIP=null;//本机连接的路由的IP
    private String connecterMAC=null;//本机连接的路由的MAC地址
    private boolean isConnecterHDCP=false;//本机连接的路由是否为HDCP动态

    //构造函数
    public GetWLANip() {
        //通过ipconfig获取WLAN的IP地址
        String str = Cmd.command("ipconfig");
        str = str + "\r\n";

        //获取无线局域网适配器WLAN段   //使用正则表达式匹配“无线局域网适配器 WLAN”部分
        String WLAN_str = null;
        Pattern pattern = Pattern.compile("无线局域网适配器 WLAN:\r\n\r\n[\\s\\S]*?\r\n\r\n");//设置正则表达式.[\\s\\S]*?为任意数量字符非贪婪正则表达式
        Matcher matcher = pattern.matcher(str);//对str进行正则表达式搜索
        if(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            WLAN_str = str.substring(start, end);
        }
        if(WLAN_str == null){
            return;
        }

        //执行“ARP-a”命令
        str = Cmd.command("arp -a");
        str = str + "\r\n";//增加用于确定文字分段的回车

        //获取myIP
        pattern = Pattern.compile("IPv4 地址.....:");//设置正则表达形式，[\\s\\S]*?为任意数量字符非贪婪正则表达式
        matcher = pattern.matcher(WLAN_str);//对str进行正则表达式搜索
        if(matcher.find()){
            int end = matcher.end();
            myIP = WLAN_str.substring(end).split("\r\n")[0];//首先截取匹配到的字符串，然后读到回车，获取ip地址
            isConnecterHDCP = true;
        }
        else {
            isWiFiConnected=false;
            return;
        }
        System.out.println(myIP);
    }

    public String getIP() {
        return myIP;
    }
}

class Cmd {
    // 静态方法，接受一个字符串参数 cmd，用于执行命令行命令
    public static String command(String cmd){//获取cmd输出
        try {
            // 使用 Runtime.getRuntime().exec(cmd) 执行命令，并返回一个 Process 对象
            Process process = Runtime.getRuntime().exec(cmd);
            // 关闭输出流以释放资源，因为在读取命令行输出时不需要写入任何内容
            if(process != null){
                process.getOutputStream().close();
            }
            // 获取命令行进程的输入流，从中读取命令的输出
            InputStream in = process.getInputStream();
            // 使用 BufferedReader 包装输入流，以便按行读取
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // 创建 StringBuilder 对象用于构建最终的输出字符串
            StringBuilder result = new StringBuilder();
            String tmp = null;
            // 循环读取命令输出的每一行，直到没有更多行可读
            while ((tmp = br.readLine()) != null) {
                // 将读取到的每一行追加到 StringBuilder 对象中，并添加一个回车符
                result.append(tmp+"\r\n");//将tmp内容附加（append）到StringBuilder类中
            }
            return result.toString();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
            return null;
        }
    }
}
