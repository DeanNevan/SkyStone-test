package com.example.java_test;

import java.util.*;
import java.lang.*;

public class MapSort {

    //  hashmap按值排序
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // HashMap的entry放到List中
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        //  对List按entry的value排序
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 将排序后的元素放到LinkedHashMap中
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    public static void main(String[] args)
    {

        HashMap<String, Integer> hm = new HashMap<String, Integer>();

        // 填充测试数据
        hm.put("Math", 98);
        hm.put("Data Structure", 85);
        hm.put("Database", 91);
        hm.put("Java", 95);
        hm.put("Operating System", 79);
        hm.put("Networking", 80);
        Map<String, Integer> hm1 = sortByValue(hm);

        // 打印按值排序后的数据
        for (Map.Entry<String, Integer> en : hm1.entrySet()) {
            System.out.println("Key = " + en.getKey() +
                    ", Value = " + en.getValue());
        }
    }
}