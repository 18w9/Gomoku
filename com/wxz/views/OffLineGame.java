package com.wxz.views;


import com.wxz.tools.ChessBoard;
import com.wxz.game.Window;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class OffLineGame extends JPanel implements ActionListener, MouseListener {

    JButton regret;// 悔棋 按钮
    JButton exit;// 离开 按钮

    JLabel showChess;// 显示执什么棋子
    JLabel showBlackCnt;// 显示黑棋数量
    JLabel showWhiteCnt;// 显示白棋数量
    JLabel showStatus;// 显示当前谁的回合

    //显示棋盘数量和棋盘数据结构
    int blackCnt = 0;
    int whiteCnt = 0;

    ChessBoard board;// 棋盘数据结构

    boolean enable;// 使能信号，如果为true则轮到自身下棋，否则为对方下棋
    boolean isBlack;// 是否执黑棋，执黑棋为先手
    boolean gameIsOver = false;// 游戏是否结束


    // 最后落子位置和堆栈
    private int myLastX = -1, myLastY = -1, computerLastX = -1, computerLastY = -1;
    private Stack<Integer> myLast = new Stack<Integer>();
    private Stack<Integer> computerLast = new Stack<Integer>();

    //绘制棋盘参数
    int distinceOfX = 30;  	// 距离 x 边界
    int distinceOfY = 30; 	// 距离 y 边界
    int widthOfLatic = 43; 	// 格宽

    // 评估函数使用的棋型和对应的分数
    private String[] blackKey = new String[]{
            "XXXXX"," XXXX "," XXXX","XXXX ","X XXX","XX XX","XXX X"," XXX ","XXX "," XXX","X XX","XX X"," XX ","XX","X"
    };
    private String[] whiteKey = new String[]{
            "OOOOO"," OOOO "," OOOO","OOOO ","O OOO","OO OO","OOO O"," OOO ","OOO "," OOO","O OO","OO O"," OO ","OO","O"
    };
    private int[] keyValues = new int[]{
            100,90,80,80,80,80,80,70,60,50,40,30,20,10,5
    };

    char comCurrent; // 电脑棋
    char humCurrent; // 玩家棋

    Window window = null;

    //构造函数，初始化游戏
    public OffLineGame(Window win, boolean isFirst){

        // 将传入的Window对象引用赋值给成员变量window，这样可以在OffLineGame类中操作窗口
        window = win;
        // 将isFirst参数的值赋给isBlack成员变量，用于标记当前玩家是执黑棋还是白棋
        isBlack = isFirst;
        // 根据isBlack的值设置enable标志，决定游戏开始时轮到谁下棋
        // 如果isBlack为true，表示当前玩家执黑棋，enable也为true，表示轮到玩家下棋
        // 如果isBlack为false，表示当前玩家执白棋，enable为false，表示轮到电脑下棋
        enable = isBlack;
        // 创建一个新的ChessBoard对象，用于管理棋盘的状态
        board = new ChessBoard();

        // 根据是否执黑棋设置电脑和玩家的棋子类型
        if(this.isBlack) {
            comCurrent = board.White_Char; // 电脑为白
            humCurrent = board.Black_Char;
        }else {
            comCurrent = board.Black_Char; // 电脑为黑
            humCurrent = board.White_Char;
        }

        this.setSize(950, 662);// 设置面板大小
        this.addMouseListener(this);// 为面板加入监听

        this.setLayout(null);// 设置为空布局
        this.setBackground(Color.WHITE);

        regret = new JButton("悔棋");
        regret.setFont(new Font("黑体", Font.BOLD, 26));
        regret.setBackground(new Color(0xffededed));
        regret.setFocusPainted(false);
        regret.setBounds(662, 320, 270, 70);
        regret.addActionListener(this);
        this.add(regret);

        exit = new JButton("离开");
        exit.setFont(new Font("黑体", Font.BOLD, 26));
        exit.setBackground(new Color(0xffededed));
        exit.setFocusPainted(false);
        exit.setBounds(662, 548, 270, 70);
        exit.addActionListener(this);
        this.add(exit);

        //添加 显示时间的JLabel
        JLabel time = new JLabel();
        // 设置 JLabel 的字体样式和大小，这里使用的是黑体，字体加粗，字号为 40
        time.setFont(new Font("黑体", Font.BOLD, 40));
        // 设置 JLabel 的位置和大小，这里设置为距离窗口左边 780 像素，距离窗口顶部 30 像素，宽度为 300 像素，高度为 50 像素
        time.setBounds(780, 30, 300, 50);
        // 将 JLabel 添加到当前的 JPanel（或其他容器）中，这样它就会在界面上显示出来
        this.add(time);
        // 调用 setTimer 方法，传入 time 标签，设置一个定时器来定期更新时间显示
        this.setTimer(time);

        // 创建一个新的 JLabel 组件，用于显示当前执棋的颜色
        showChess = new JLabel( String.format("执棋：%s", isBlack ? "黑子" : "白子") );
        // 设置 JLabel 的字体样式和大小，这里使用的是黑体，字体加粗，字号为 26
        showChess.setFont(new Font("黑体", Font.BOLD, 26));
        // 设置 JLabel 的位置和大小，这里设置为距离窗口左边 662 像素，距离窗口顶部 116 像素，宽度为 300 像素，高度为 30 像素
        showChess.setBounds(662, 116, 300, 30);
        this.add(showChess);

        // 创建一个新的 JLabel 组件，初始时不显示任何内容
        showBlackCnt = new JLabel("");
        showBlackCnt.setFont(new Font("黑体", Font.BOLD, 26));
        showBlackCnt.setBounds(662, 159, 300, 30);
        this.add(showBlackCnt);

        showWhiteCnt = new JLabel("");
        showWhiteCnt.setFont(new Font("黑体", Font.BOLD, 26));
        showWhiteCnt.setBounds(662, 202, 300, 30);
        this.add(showWhiteCnt);

        showStatus = new JLabel("");
        showStatus.setFont(new Font("黑体", Font.BOLD, 26));
        showStatus.setBounds(662, 245, 300, 30);
        this.add(showStatus);

        // 创建一个新的线程并传递一个实现了Runnable接口的匿名内部类实例给它
        // Runnable接口中的run方法将被线程执行
        new Thread(new Runnable() {
            // 实现Runnable接口的run方法
            public void run() {
                go();
            }
        }).start(); // 启动线程

// 创建另一个新的线程并传递另一个实现了Runnable接口的匿名内部类实例给它
        new Thread(new Runnable() {

            // 重写Runnable接口的run方法
            @Override
            public void run() {
                // 使用while循环来周期性地更新UI，直到游戏结束
                while (gameIsOver == false) {
                    try {
                        // 使当前线程暂停30毫秒，以减少CPU使用率
                        Thread.sleep(30);
                    } catch (Exception E) {
                        // 如果发生异常，捕获它但不做任何处理
                        // 注意：通常应该避免空的catch块，至少应该记录异常
                    }
                    // 更新显示黑子数量的文本控件
                    showBlackCnt.setText(String.format("黑子：%d", blackCnt));
                    // 更新显示白子数量的文本控件
                    showWhiteCnt.setText(String.format("白子：%d", whiteCnt));
                    // 更新显示当前游戏状态的文本控件
                    // 使用三元运算符来决定是显示"我方"还是"对方"回合
                    showStatus.setText(String.format("当前状态：%s回合", enable ? "我方" : "对方"));
                }
            }
        }).start(); // 启动线程
    }

    // 设置Timer 1000ms实现一次动作 实际是一个线程
    private void setTimer(JLabel time) {
        // 将传入的 JLabel 实例赋值给 final 变量 varTime，这样它可以在匿名内部类中使用
        final JLabel varTime = time;
        // 获取当前时间的毫秒值，并保存为 startTime 变量
        long startTime = System.currentTimeMillis();
        // 创建一个新的 Timer 对象，设置间隔时间为 1000 毫秒（1秒）
        Timer timeAction = new Timer(1000, new ActionListener() {
            // 重写 ActionListener 接口的 actionPerformed 方法
            public void actionPerformed(ActionEvent e) {
                // 创建 SimpleDateFormat 对象用于格式化日期
                long timemillis = System.currentTimeMillis();
                // 转换日期显示格式
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                // 计算经过的时间，并格式化为 "HH:mm:ss" 格式
                // startTime - 28800000 是为了将当前时间减去 8 小时（假设为 UTC+8），以显示本地时间
                varTime.setText(df.format(new Date(timemillis - startTime - 28800000)));
            }
        });
        // 启动定时器，开始定时执行任务
        timeAction.start();
    }

    public void go() {
        // 检查是否轮到电脑走棋
        if (enable == false) { // 电脑先走
            // 弹出对话框提示电脑为先手
            JOptionPane.showMessageDialog(window, "电脑为先手");
            try {
                // 让线程休眠500毫秒，以模拟电脑思考的时间
                Thread.sleep(500);
            } catch (Exception e) {
                // 捕获并处理可能的异常，如InterruptedException
            }

            // 在棋盘的中心位置放置电脑的棋子
            board.placeChess(comCurrent, 7, 7);

            // 记录电脑最后一次落子的位置
            this.computerLast.push(7);
            this.computerLast.push(7);

            // 更新棋子数量
            if (comCurrent == ChessBoard.Black_Char) {
                blackCnt++; // 如果电脑执黑棋，则增加黑棋数量
            } else {
                whiteCnt++; // 否则增加白棋数量
            }

            // 重绘面板以显示新的棋子位置
            repaint();

            // 切换标志，表示现在轮到玩家走棋
            enable = true;
        } else {
            // 如果不是电脑先走，则弹出对话框提示玩家为先手
            JOptionPane.showMessageDialog(window, "玩家为先手");
        }
    }

    public void computerPlayer(){
        // 初始化临时变量，用于存储电脑玩家的最佳落子位置和最大评估值
        int tempRow = -1, tempCol = -1, maxValue = 0;

        // 遍历棋盘上的每个位置
        for (int i = 0; i < board.M; i ++) {
            for (int j = 0; j < board.M; j ++) {
                // 如果当前位置已经有棋子，跳过
                if (!board.posIsBlank(i, j)) continue;
                // 计算在当前位置落子的攻击评估值
                int attack = checkMax(i, j, comCurrent); // 计算攻击评估
                // 计算在当前位置落子的防守评估值
                int defend = checkMax(i, j, humCurrent); // 防守指数
                // 取攻击和防守评估值中的最大值
                int max = Math.max(attack, defend);
                // 如果当前位置的评估值大于之前的最大值，更新最佳落子位置和最大评估值
                if(max > maxValue) {
                    tempRow = i;
                    tempCol = j;
                    maxValue = max;
                }
            }
        }

        //放子
        // 在评估值最大的位置落子
        board.placeChess(comCurrent, tempRow, tempCol);
        // 记录电脑玩家的落子位置
        computerLastX = tempRow;
        computerLastY = tempCol;
        computerLast.push(tempRow);
        computerLast.push(tempCol);
        // 更新棋子数量
        if (comCurrent == ChessBoard.Black_Char) blackCnt ++;
        else whiteCnt ++;

        // 重绘棋盘以显示新的棋子位置
        repaint();
        // 检查电脑玩家是否赢了
        if (board.canWin(comCurrent, tempRow, tempCol)) {
            // 如果电脑赢了，设置游戏结束标志
            gameIsOver = true;
            // 弹出对话框询问玩家是否再玩一局
            int choice = JOptionPane.showConfirmDialog(window, "失败！失败！再来一局？", "系统消息", JOptionPane.YES_NO_OPTION);
            // 如果玩家选择再玩一局，随机决定谁先走
            if (choice == 0) {
                int randInt = (int)(0 + Math.random() * (1 - 0 + 1));
                boolean humanFirst = (randInt == 1) ? true : false;
                window.setCurrentPanel(new OffLineGame(window, humanFirst));
            }
        }
    }


    // 鼠标点击事件处理方法
    @Override
    public void mouseClicked(MouseEvent e) {
        // 当鼠标在组件上点击时，此方法会被调用
        // MouseEvent e 对象包含了关于鼠标点击事件的详细信息，如点击次数、鼠标坐标等
        // TODO Auto-generated method stub
        // 这里需要添加代码来处理鼠标点击事件，例如更新游戏状态或响应用户操作
    }
    // 鼠标进入组件事件处理方法
    @Override
    public void mouseEntered(MouseEvent arg0) {
        // 当鼠标光标第一次进入组件的边界时，此方法会被调用
        // MouseEvent arg0 对象包含了关于鼠标进入事件的详细信息
        // TODO Auto-generated method stub
        // 这里需要添加代码来响应鼠标进入事件，例如改变鼠标光标的形状或显示提示信息
    }
    // 鼠标离开组件事件处理方法
    @Override
    public void mouseExited(MouseEvent arg0) {
        // 当鼠标光标离开组件的边界时，此方法会被调用
        // MouseEvent arg0 对象包含了关于鼠标离开事件的详细信息
        // TODO Auto-generated method stub
        // 这里需要添加代码来响应鼠标离开事件，例如恢复鼠标光标的形状或隐藏提示信息

    }
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

            // 四舍五入到最近的整数，得到棋盘上的格子坐标
            x = (int) (tempx + 0.5);
            y = (int) (tempy + 0.5);

            // 检查点击的位置是否为空
            if (board.posIsBlank(x, y)) {
                // 将玩家的落子坐标压入栈中，以便悔棋时回退
                myLast.push(x);
                myLast.push(y);

                // 在棋盘上放置玩家的棋子
                board.placeChess(humCurrent, x, y);

                // 更新棋子数量
                if (humCurrent == ChessBoard.Black_Char) {
                    blackCnt++;
                } else {
                    whiteCnt++;
                }

                // 立即重绘棋盘以显示新的棋子
                this.paintImmediately(0, 0, 950, 662);

                // 设置标志，表示当前轮到电脑下棋
                enable = false;

                // 检查玩家是否赢了
                if (board.canWin(humCurrent, x, y)) {
                    gameIsOver = true;
                    // 弹出对话框询问玩家是否再玩一局
                    int choice = JOptionPane.showConfirmDialog(window, "游戏胜利！再来一局？", "系统消息", JOptionPane.YES_NO_OPTION);
                    if (choice == 0) {
                        // 随机决定下一局谁先走
                        int randInt = (int) (0 + Math.random() * (1 - 0 + 1));
                        boolean humanFirst = (randInt == 1) ? true : false;
                        window.setCurrentPanel(new OffLineGame(window, humanFirst));
                    }
                }
                // 如果游戏还没有结束，电脑下棋
                if (gameIsOver != true) {
                    try {
                        Thread.sleep(500); // 等待500毫秒
                    } catch (Exception e1) {
                        // 处理可能的异常
                    }
                    computerPlayer(); // 调用电脑下棋的方法
                }
                // 设置标志，表示当前轮到玩家下棋
                enable = true;
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent arg0) {
// 当鼠标按钮释放时，此方法会被调用
        // 通常用于处理鼠标释放事件，但在这个例子中，方法体为空
        // TODO Auto-generated method stub
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // 当一个动作事件被触发时，此方法会被调用，如按钮点击
        // TODO Auto-generated method stub

        // 检查动作命令是否为 "悔棋"
        if (e.getActionCommand() == "悔棋") {
            // 如果游戏尚未结束
            if (gameIsOver == false) {
                // 如果当前轮到玩家操作且有悔棋操作
                if (this.enable && !myLast.empty()) {
                    // 从栈中弹出最后落子的坐标
                    myLastY = myLast.pop();
                    myLastX = myLast.pop();
                    // 在棋盘上清除玩家的最后一个棋子
                    board.placeChess(board.Blank_Char, myLastX, myLastY);

                    // 弹出电脑最后落子的坐标
                    computerLastY = computerLast.pop();
                    computerLastX = computerLast.pop();
                    // 在棋盘上清除电脑的最后一个棋子
                    board.placeChess(board.Blank_Char, computerLastX, computerLastY);

                    // 更新棋子数量
                    blackCnt--;
                    whiteCnt--;

                    // 重绘棋盘以显示悔棋后的状态
                    this.repaint();
                } else {
                    // 如果没有悔棋操作，显示提示信息
                    JOptionPane.showMessageDialog(this, "现在还不能悔棋");
                }
            } else {
                // 如果游戏已经结束，显示提示信息
                JOptionPane.showMessageDialog(window, "游戏已经结束，后悔也没有用了");
            }
        }
        // 检查动作命令是否为 "离开"
        else if (e.getActionCommand() == "离开") {
            // 切换到欢迎面板
            window.setCurrentPanel(new Welcome(window));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 调用父类的 paintComponent 方法，处理组件的默认绘制

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

        // 根据棋盘状态绘制棋子
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board.whichChessInThisPos(i, j) == ChessBoard.Black_Char) {
                    // 绘制黑子
                    int tempX = i * widthOfLatic + distinceOfX;
                    int tempY = j * widthOfLatic + distinceOfY;
                    g.setColor(Color.BLACK);
                    g.fillOval(tempX-10, tempY-10, 25, 25);
                } else if (board.whichChessInThisPos(i, j) == ChessBoard.White_Char) {
                    // 绘制白子
                    int tempX = i * widthOfLatic + distinceOfX;
                    int tempY = j * widthOfLatic + distinceOfY;
                    g.setColor(Color.WHITE);
                    g.fillOval(tempX-10, tempY-10, 25, 25);
                    g.setColor(Color.black);
                    g.drawOval(tempX-10, tempY-10, 25, 25);
                }
            }
        }
// 如果电脑的下一步预测位置不为空，绘制预测位置的标记
        if (!this.computerLast.empty()) {
            // 弹出电脑预测的最后一个位置的y坐标
            int thisPosY = this.computerLast.pop();
            // 弹出电脑预测的最后一个位置的x坐标
            int thisPosX = this.computerLast.pop();

            // 将弹出的坐标重新压入栈中，以便下次可以继续使用
            this.computerLast.push(thisPosX);
            this.computerLast.push(thisPosY);

            // 计算预测位置在界面上的实际x坐标
            int tempX = thisPosX * widthOfLatic + distinceOfX;
            // 计算预测位置在界面上的实际y坐标
            int tempY = thisPosY * widthOfLatic + distinceOfY;
            // 设置绘制颜色为浅灰色
            g.setColor(Color.LIGHT_GRAY);
            // 绘制一个半径为5的圆来标记预测位置，圆心为(tempX, tempY)
            g.fillOval(tempX-2, tempY-2, 10, 10);
        }
    }


    //评估函数
    private int checkMax(int row, int col, char current) {
        // 初始化最大评估值为0
        int max = 0;
        // 用于临时存储当前方向上的最大评估值
        int tmpMax = 0;
        // 根据当前棋子颜色选择对应的棋型数组
        String[] array;
        if (current == board.Black_Char) {
            array = blackKey;
        } else {
            array = whiteKey;
        }
        // 使用StringBuilder构建当前方向上的棋子序列
        StringBuilder builder = new StringBuilder();

        // 水平方向检测评估
        // 遍历当前行的水平方向上的所有可能位置，包括两侧的扩展
        for (int i = -4; i <= 4; i++) {
            int newCol = col + i; // 计算新列位置
            // 检查新列位置是否在棋盘范围内
            if (newCol < 0 || newCol >= board.M) continue; // 如果超出范围，跳过本次循环
            // 构建当前方向上的棋子序列
            if (i == 0) {
                // 如果是中间位置（即当前位置），添加当前棋子
                builder.append(current);
            } else {
                // 如果是其他位置，添加该位置的棋子
                builder.append(board.whichChessInThisPos(row, newCol));
            }
        }

        // 检查构建的序列中是否包含棋型数组中的棋型，并更新最大评估值
        for (int i = 0; i < array.length; i++) {
            String key = array[i]; // 获取棋型数组中的一个棋型
            if (builder.indexOf(key) >= 0) {
                // 如果当前序列中包含该棋型，更新临时最大评估值
                tmpMax = keyValues[i];
                break; // 找到匹配的棋型后，跳出循环
            }
        }

        if (tmpMax > max) {
            max = tmpMax; // 更新最大评估值
        }

        if (tmpMax == 100) {
            return tmpMax; // 如果找到五子连线（即评估值为100），立即返回最高评估值
        }

        // 清空builder字符串，以便重新构建竖直方向的棋局字符串
        builder.delete(0, builder.length());

        // 遍历当前行上下的行，包括当前行
        for (int i = -4; i <= 4; i++) {
            // 计算新的行号
            int newRow = row + i;
            // 如果新的行号超出棋盘范围，则跳过
            if (newRow < 0 || newRow >= board.M) continue;
            // 如果是当前行，则添加当前棋子的标识
            if (i == 0) {
                builder.append(current);
            } else {
                // 否则，添加该位置的棋子标识
                builder.append(board.whichChessInThisPos(newRow, col));
            }
        }

        // 遍历数组，检查竖直方向上的棋局字符串是否包含特定的棋子序列
        for (int i = 0; i < array.length; i++) {
            String key = array[i];
            // 如果竖直方向的棋局字符串中包含该序列
            if (builder.indexOf(key) >= 0) {
                // 更新tmpMax为该序列对应的分数
                tmpMax = keyValues[i];
                break; // 找到匹配的序列后，跳出循环
            }
        }

        // 如果tmpMax大于之前记录的最大值max，则更新max
        if (tmpMax > max)
            max = tmpMax;

        // 如果tmpMax等于100，说明找到了最优的竖直方向序列，直接返回100
        if (tmpMax == 100)
            return tmpMax;
        // 清空builder字符串，以便重新构建右下至左上的45度对角线棋局字符串
        builder.delete(0, builder.length());
// 遍历当前位置周围的对角线位置，包括当前位置
        for (int i = -4; i <= 4; i++) {
            // 计算新的行号和列号
            int newRow = row + i;
            int newCol = col + i;
            // 如果新的行号或列号超出棋盘范围，则跳过
            if (newRow < 0 || newCol < 0 || newCol >= board.M || newRow >= board.M) continue;
            // 如果是当前位置，则添加当前棋子的标识
            if (i == 0) {
                builder.append(current);
            } else {
                // 否则，添加该位置的棋子标识
                builder.append(board.whichChessInThisPos(newRow, newCol));
            }
        }
// 遍历数组，检查右下至左上的45度对角线棋局字符串是否包含特定的棋子序列
        for (int i = 0; i < array.length; i++) {
            String key = array[i];
            // 如果对角线棋局字符串中包含该序列
            if (builder.indexOf(key) >= 0) {
                // 更新tmpMax为该序列对应的分数
                tmpMax = keyValues[i];
                break; // 找到匹配的序列后，跳出循环
            }
        }

// 如果tmpMax大于之前记录的最大值max，则更新max
        if (tmpMax > max)
            max = tmpMax;

// 如果tmpMax等于100，说明找到了最优的对角线序列，直接返回100
        if (tmpMax == 100)
            return tmpMax;

// 清空builder字符串，以便重新构建右上至左下的45度对角线棋局字符串
        builder.delete(0, builder.length());
// 遍历当前位置周围的对角线位置，包括当前位置
        for (int i = -4; i <= 4; i++) {
            // 计算新的行号和列号
            int newRow = row - i;
            int newCol = col + i;
            // 如果新的行号或列号超出棋盘范围，则跳过
            if (newRow < 0 || newCol < 0 || newCol >= board.M || newRow >= board.M) continue;
            // 如果是当前位置，则添加当前棋子的标识
            if (i == 0) {
                builder.append(current);
            } else {
                // 否则，添加该位置的棋子标识
                builder.append(board.whichChessInThisPos(newRow, newCol));
            }
        }
// 遍历数组，检查右上至左下的45度对角线棋局字符串是否包含特定的棋子序列
        for (int i = 0; i < array.length; i++) {
            String key = array[i];
            // 如果对角线棋局字符串中包含该序列
            if (builder.indexOf(key) >= 0) {
                // 更新tmpMax为该序列对应的分数
                tmpMax = keyValues[i];
                break; // 找到匹配的序列后，跳出循环
            }
        }

// 如果tmpMax大于之前记录的最大值max，则更新max
        if (tmpMax > max)
            max = tmpMax;

// 返回最终计算出的最大值max
        return max;
    }

}