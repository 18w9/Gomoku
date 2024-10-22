package com.wxz.views;


import com.wxz.game.Window;
import com.wxz.tools.ChessBoard;
import com.wxz.tools.NetWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameView extends JPanel implements ActionListener, MouseListener {

    NetWork net;			// 联机和收发消息的工具
        // 消息格式为a,b,c    a为1代表此消息为请求悔棋，a为0代表此消息为坐标(b, c)
        // 1,0,0:请求悔棋   	1,0,1:拒绝悔棋	1,1,0:同意悔棋
        // 2,0,0:请求和棋       2,0,1:拒绝和棋      2,1,0:同意和棋
        // 3,0,0:投降

    JButton regret;			// 悔棋 按钮
    JButton requestDraw;	// 请和 按钮
    JButton surrender;		// 投降 按钮
    JButton back;			// 返回 按钮

    JLabel showChess;			// 显示执什么棋子
    JLabel showBlackCnt;		// 显示黑棋数量
    JLabel showWhiteCnt;		// 显示白棋数量
    JLabel showStatus;			// 显示当前谁的回合

    int blackCnt = 0;
    int whiteCnt = 0;

    ChessBoard board;		// 棋盘数据结构

    boolean enable;				// 使能信号，如果为true则轮到自身下棋，否则为对方下棋
    boolean isBlack;			// 是否执黑棋，执黑棋为先手
    boolean gameIsOver = false;	// 游戏是否结束

    private int myLastX = -1, myLastY = -1, urLastX = -1, urLastY = -1;// 记录上一次下棋的位置

    BufferedImage bgImage = null;			//初始化背景图片

    int distinceOfX = 30;  	// 距离 x 边界
    int distinceOfY = 30; 	// 距离 y 边界
    int widthOfLatic = 43; 	// 格宽

    Window window = null;

    public GameView(Window win, NetWork paramNet, boolean isFirst) {

        window = win;
        net = paramNet;
        enable = isFirst;
        isBlack = isFirst;

        board = new ChessBoard();			// 创建棋盘数据结构
        this.setSize(950, 662);		// 设置面板大小
        this.addMouseListener(this);				// 为面板加入监听

        this.setLayout(null);		// 设置为空布局
        this.setBackground(Color.WHITE);

        regret = new JButton("悔棋");
        regret.setFont(new Font("黑体", Font.BOLD, 26));
        regret.setBackground(new Color(0xffededed));
        regret.setFocusPainted(false);
        regret.setBounds(662, 320, 270, 70);
        regret.addActionListener(this);
        this.add(regret);

        requestDraw = new JButton("请和");
        requestDraw.setFont(new Font("黑体", Font.BOLD, 26));
        requestDraw.setBackground(new Color(0xffededed));
        requestDraw.setFocusPainted(false);
        requestDraw.setBounds(662, 434, 270, 70);
        requestDraw.addActionListener(this);
        this.add(requestDraw);

        surrender = new JButton("投降");
        surrender.setFont(new Font("黑体", Font.BOLD, 26));
        surrender.setBackground(new Color(0xffededed));
        surrender.setFocusPainted(false);
        surrender.setBounds(662, 548, 270, 70);
        surrender.addActionListener(this);
        this.add(surrender);

        back = new JButton("返回");
        back.setFont(new Font("黑体", Font.BOLD, 16));
        back.setBackground(new Color(0xffededed));
        back.setFocusPainted(false);
        back.setBounds(660, 30, 110, 40);
        back.addActionListener(this);
        this.add(back);
        back.setEnabled(false);

        // 创建一个新的 JLabel 组件，用于显示当前时间
        JLabel time = new JLabel();
        // 设置时间标签的字体样式和大小
        time.setFont(new Font("黑体", Font.BOLD, 40));
        // 设置时间标签的位置和大小
        time.setBounds(780, 45, 300, 50);
        // 将时间标签添加到当前面板中
        this.add(time);
        // 调用 setTimer 方法，传入时间标签，设置一个定时器来定期更新时间显示
        this.setTimer(time);

        // 创建一个新的 JLabel 组件，用于显示执棋信息（黑子或白子）
        showChess = new JLabel(String.format("执棋：%s", isBlack ? "黑子" : "白子"));
        // 设置执棋标签的字体样式和大小
        showChess.setFont(new Font("黑体", Font.BOLD, 26));
        // 设置执棋标签的位置和大小
        showChess.setBounds(662, 116, 300, 30);
        // 将执棋标签添加到当前面板中
        this.add(showChess);

        // 创建一个新的 JLabel 组件，用于显示黑棋的数量
        showBlackCnt = new JLabel("");
        // 设置黑棋数量标签的字体样式和大小
        showBlackCnt.setFont(new Font("黑体", Font.BOLD, 26));
        // 设置黑棋数量标签的位置和大小
        showBlackCnt.setBounds(662, 159, 300, 30);
        // 将黑棋数量标签添加到当前面板中
        this.add(showBlackCnt);

        // 创建一个新的 JLabel 组件，用于显示白棋的数量
        showWhiteCnt = new JLabel("");
        // 设置白棋数量标签的字体样式和大小
        showWhiteCnt.setFont(new Font("黑体", Font.BOLD, 26));
        // 设置白棋数量标签的位置和大小
        showWhiteCnt.setBounds(662, 202, 300, 30);
        // 将白棋数量标签添加到当前面板中
        this.add(showWhiteCnt);

        // 创建一个新的 JLabel 组件，用于显示当前的游戏状态
        showStatus = new JLabel("");
        // 设置游戏状态标签的字体样式和大小
        showStatus.setFont(new Font("黑体", Font.BOLD, 26));
        // 设置游戏状态标签的位置和大小
        showStatus.setBounds(662, 245, 300, 30);
        // 将游戏状态标签添加到当前面板中
        this.add(showStatus);
        // 创建并启动一个新的线程，用于在后台控制组件的可用性
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 无限循环，持续检查和更新组件的可用状态
                while (true) {
                    try {
                        // 让当前线程暂停30毫秒，减少CPU占用
                        Thread.sleep(30);
                    } catch (Exception e) {
                        // 如果发生异常，例如InterruptedException，则捕获并处理
                    }

                    // 根据游戏的进行状态（enable变量）设置悔棋按钮的可用性
                    regret.setEnabled(enable);
                    // 根据游戏的进行状态设置请求和棋按钮的可用性
                     requestDraw.setEnabled(enable);
                    // 根据游戏的进行状态设置投降按钮的可用性
                    surrender.setEnabled(enable);
                    // 如果游戏已结束（gameIsOver变量为true），则启用返回按钮
                    back.setEnabled(gameIsOver);
                }
            }
        }).start(); // 启动线程

        // 创建并启动一个新的线程，用于在后台接收游戏相关的消息
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 循环检查游戏是否结束，如果游戏结束，则退出循环
                while (gameIsOver == false) {
                    // 让当前线程暂停30毫秒，减少CPU占用
                    try { Thread.sleep(30); } catch (Exception e) { };
                    // 如果当前轮次不是玩家的回合，则接收消息
                    if (enable == false) {
                        // 接收网络消息并分割成数组
                        String []info = net.receive().split(",");
                        // 将消息数组中的字符串转换为整数
                        int a = new Integer(info[0]);
                        int b = new Integer(info[1]);
                        int c = new Integer(info[2]);
                        // 如果消息类型为0，表示棋盘位置坐标更新
                        if (a == 0) {
                            // 更新玩家最后落子的位置
                            urLastX = b;
                            urLastY = c;
                            // 根据当前执棋颜色放置棋子
                            char chess = isBlack ? ChessBoard.White_Char : ChessBoard.Black_Char;
                            board.placeChess(chess, b, c);

                            if (chess == ChessBoard.Black_Char) blackCnt ++;
                            else	whiteCnt ++;

                            // 设置enable 设置为玩家的回合
                            enable = true;
                            // 重绘界面
                            repaint();
                            // 判断游戏是否结束
                            if (board.canWin(chess, b, c)) {
                                gameIsOver = true;
                                JOptionPane.showMessageDialog(window, "失败！失败！");
                            }
                        }
                        // 如果消息类型为1，表示悔棋请求或结果
                        else if (a == 1) {
                            // 此为悔棋消息
                            // 如果收到的消息表示对方请求悔棋（b == 0 && c == 0）
                            if (b == 0 && c == 0) {
                                // 提供两个选项给玩家选择："同意"或"拒绝"
                                Object[] options = {"同意", "拒绝"};
                                // 弹出对话框询问玩家是否同意悔棋
                                int m = JOptionPane.showOptionDialog(window, "对方请求悔棋", "系统消息", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                // 如果玩家选择"同意"（m == 0）
                                if (m == 0) {
                                    // 发送同意悔棋的消息给服务器
                                    net.send("1,1,0");
                                    // 在棋盘上移除玩家和对方的最后落子
                                    board.unPlace(myLastX, myLastY);
                                    board.unPlace(urLastX, urLastY);
                                    // 重置最后落子的位置
                                    myLastX = -1;
                                    myLastY = -1;
                                    urLastX = -1;
                                    urLastY = -1;
                                    // 更新棋子数量
                                    blackCnt--;
                                    whiteCnt--;
                                    // 重绘棋盘以显示最新的状态
                                    repaint();
                                }
                                // 如果玩家选择"拒绝"
                                else {
                                    // 发送拒绝悔棋的消息给服务器
                                    net.send("1,0,1");
                                }
                            }
// 如果收到的消息表示对方拒绝悔棋（b == 0 && c == 1）
                            else if (b == 0 && c == 1) {
                                // 弹出对话框通知玩家对方拒绝了悔棋
                                JOptionPane.showMessageDialog(window, "对方拒绝悔棋");
                                // 设置游戏状态为玩家的回合
                                enable = true;
                            }
// 如果收到的消息表示对方同意悔棋（b == 1 && c == 0）
                            else if (b == 1 && c == 0) {
                                // 在棋盘上移除玩家和对方的最后落子
                                board.unPlace(myLastX, myLastY);
                                board.unPlace(urLastX, urLastY);
                                // 重置最后落子的位置
                                myLastX = -1;
                                myLastY = -1;
                                urLastX = -1;
                                urLastY = -1;
                                // 更新棋子数量
                                blackCnt--;
                                whiteCnt--;
                                // 设置游戏状态为玩家的回合
                                enable = true;
                                // 重绘棋盘以显示最新的状态
                                repaint();
                            }
                            else {
                                // 此为错误消息

                            }
                        }
                        // 如果消息类型为2，表示和棋请求或结果
                        else if (a == 2) {
                            if (b == 0 && c == 0) {			// 对方请求和棋
                                Object[] options ={ "同意", "拒绝" };  //自定义按钮上的文字
                                int m = JOptionPane.showOptionDialog(window, "对方请求和棋", "系统消息", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                if (m == 0) {
                                    net.send("2,1,0");
                                    gameIsOver = true;
                                    JOptionPane.showMessageDialog(window, "和棋：平局！");
                                }
                                else {
                                    net.send("2,0,1");
                                }
                            }
                            else if (b == 0 && c == 1) {	// 对方拒绝和棋
                                JOptionPane.showMessageDialog(window, "对方拒绝和棋");
                                enable = true;
                            }
                            else if (b == 1 && c == 0) {	// 对方同意和棋
                                JOptionPane.showMessageDialog(window, "对方同意和棋：平局！");
                                gameIsOver = true;
                            }
                            else {}
                        }
                        // 如果消息类型为3，表示投降
                        else if (a == 3) {
                            if (b == 0 && c == 0) {			// 对方投降
                                JOptionPane.showMessageDialog(window, "对方已投降：胜利！");
                                gameIsOver = true;
                            }
                            else {}
                        }
                        else {}
                    }
                }
            }

        }).start();//启动线程

        // 开启线程实时显示当前棋盘状态信息
        // 创建一个新的线程来执行定期任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 循环检查游戏是否结束，如果游戏结束，则退出循环
                while (gameIsOver == false) {
                    try {
                        // 让当前线程暂停30毫秒，减少CPU占用
                        Thread.sleep(30);
                    } catch (Exception E) {
                        // 如果发生异常（如InterruptedException），则捕获并处理异常
                    }

                    // 更新显示黑子数量的标签
                    showBlackCnt.setText(String.format("黑子：%d", blackCnt));
                    // 更新显示白子数量的标签
                    showWhiteCnt.setText(String.format("白子：%d", whiteCnt));//赋值数量
                    // 更新显示当前游戏状态的标签
                    showStatus.setText(String.format("当前状态：%s回合", enable ? "我方" : "对方"));

                    // 注意：这里应该有一个延迟或等待机制来避免频繁更新UI，否则可能会造成UI更新过于频繁而出现闪烁或性能问题
                }
            }
        }).start(); // 启动线程
        // 创建一个新的线程，用于在后台执行任务
        new Thread(new Runnable() {
            // Runnable 接口的 run 方法，这是线程执行的入口点
            public void run() {
                // 调用 showDialog 方法，这个方法可能用于显示一个对话框
                showDialog();
            }
        }).start(); // 启动线程，使 run 方法中的代码开始执行

    }

    private void showDialog() {
        // 检查是否轮到当前玩家（我方）走棋
        if (enable == true) {
            // 如果 enable 为 true，表示轮到我方走棋，显示对话框提示我方为先手
            JOptionPane.showMessageDialog(window, "我方为先手");
        } else {
            // 如果 enable 为 false，表示轮到对方走棋，显示对话框提示对方为先手
            JOptionPane.showMessageDialog(window, "对方为先手");
        }
    }

    // 设置一个定时器，每1000毫秒（1秒）触发一次动作，用于更新时间显示
    private void setTimer(JLabel time) {
            // final 关键字用于声明一个在构造器中初始化的变量，以便在匿名内部类中使用
        final JLabel varTime = time;
        // 获取当前时间的毫秒值，并保存为 startTime 变量
        long startTime = System.currentTimeMillis();
        // 创建一个 Timer 对象，设置间隔时间为1000毫秒，并提供一个 ActionListener 来处理时间更新
        Timer timeAction = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 每次定时器触发时获取当前时间的毫秒值
                long timemillis = System.currentTimeMillis();
                // 创建 SimpleDateFormat 对象用于格式化日期
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                // 计算经过的时间，并格式化为 "HH:mm:ss" 格式
                // startTime - 28800000 是为了将当前时间减去 8 小时（假设为 UTC+8），以显示本地时间
                varTime.setText(df.format(new Date(timemillis - startTime - 28800000)));
            }
        });
        // 启动定时器，开始定时执行任务
        timeAction.start();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 调用父类的 paintComponent 方法，确保基本的绘制操作（如清除旧的绘制内容）被执行。

        // 加载背景图片并绘制
        ImageIcon icon = new ImageIcon("D:\\JavaWork\\Gomoku\\project4\\src\\image\\background2.png");
        Image img = icon.getImage();
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

        // 设置绘制颜色为黑色
        g.setColor(Color.BLACK);
        // 绘制棋盘的网格线
        for(int i=0; i<15; i++) {
            g.drawLine(distinceOfX, distinceOfY+widthOfLatic*i, widthOfLatic*14+distinceOfX, distinceOfY+widthOfLatic*i);
            g.drawLine(distinceOfX+widthOfLatic*i, distinceOfY, distinceOfX+widthOfLatic*i, widthOfLatic*14 + distinceOfY);
        }

        // 绘制棋盘上的四个角落的圆点
        g.fillOval(154, 154, 10, 10);
        g.fillOval(500, 154, 10, 10);
        g.fillOval(154, 498, 10, 10);
        g.fillOval(498, 498, 10, 10);
        g.fillOval(326, 326, 10, 10);

        // 遍历棋盘的每个位置，绘制棋子
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.whichChessInThisPos(i, j) == ChessBoard.Black_Char) { // 画黑子
                    int tempX = i * widthOfLatic + distinceOfX;
                    int tempY = j * widthOfLatic + distinceOfY;
                    g.setColor(Color.BLACK);
                    g.fillOval(tempX-10, tempY-10, 25, 25);
                }
                if (board.whichChessInThisPos(i, j) == ChessBoard.White_Char) { // 画白子
                    int tempX = i * widthOfLatic + distinceOfX;
                    int tempY = j * widthOfLatic + distinceOfY;
                    g.setColor(Color.WHITE);
                    g.fillOval(tempX-10, tempY-10, 25, 25);
                    g.setColor(Color.black);
                    g.drawOval(tempX-10, tempY-10, 25, 25);
                }
                // 如果是电脑最后落子的位置，绘制一个灰色的圆圈作为标记
                if (i == urLastX && j == urLastY) {
                    int tempX = i * widthOfLatic + distinceOfX;
                    int tempY = j * widthOfLatic + distinceOfY;
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillOval(tempX-2, tempY-2, 10, 10);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (gameIsOver == false) {
            if (e.getActionCommand() == "悔棋") {
                if (myLastX != -1 && urLastX != -1) {
                    net.send("1,0,0");
                    this.enable = false;
                }
                else {
                    JOptionPane.showMessageDialog(window, "现在还不能悔棋");
                }
            }
            else if (e.getActionCommand() == "请和") {
                net.send("2,0,0");
                this.enable = false;
            }
            else if (e.getActionCommand() == "投降") {
                net.send("3,0,0");
                this.enable = false;
                gameIsOver = true;
                JOptionPane.showMessageDialog(window, "游戏失败：你举起双手，投降了。");
            }
        }
        else {
            if (e.getActionCommand() == "返回") {
                window.setCurrentPanel(new Welcome(window));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        // 获取鼠标点击的屏幕坐标
        int x = e.getX();
        int y = e.getY();

        // 检查游戏是否结束，当前玩家是否轮到下棋，以及点击是否在棋盘的有效范围内
        if (gameIsOver == false && enable && x >= distinceOfX && x <= widthOfLatic*14+distinceOfX+3 && y >= distinceOfY && y <= widthOfLatic*14 + distinceOfY+3) {
            // 将鼠标点击的屏幕坐标转换为棋盘上的格子坐标
            double tempx = (x - distinceOfX) / (1.0 * widthOfLatic);
            double tempy = (y - distinceOfY) / (1.0 * widthOfLatic);
            x = (int)(tempx + 0.5);
            y = (int)(tempy + 0.5);

            // 检查点击的位置是否为空
            if (board.posIsBlank(x, y)) {
                // 记录玩家最后一次落子的位置
                myLastX = x;
                myLastY = y;
                // 在棋盘上放置玩家的棋子
                board.placeChess(isBlack ? ChessBoard.Black_Char : ChessBoard.White_Char, x, y);

                // 更新棋子数量
                if (isBlack) {
                    blackCnt++;
                } else {
                    whiteCnt++;
                }

                // 发送玩家的落子位置到服务器
                net.send(String.format("0,%d,%d", x, y));
                // 设置标志，表示当前轮到对方下棋
                enable = false;

                // 重绘棋盘以显示新的棋子位置
                this.repaint();

                // 判断游戏是否结束
                if (board.canWin(isBlack ? ChessBoard.Black_Char : ChessBoard.White_Char, x, y)) {
                    gameIsOver = true;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

}