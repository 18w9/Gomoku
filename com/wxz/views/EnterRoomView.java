package com.wxz.views;

import com.wxz.tools.GetWLANip;
import com.wxz.game.Window;
import com.wxz.tools.NetWork;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class EnterRoomView extends JPanel implements ActionListener {
    NetWork net;//联机和收发消息的工具

    JButton enter;//进入按钮
    JButton back;//返回按钮
    JButton cancel;//取消按钮

    JTextField input;//输入框 输入房间IP

    Window window;// 窗口对象引用

    public EnterRoomView(Window win){
        this.window = win;// 初始化窗口对象

        // 设置面板大小、布局和背景颜色
        this.setSize(950, 662);										// 设置面板大小
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        // 初始化 "返回" 按钮，并设置其属性
        back = new JButton("返回");
        back.setBounds(30, 30, 150, 50);
        back.setFocusPainted(false);
        back.setFont(new Font("黑体", Font.BOLD, 25));
        back.setBackground(new Color(0xffededed));
        back.addActionListener(this);
        this.add(back);

        // 初始化标签并设置其属性
        JLabel label = new JLabel("房间IP:");
        label.setFont(new Font("黑体", Font.BOLD + Font.ITALIC, 45));
        label.setBounds(750, 240, 300, 60);
        this.add(label);

        // 初始化输入框并设置其属性
        input = new JTextField(50);
        input.setFont(new Font("黑体", Font.PLAIN, 45));
        input.setHorizontalAlignment(JTextField.RIGHT);
        input.setBounds(300, 300, 620, 65);
        this.add(input);

        // 初始化 "进入" 按钮，并设置其属性
        enter = new JButton("进入");
        enter.setBounds(490, 450, 430, 90);
        enter.setFocusPainted(false);
        enter.setFont(new Font("黑体", Font.BOLD, 40));
        enter.setBackground(new Color(0xffededed));
        enter.addActionListener(this);
        this.add(enter);

        // 初始化 "取消" 按钮，并设置其属性
        cancel = new JButton("取消");
        cancel.setBounds(40, 450, 430, 90);
        cancel.setFocusPainted(false);
        cancel.setFont(new Font("黑体", Font.BOLD, 40));
        cancel.setBackground(new Color(0xffededed));
        cancel.addActionListener(this);
        this.add(cancel);
    }

    //重写paintComponent方法用于自定义面板的绘制
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ImageIcon icon = new ImageIcon("D:\\JavaWork\\Gomoku\\project4\\src\\image\\background1.png");
        Image img = icon.getImage();
        g.drawImage(img, 0, 0, 1290, 722, this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO Auto-generated method stub
        if(e.getActionCommand() == "返回"){//返回到欢迎界面
            window.setCurrentPanel(new Welcome(window));
        }
        else if (e.getActionCommand() == "取消") {// 取消操作，也返回到欢迎界面
            window.setCurrentPanel(new Welcome(window));
        }
        else if(e.getActionCommand() == "进入"){
            try {
                //从文本输入框中获取用户输入的字符串，这通常是房间的IP地址
                String str = input.getText();
                // 创建一个新的NetWork对象，该对象使用Socket连接到用户输入的IP地址和端口
                net = new NetWork(new Socket(str, NetWork.PORT));
                // 通过NetWork对象发送消息，告知服务器有用户进入房间
                // 消息格式为："1,用户IP已进入,0"
                net.send("1," + new GetWLANip().getIP() + "已进入,0");

                // 切换到房间界面，Room类可能是另一个JPanel的子类，用于显示房间的GUI
                window.setCurrentPanel(new Room(window, net));
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                // 如果无法解析主机名，则捕获UnknownHostException异常
                // 这通常意味着输入的IP地址无效或无法找到
                e1.printStackTrace(); // 打印出主机不可达异常
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                // 如果发生I/O错误，如网络连接问题，则捕获IOException异常
                e1.printStackTrace(); // 打印出其他 I/O 异常
            }

        }
    }
}
