package com.wxz.tools;

public class ChessBoard {
    //定义棋子
    public static char White_Char = 'O';//白子
    public static char Black_Char = 'X';//黑子
    public static char Blank_Char = ' ';//空位

    //定义棋盘大小常量
    public static int M = 15;//15*15
    public static int GridNumber = 225;//15*15=225

    //一维数组表示的棋盘
    char boardInOneDimens[];

    //构造函数初始化棋盘
    public ChessBoard(){
        boardInOneDimens = new char[ChessBoard.GridNumber];//初始化棋盘数组，长度为GridNumber
        for(int i = 0;i < ChessBoard.GridNumber;i++){
            boardInOneDimens[i] = ChessBoard.Blank_Char;
        }
    }

    //判断位置（x，y）是否为空位
    public boolean posIsBlank(int x,int y){
        //计算一维数组中对应的索引位置，基于棋盘的二维坐标（x,y）
        //由于棋盘是15*15，所以使用x * ChessBoard.M + y来定位
        //检查该位置的字符是否等于Blank_Char，即空位字符
        //如果等于，返回true表示该位置是空的，否则返回false
        return boardInOneDimens[x * ChessBoard.M + y] == ChessBoard.Blank_Char;
    }

    //判断棋盘是否已经填满
    public boolean isFull(){
        //遍历棋盘上的所有位置
        for(int i = 0;i < ChessBoard.GridNumber;i++){
            //检查当前位置是否是空位
            //如果找到空位，返回false表示棋盘未填满
            if(boardInOneDimens[i] == ChessBoard.Blank_Char)
                return false;
        }
        //如果没有找到空位，返回true表示棋盘已填满
        return true;
    }

    //当前位置是什么棋子
    public char whichChessInThisPos(int x,int y){
        //计算一维数组中对应的索引位置，基于棋盘的二维坐标（x，y）
        //返回该位置的字符，表示该位置上的棋子
        //如果是空将返回Blank_Char
        return boardInOneDimens[x * ChessBoard.M + y];
    }

    // 判断在位置 (x, y) 放置棋子 c 后，玩家是否能赢
    public boolean canWin(char c, int x, int y) {
        int i = 1,cnt = 1;//i用于循环计数，cnt用于计数连续相同棋子的数量

        //检查向右连续相同棋子的数量
        while(y + i < ChessBoard.M && boardInOneDimens[x * ChessBoard.M + y + i] == c){
            i++;
            cnt++;
        }
        // 如果连续相同棋子的数量达到 5，返回 true 表示赢了
        if (cnt >= 5) return true;

        // 重置 i 和 cnt，检查向左连续相同棋子的数量
        i = 1;
        while (y - i >= 0 && boardInOneDimens[x * ChessBoard.M + y - i] == c) {
            i++;
            cnt++;
        }
        if (cnt >= 5) return true;

        //重置i和cnt，检查项下连续相同棋子数量
        i = 1; cnt = 1;
        while (x + i < ChessBoard.M && boardInOneDimens[(x + i) * ChessBoard.M + y] == c) {
            i++;
            cnt++;
        }
        // 检查向上连续相同棋子的数量
        i = 1;
        while (x - i >= 0 && boardInOneDimens[(x - i) * ChessBoard.M + y] == c) {
            i++;
            cnt++;
        }
        if (cnt >= 5) return true;

        // 重置 i 和 cnt，检查斜右向下连续相同棋子的数量
        i = 1; cnt = 1;
        while (x + i < ChessBoard.M && y + i < ChessBoard.M && boardInOneDimens[(x + i) * ChessBoard.M + y + i] == c) {
            i++;
            cnt++;
        }
        // 检查斜左向上连续相同棋子的数量
        i = 1;
        while (x - i >= 0 && y - i >= 0 && boardInOneDimens[(x - i) * ChessBoard.M + y - i] == c) {
            i++;
            cnt++;
        }
        if (cnt >= 5) return true;

        // 重置 i 和 cnt，检查斜左向下连续相同棋子的数量
        i = 1; cnt = 1;
        while (x + i < ChessBoard.M && y - i >= 0 && boardInOneDimens[(x + i) * ChessBoard.M + y - i] == c) {
            i++;
            cnt++;
        }
        // 检查斜右向上连续相同棋子的数量
        i = 1;
        while (x - i >= 0 && y + i < ChessBoard.M && boardInOneDimens[(x - i) * ChessBoard.M + y + i] == c) {
            i++;
            cnt++;
        }
        if (cnt >= 5) return true;

        // 如果所有方向都没有达到 5 个连续棋子，则返回 false 表示没有赢
        return false;
    }

    //放置棋子
    public void placeChess(char c, int x, int y){boardInOneDimens[x * ChessBoard.M + y] = c;}

    //清空位置
    // 清空位置
    public void unPlace(int x, int y) {
        boardInOneDimens[x * ChessBoard.M + y] = ChessBoard.Blank_Char;
    }
    // 打印棋盘
    public void print() {
        for (int i = 0; i < ChessBoard.M; i ++) {
            for (int j = 0; j < ChessBoard.M; j ++) {
                System.out.print(boardInOneDimens[i * ChessBoard.M + j] + " ");
            }
            System.out.println("");
        }
    }

}
