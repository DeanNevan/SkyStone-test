package com.example.java_test;

public class GetWanShu {
    public static void main(String[] args){
        for (int i = 1; i <= 1000; i++){//0应该不算吧。。。
            int sum = 0;
            for (int s = 1; s < i; s++){
                if (i % s == 0){
                    sum += s;
                }
            }
            if (sum == i){
                System.out.println(i);
            }
        }
    }
}
