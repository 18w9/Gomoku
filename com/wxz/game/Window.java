package com.wxz.game;

import com.wxz.views.Welcome;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {    //定义Window类，继承Frame

    JPanel currentPanel = null;//显示当前的JPanel面板

    public Window() {
        this.setTitle("热血五子棋  v2.1.3");//设置窗口标题
        this.setSize(980, 710);//设置窗口大小
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口，退出程序
        this.setResizable(false);//禁止调整窗口大小

        int width = Toolkit.getDefaultToolkit().getScreenSize().width;  	// 屏幕宽
        int height = Toolkit.getDefaultToolkit().getScreenSize().height; 	// 屏幕高

        this.setLocation((width - 980) / 2, (height-710) / 3);		// 设置窗体位置（居中）

        //创建初始面板并设置为当前面板
        currentPanel = new Welcome(this);
        this.add(currentPanel, BorderLayout.CENTER);

        //在此窗口上加载图标
        ImageIcon icon = new ImageIcon("D:\\JavaWork\\Gomoku\\project4\\src\\image\\Icon1.png");
        setIconImage(icon.getImage());//设置窗口图标

        this.setVisible(true);//设置窗口可见
    }

    //setCurrentPanel方法用于切换当前显示的Panel
    public void setCurrentPanel(JPanel panel) {
        this.remove(currentPanel);//移除当前面板
        currentPanel = panel;//更新当前面板为新的面板
        this.add(currentPanel, BorderLayout.CENTER);//将面板添加到JFrame
        this.revalidate();//设置布局管理器 该容器的大小可能已更改
        this.repaint();//请求重新绘制容器
    }

}