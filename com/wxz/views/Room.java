package com.wxz.views;

import com.wxz.game.Window;
import com.wxz.tools.NetWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Room extends JPanel implements ActionListener {

    NetWork net;        // 联机和收发消息的工具
    // 0,xxxx,0     普通信息
    // 1,xxxx,0   一般控制信息
    // 1,游戏开始,0/1	游戏开始控制信息，0/1代表是否为先手

    JTextArea show;        // 显示消息  JTextArea用于显示消息的文本区域组件
    JTextField input;        // 输入信息 JTextField用于输入信息的文本字段组件

    JButton exit;            // 离开 按钮
    JButton ready;            // 准备 按钮
    JButton cancelReady;    // 取消准备 按钮
    JButton send;            // 发送 按钮

    boolean gameStart = false;// 标记游戏是否已经开始的标志变量

    Window window = null;// 声明一个 Window 类型的成员变量，用于引用窗口对象，初始值为 null

    // 构造函数，接收一个 Window 对象和一个 NetWork 对象作为参数
    public Room(Window win, NetWork paramNet) {

        window = win;// 将传入的 Window 对象赋值给成员变量 window
        net = paramNet;// 将传入的 NetWork 对象赋值给成员变量 net

        this.setSize(950, 662);                                        // 设置面板大小
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        JLabel label1 = new JLabel("房间信息"); // 创建一个新的 JLabel 实例，用于显示文本 "房间信息"
        label1.setFont(new Font("黑体", Font.BOLD, 30));
        label1.setBounds(50, 150, 300, 50);
        this.add(label1);

        JLabel label2 = new JLabel(">");
        label2.setFont(new Font("黑体", Font.BOLD, 30));
        label2.setBounds(30, 595, 30, 30);
        this.add(label2);

        // 创建一个新的 JPanel 实例，使用 BorderLayout 作为其布局管理器
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBounds(50, 200, 600, 370);
        // 创建一个新的 JTextArea 实例，用于显示多行文本
        show = new JTextArea();
        show.setFont(new Font("宋体", Font.BOLD, 22));
        show.setEditable(false);                        // 设置文本区不可编辑
        show.setLineWrap(true);                            // 激活自动换行功能
        show.setWrapStyleWord(true);                    // 激活断行不断字功能
        JScrollPane scroll = new JScrollPane(show);        // 添加滚动条
        // 将滚动面板添加到 p1 面板的中心位置
        p1.add(scroll, BorderLayout.CENTER);
        // 将 p1 面板添加到当前 JPanel（或其他容器）中
        this.add(p1);

        // 创建一个新的 JTextField 实例，初始时可以输入大约50个字符
        input = new JTextField(50);
        input.setFont(new Font("宋体", Font.BOLD, 22));//设置字体样式和大小
        input.setBounds(50, 590, 600, 45);// 设置输入框的位置和大小
        input.setText("输入信息后点击发送进行聊天");
        this.add(input);

        exit = new JButton("离开");
        exit.setBounds(680, 200, 260, 70);
        exit.setFocusPainted(false);
        exit.setFont(new Font("黑体", Font.BOLD, 30));
        exit.setBackground(new Color(0xffededed));
        exit.addActionListener(this);
        this.add(exit);

        ready = new JButton("准备");
        ready.setBounds(680, 310, 260, 70);
        ready.setFocusPainted(false);
        ready.setFont(new Font("黑体", Font.BOLD, 30));
        ready.setBackground(new Color(0xffededed));
        ready.addActionListener(this);
        this.add(ready);

        cancelReady = new JButton("取消准备");
        cancelReady.setBounds(680, 420, 260, 70);
        cancelReady.setFocusPainted(false);
        cancelReady.setFont(new Font("黑体", Font.BOLD, 30));
        cancelReady.setBackground(new Color(0xffededed));
        cancelReady.addActionListener(this);
        this.add(cancelReady);
        cancelReady.setEnabled(false);

        send = new JButton("发送");
        send.setBounds(680, 530, 260, 70);
        send.setFocusPainted(false);
        send.setFont(new Font("黑体", Font.BOLD, 30));
        send.setBackground(new Color(0xffededed));
        send.addActionListener(this);
        this.add(send);

        show.append("已进入房间\n");
        show.append("--------------------------------\n");

// 创建一个新的线程来异步接收和处理网络消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用无限循环来持续检查游戏是否已经开始
                while (! gameStart) {
                    try {
                        // 让线程暂停30毫秒，以减少CPU资源的占用
                        Thread.sleep(30);
                    } catch (Exception e) {
                        // 如果发生异常（如InterruptedException），则捕获并处理异常
                    }

                    // 接收从网络发送过来的消息
                    String[] info = net.receive().split(",");
                    // 检查消息类型是否为系统控制信息（"1"）
                    if (info[0].equals("1")) {
                        // 如果消息内容表示“房主已离开”，则在文本区域显示消息并关闭网络连接
                        if (info[1].equals("房主已离开")) {
                            show.append("[系统消息]" + info[1] + "\n");
                            show.setCaretPosition(show.getDocument().getLength());
                            net.close();
                            break;
                        }
                        // 如果消息内容表示“游戏开始”，则发送确认消息，更新文本区域，并切换到游戏视图
                        else if (info[1].equals("游戏开始")) {
                            net.send("1,确认游戏开始,0");
                            show.append("[系统消息]" + info[1] + "\n");
                            show.setCaretPosition(show.getDocument().getLength());
                            // 根据接收到的信息决定谁先手，并切换到游戏视图
                            window.setCurrentPanel(new GameView(window, net, info[2].equals("1") ? true : false));
                            gameStart = true;
                            break;
                        }
                    }
                    // 如果消息类型为普通信息（"0"），则在文本区域显示这条消息
                    else if (info[0].equals("0")) {
                        show.append("-- " + info[1] + "\n");
                        show.setCaretPosition(show.getDocument().getLength());
                    }
                }
            }
        }).start(); // 启动线程
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ImageIcon icon = new ImageIcon("D:\\JavaWork\\Gomoku\\project4\\src\\image\\background1.png");
        Image img = icon.getImage();
        g.drawImage(img, 0, 0, 1290, 722, this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 检查动作事件的命令字符串是否匹配"离开"
        if (e.getActionCommand().equals("离开")) {
            // 发送玩家离开房间的消息到服务器
            net.send("1,玩家已离开,0");
            // 关闭网络连接
            net.close();
            // 切换到进入房间的视图
            window.setCurrentPanel(new EnterRoomView(window));
        }
        // 检查动作事件的命令字符串是否匹配"准备"
        else if (e.getActionCommand().equals("准备")) {
            // 发送玩家已准备的消息到服务器
            net.send("1,玩家已准备,0");
            // 在文本区域显示系统消息
            show.append("[系统消息]已准备\n");
            // 将光标移动到文本区域的末尾
            show.setCaretPosition(show.getDocument().getLength());
            // 禁用"准备"按钮
            ready.setEnabled(false);
            // 启用"取消准备"按钮
            cancelReady.setEnabled(true);
        }
        // 检查动作事件的命令字符串是否匹配"取消准备"
        else if (e.getActionCommand().equals("取消准备")) {
            // 发送玩家取消准备的消息到服务器
            net.send("1,玩家取消准备,0");
            // 在文本区域显示系统消息
            show.append("[系统消息]已取消准备\n");
            // 将光标移动到文本区域的末尾
            show.setCaretPosition(show.getDocument().getLength());
            // 启用"准备"按钮
            ready.setEnabled(true);
            // 禁用"取消准备"按钮
            cancelReady.setEnabled(false);
        }
        // 检查动作事件的命令字符串是否匹配"发送"
        else if (e.getActionCommand().equals("发送")) {
            // 获取输入框中的文本
            String str = input.getText();
            // 如果输入框不为空
            if (! str.equals("")) {
                // 清空输入框
                input.setText("");
                // 发送普通消息到服务器
                net.send("0," + str + ",0");
                // 在文本区域显示发送的消息
                show.append(">> " + str + "\n");
                // 将光标移动到文本区域的末尾
                show.setCaretPosition(show.getDocument().getLength());
            }
        }
    }
}