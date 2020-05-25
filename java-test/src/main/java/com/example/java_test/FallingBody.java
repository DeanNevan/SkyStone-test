package com.example.java_test;

import java.text.DecimalFormat;
import java.util.Scanner;

public class FallingBody {
    public static void main(String[] args){
        int HEIGHT = 120;//初始下坠高度
        Scanner scan = new Scanner(System.in);//创建scan类
        DecimalFormat df = new DecimalFormat("#.00");
        int number = scan.nextInt();
        System.out.print("总运动距离为：" + df.format(get_total_path_length(number, HEIGHT)) + "m\n");
        System.out.print("下次反弹高度为：" + df.format(get_next_bounce_height(number, HEIGHT)) + "m");

    }
    //获得总路径长度，就是初始高度HEIGHT加上以HEIGHT为首项、number-1为项数、0.5为公比的等比数列的和
    public static double get_total_path_length(int number, int init_height){
        return init_height + 2 * init_height * (1 - Math.pow(0.5, number - 1));

    }
    private static double get_next_bounce_height(int number, int init_height){
        return init_height * Math.pow(0.5, number);
    }
}
