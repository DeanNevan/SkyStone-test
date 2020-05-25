package com.example.java_test;

import java.util.Arrays;
import java.util.Random;

public class Sort100RandomInt {
    public static void main(String[] args){
        int[] numbers = new int[100];
        Random random = new Random();//构造
        for (int i = 0; i < 100; i++){
            numbers[i] = random.nextInt(1001);//随机填充0-1000到数组里
        }
        Arrays.sort(numbers);//排序
        for (int x = 0; x < 10; x++){
            for (int y = 0; y < 10; y++){
                System.out.print(numbers[x * 10 + y] + " ");
            }
            System.out.print("\n");
        }
    }
}
