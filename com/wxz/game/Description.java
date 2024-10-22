package com.wxz.game;

import javax.swing.*;
import java.awt.*;

public class Description extends JFrame {
    JTextArea info;//内容
    public Description(){
        this.setTitle("说明");
        this.setSize(500,600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//窗口关闭后只关闭当前窗口

        int width = Toolkit.getDefaultToolkit().getScreenSize().width;  //屏幕宽
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;    //屏幕高

        this.setLocation(width / 2,height / 2); //设置窗体位置

        info = new JTextArea();//创建文本域
        info.setFont(new Font("宋体",Font.BOLD,22));//设置字体
        info.setEditable(false);						// 设置文本区不可编辑
        info.setLineWrap(true);        					// 激活自动换行功能
        info.setWrapStyleWord(true);            		// 激活断行不断字功能
        JScrollPane scroll = new JScrollPane(info); //添加滚动条
        this.add(scroll, BorderLayout.CENTER);

        info.setText("[版本]v2.1.3\n"
                + "--------------------\n"
                + "\n游戏说明：\n创建房间：\n  点击创建房间可以自己开设一个对战房间，对方输入该房间的IP地址即可进入同一房间，准备之后可以由房主开启对战，对战之前还可以在对话框内进行聊天。\n\n进入房间：\n  点击进入房间，然后输入已经开设好对战房间的IP地址，即可加入房间。\n\n单机游戏：\n单机游戏可以实现玩家与电脑对战。\n\n注：联机过程中可以进行悔棋和求和操作，不过都要先经过对方同意。单机模式下可以根据自己意愿进行悔棋操作，不用征求电脑同意。\n\n在棋盘的右侧会显示我方当前的棋子颜色、黑白棋子的个数以及当前下棋的状态。\n\n"
                + "--------------------\n\n2021/11/19 11:59:59\n无敌男子汉\n");
        info.setCaretPosition(0);

        this.setVisible(true);
    }
}
