package com.wxz.views;


import com.wxz.game.Description;
import com.wxz.game.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Welcome extends JPanel implements ActionListener {

    JButton enterRoom;		// 进入房间按钮
    JButton createRoom;		// 创建房间按钮
    JButton offLine;		// 单机游戏按钮
    JButton explain;		// 说明按钮

    // 定义一个Window类型的成员变量，用于引用窗口对象
    Window window;
    //首界面构造函数
    public Welcome(Window win) {

        this.window = win;

        this.setSize(950, 662);// 设置面板大小
        this.setLayout(null);// 将面板的布局管理器设置为null，使用绝对定位
        this.setBackground(Color.WHITE);// 设置面板的背景颜色为白色

        this.enterRoom = new JButton("进入房间");
        this.enterRoom.setFocusPainted(false);// 设置按钮焦点时不绘制焦点轮廓
        this.enterRoom.setFont(new Font("黑体", Font.BOLD, 35));//设置按钮字体
        this.enterRoom.setBackground(new Color(0xffededed));//设置按钮背景颜色
        this.enterRoom.setBounds(530, 300, 400, 90);//设置按钮的位置和大小
        this.enterRoom.addActionListener(this);//为按钮添加事件监听器

        this.createRoom = new JButton("创建房间");
        this.createRoom.setFocusPainted(false);
        this.createRoom.setFont(new Font("黑体", Font.BOLD, 35));
        this.createRoom.setBackground(new Color(0xffededed));
        this.createRoom.setBounds(530, 420, 400, 90);
        this.createRoom.addActionListener(this);

        this.offLine = new JButton("单机游戏");
        this.offLine.setFocusPainted(false);
        this.offLine.setFont(new Font("黑体", Font.BOLD, 35));
        this.offLine.setBackground(new Color(0xffededed));
        this.offLine.setBounds(530, 540, 400, 90);
        this.offLine.addActionListener(this);

        this.explain = new JButton("说明");
        this.explain.setFocusPainted(false);
        this.explain.setFont(new Font("黑体", Font.BOLD, 26));
        this.explain.setContentAreaFilled(false);				//设置按钮透明
//		this.explain.setBackground(Color.LIGHT_GRAY);
        this.explain.setBounds(50, 590, 200, 40);
        this.explain.addActionListener(this);

        //将按钮添加到面板中
        this.add(this.enterRoom);
        this.add(this.createRoom);
        this.add(this.offLine);
        this.add(this.explain);

    }

    @Override
        public void paintComponent(Graphics g) {//用于绘制组件内容，想要自定义一个组件的绘制外观时，可以通过重写这个方法来实现。
        // 首先调用父类JPanel的paintComponent方法，确保基本的绘制（如清除旧的绘制内容）被执行
        super.paintComponent(g);
        // 创建一个 ImageIcon 对象，加载指定路径的图像文件
        ImageIcon icon = new ImageIcon("D:\\JavaWork\\Gomoku\\project4\\src\\image\\background1.png");
        // 从 ImageIcon 对象中获取 Image 对象
        Image img = icon.getImage();
        // 使用 Graphics 对象 g 在组件上绘制图像
        // drawImage 方法参数解释：
        // Image img - 要绘制的图像
        // int dx - 图像在组件上的起始 x 坐标
        // int dy - 图像在组件上的起始 y 坐标
        // int dw - 图像在组件上的宽度
        // int dh - 图像在组件上的高度
        // ImageObserver observer - 图像绘制的观察者，通常是 this，表示当前组件
        g.drawImage(img, 0, 0, 1290, 722, this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {//设置点击监听事件
        // TODO Auto-generated method stub
        if (e.getActionCommand() == "进入房间") {        // 检查触发事件的动作命令是否为 "进入房间"
            // 如果是，调用 window 对象的 setCurrentPanel 方法
            // 传入一个新的 EnterRoomView 对象，该对象是当前视图
            window.setCurrentPanel(new EnterRoomView(window));
        }
        else if (e.getActionCommand() == "创建房间") {        // 检查触发事件的动作命令是否为 "创建房间"
            // 如果是，执行与 "进入房间" 类似的操作，但这次是 HostRoom 视图
            window.setCurrentPanel(new HostRoom(window));
        }
        else if (e.getActionCommand() == "单机游戏") {          // 检查触发事件的动作命令是否为 "单机游戏"
            // 生成一个 0 或 1 的随机整数
            int randInt = (int)(0 + Math.random() * (1 - 0 + 1));
            // 根据随机数决定是人类先手还是电脑先手
            boolean humanFirst = (randInt == 1) ? true : false;
            // 设置当前视图为离线游戏视图，并传入 window 对象和先手信息
            window.setCurrentPanel(new OffLineGame(window, humanFirst));
        }
        else if (e.getActionCommand() == "说明") {         // 检查触发事件的动作命令是否为 "说明"
            // 如果是，创建一个新的 Description 对象
            new Description();
        }
    }

}