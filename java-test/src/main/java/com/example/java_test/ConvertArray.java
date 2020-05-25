package com.example.java_test;
import java.util.Scanner;
public class ConvertArray {
    public static void main(String[] args){
        int[][] num = new int [3][3];//num为3*3的二位数组
        Scanner scan = new Scanner(System.in);//创建scan类
        //输入
        for (int i = 0; i < 3; i++){
            for(int j=0;j<3;j++) {
                num[i][j] = scan.nextInt();
            }
        }
        //转置输出
        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                System.out.print(num[j][i]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }
}
