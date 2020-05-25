package com.example.java_test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Test {
    public static void main(String[] args){
        Map<String, Double> hm = new HashMap<String, Double>();

        Scanner scan = new Scanner(System.in);//创建scan类
        while (true){
            System.out.println("添加成绩请输入0，查看成绩排名表请输入1");
            int i = scan.nextInt();
            if (i == 1){
                System.out.println(hm);
                continue;
            }
            System.out.println("请依次输入部长评分总成绩、行政跟进评分、干事自评分、额外增减分、干事姓名，中间用空格隔开");
            int a = scan.nextInt();
            int b = scan.nextInt();
            int c = scan.nextInt();
            double d = scan.nextInt();
            String name = scan.next();
            double result = a * 0.6 + b * 0.2 + c * 0.2 + d;
            System.out.println(name + "最终分数:" + result);
            hm.put(name, result);
            hm = sortByValueDescending(hm);
        }
    }
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>(){
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return compare;
                //return -compare;  //降序排序
                }
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}