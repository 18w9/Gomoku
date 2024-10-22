package com.wxz.views;


import com.wxz.tools.GetWLANip;
import com.wxz.game.Window;
import com.wxz.tools.NetWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

public class HostRoom extends JPanel implements ActionListener {

    NetWork net = null;		 // 网络对象，用于处理联机和收发消息的工具
    // 0,xxxx,0     普通信息
    // 1,xxxx,0   一般控制信息
    // 1,游戏开始,0/1	游戏开始控制信息，0/1代表是否为先手

    JTextArea show;			// 显示消息的文本区域
    JTextField input;		// 输入信息的文本框

    JButton launch;			// 启动房间，启动后服务器才开启
    JButton start;			// 开始游戏 按钮
    JButton exit;			// 关闭 按钮
    JButton send;			// 发送 按钮

    boolean connect = false;	// 连接状态
    boolean gameStart = false;//游戏开始状态

    Window window = null;//窗口对象引用

    public HostRoom(Window win) {

        this.window = win;//初始化窗口对象

        // 设置面板大小、布局和背景颜色
        this.setSize(950, 662);										// 设置面板大小
        this.setLayout(null);
        this.setBackground(Color.WHITE);


        // 初始化并添加 "房间信息" 标签
        JLabel label1 = new JLabel("房间信息");
        label1.setFont(new Font("黑体", Font.BOLD, 30));
        label1.setBounds(50, 150, 300, 50);
        this.add(label1);

        // 初始化并添加 ">" 标签
        JLabel label2 = new JLabel(">");
        label2.setFont(new Font("黑体", Font.BOLD, 30));
        label2.setBounds(30, 595, 30, 30);
        this.add(label2);

        // 初始化并添加显示消息的文本区域
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBounds(50, 200, 600, 370);
        show = new JTextArea();
        show.setFont(new Font("宋体", Font.BOLD, 22));
        show.setEditable(false);						// 设置文本区不可编辑
        show.setLineWrap(true);        					// 激活自动换行功能
        show.setWrapStyleWord(true);            		// 激活断行不断字功能
        JScrollPane scroll = new JScrollPane(show);		// 添加滚动条
        p1.add(scroll, BorderLayout.CENTER);
        this.add(p1);

        // 初始化并添加输入信息的文本框
        input = new JTextField(50);
        input.setFont(new Font("宋体", Font.BOLD, 22));
        input.setBounds(50, 590, 600, 45);
        input.setText("输入信息后点击发送进行聊天");
        this.add(input);

        // 初始化并添加 "离开" 按钮，并设置其属性和事件监听器
        exit = new JButton("离开");
        exit.setBounds(680, 200, 260, 70);
        exit.setFocusPainted(false);
        exit.setFont(new Font("黑体", Font.BOLD, 30));
        exit.setBackground(new Color(0xffededed));
        exit.addActionListener(this);
        this.add(exit);

        // 初始化并添加 "启动房间" 按钮，并设置其属性和事件监听器
        launch = new JButton("启动房间");
        launch.setBounds(680, 310, 260, 70);
        launch.setFocusPainted(false);
        launch.setFont(new Font("黑体", Font.BOLD, 30));
        launch.setBackground(new Color(0xffededed));
        launch.addActionListener(this);
        this.add(launch);

        // 初始化并添加 "开始游戏" 按钮，并设置其属性和事件监听器
        start = new JButton("开始游戏");
        start.setBounds(680, 420, 260, 70);
        start.setFocusPainted(false);
        start.setFont(new Font("黑体", Font.BOLD, 30));
        start.setBackground(new Color(0xffededed));
        start.addActionListener(this);
        this.add(start);
        start.setEnabled(false);//初始时禁止开始游戏按钮

        // 初始化并添加 "发送" 按钮，并设置其属性和事件监听器
        send = new JButton("发送");
        send.setBounds(680, 530, 260, 70);
        send.setFocusPainted(false);
        send.setFont(new Font("黑体", Font.BOLD, 30));
        send.setBackground(new Color(0xffededed));
        send.addActionListener(this);
        this.add(send);
        send.setEnabled(false);//初始时禁止发送按钮

        this.setVisible(true);
    }

    // 重写 paintComponent 方法用于自定义面板的绘制
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ImageIcon icon = new ImageIcon("D:\\JavaWork\\Gomoku\\project4\\src\\image\\background1.png");
        Image img = icon.getImage();
        g.drawImage(img, 0, 0, 1290, 722, this);//绘制背景图片

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (e.getActionCommand() == "离开") {
            // 如果已经连接，则发送离开消息并关闭连接
            if (connect == true) {
                net.send("1,房主已离开,0");
                net.close();
            }
            // 切换回欢迎界面
            window.setCurrentPanel(new Welcome(window));
        }
        else if (e.getActionCommand() == "启动房间") {
            // 在文本组件中显示房间已对外开放的消息
            show.append("房间已对外开放\n");
            try {
                // 获取本机IP地址并显示
                String str = new GetWLANip().getIP();
                show.append("IP Address:     " + str + "\n");
                show.append("--------------------------------\n");
                // 滚动文本组件到底部
                show.setCaretPosition(show.getDocument().getLength());
                // 滚动文本组件到底部
                launch.setEnabled(false);

                // 创建一个新的NetWork对象，用于监听连接请求
                net = new NetWork(new ServerSocket(NetWork.PORT));

                // 开启线程用于接收房间的消息
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 接受连接请求
                        net.doAccept();
                        connect = true;
                        // 启用发送按钮
                        send.setEnabled(true);

                        // 循环监听网络消息
                        while (!gameStart) {
                            // 短暂等待，防止CPU占用过高
                            try{ Thread.sleep(30); } catch (Exception e) { };
                            // 接收消息并分割成数组
                            String []info = net.receive().split(",");
                            // 根据消息类型进行处理
                            if (info[0].equals("1")) {
                                // 处理不同类型的系统消息
                                if (info[1].equals("玩家已准备")) {
                                    // 将文本区域追加一条系统消息，表示有玩家已经准备好了。
                                    show.append("[系统消息]" + info[1] + "\n");
                                    // 移动文本区域的插入点到最后，这样新的消息会显示在文本区域的底部。
                                    show.setCaretPosition(show.getDocument().getLength());
                                    // 启用"开始游戏"按钮，因为至少有一个玩家已经准备好了。
                                    start.setEnabled(true);
                                }
                                // 如果接收到的消息内容为"玩家已离开"，执行以下操作：
                                else if (info[1].equals("玩家已离开")) {
                                    show.append("[系统消息]" + info[1] + "\n"); // 在文本区域追加系统消息
                                    show.setCaretPosition(show.getDocument().getLength()); // 将文本区域的光标移动到末尾
                                    net.close(); // 关闭网络连接
                                    break; // 退出循环
                                }
                                // 如果接收到的消息内容为"玩家取消准备"，执行以下操作：
                                else if (info[1].equals("玩家取消准备")) {
                                    show.append("[系统消息]" + info[1] + "\n"); // 在文本区域追加系统消息
                                    show.setCaretPosition(show.getDocument().getLength()); // 将文本区域的光标移动到末尾
                                    start.setEnabled(false); // 禁用“开始游戏”按钮
                                }
                                // 如果接收到的消息内容为"确认游戏开始"，执行以下操作：
                                else if (info[1].equals("确认游戏开始")) {
                                    break; // 退出循环
                                }
                                // 如果接收到的消息不是上述任何一种，执行以下操作：
                                else {
                                    show.append("[系统消息]" + info[1] + "\n"); // 在文本区域追加系统消息
                                    show.setCaretPosition(show.getDocument().getLength()); // 将文本区域的光标移动到末尾
                                }
                            }
                            else if (info[0].equals("0")) {
                                // 处理普通消息
                                show.append("-- " + info[1] + "\n");
                                show.setCaretPosition(show.getDocument().getLength());
                            }
                        }
                    }

                }).start();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        else if (e.getActionCommand() == "开始游戏") {
            // 随机生成一个整数，用于决定游戏的某些设置
            int randInt = (int)(0 + Math.random() * (1 - 0 + 1));
            int sendInt = (randInt == 1) ? 0 : 1;

            // 发送游戏开始的消息
            net.send( String.format("1,游戏开始,%d", sendInt) );
            gameStart = true;

            // 切换到游戏视图
            window.setCurrentPanel(new GameView(window, net, (randInt == 1) ? true : false));
        }
        else if (e.getActionCommand() == "发送") {
            // 获取输入框中的文本
            String str = input.getText();
            // 如果文本不为空，则发送消息
            if (!str.equals("")) {
                input.setText("");
                net.send("0," + str + ",0");
                show.append(">> " + str + "\n");
                show.setCaretPosition(show.getDocument().getLength());
            }
        }
    }

}