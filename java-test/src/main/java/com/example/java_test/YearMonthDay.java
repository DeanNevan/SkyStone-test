package com.example.java_test;

import java.util.Scanner;

public class YearMonthDay {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);//创建scan类
        int year = scan.nextInt();
        int month = scan.nextInt();
        int day = scan.nextInt();
        int total_day = 0;
        int[] days_in_year = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};//一年中每月的天数
        for (int i = 0; i < month; i++){
            total_day += days_in_year[i];
            if (i == 1 && year % 4 == 0){
                total_day += 1;//闰年，天数+1
            }
        }
        System.out.print("你输入的是" + year + "年" + month + "月" + day + "日\n");
        System.out.print("这是" + year + "年的第" + total_day + "天");
    }
}
