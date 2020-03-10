/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Imitator", group="Linear Opmode")
//@Disabled
public class Imitator extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    //读取txt路径
    String path = "/storage/emulated/0/myFTCRecorder.txt";

    @Override
    public void runOpMode() {
        //读取txt
        List<String> imitationList = readTxtFileIntoStringArrList(path, telemetry);

        //实例化Imitator线程,如果不想在DS手机上看到Debug信息，把下面的传入参数的showDebugData改成false
        ThreadImitator threadImitator = new ThreadImitator(imitationList, true);

        telemetry.addLine("模仿者准备运行");
        telemetry.update();
        waitForStart();
        telemetry.addLine("模仿者运行中");
        telemetry.update();
        runtime.reset();

        threadImitator.start();//启动Imitator线程
        while (opModeIsActive()){

            /*
            请在这里放入您的控制代码，与Recorder手动程序所用的一致，即可
            比如:
            if (gamepad1.a){
            motor.setPower(1)
            }
            等
             */

        }
    }

    /*
    声明ThreadImitator线程，目的是考虑到某些情况下，在控制代码里存在sleep()等延时函数，导致模仿延迟
    因此单独放在另一线程中运行记录程序
     */
    public class ThreadImitator extends Thread {
        private List<String> imitationList;
        private boolean showDebugData;

        //构造函数，参数传递
        private ThreadImitator (List<String> imitationList, boolean showDebugData) {
            this.imitationList = imitationList;
            this.showDebugData = showDebugData;
        }
        public void run() {
            //for循环，遍历 imitationList的元素
            for (int i = 0; i < imitationList.size(); i++) {
                if (!opModeIsActive()) break;//如果已经结束程序，跳出遍历
                imitator(imitationList, i, showDebugData);//本次模仿启动
            }

        }
    }

    /*
    imitator,赋予手柄数值
             参数imitationList：就是读取txt读到的List
             参数i：List的索引（即对参数imitationList的第i个数据进行手柄的赋值）
             参数showDebugData：是否显示Debug信息
     */
    private void imitator(List<String> imitationList, int i, boolean showDebugData){
        //将大括号去除
        String nextImitateList = imitationList.get(i).replaceAll("\\{", "").replaceAll("\\}", "");
        //nextImitateU1，对1号手柄的赋值数组
        //nextImitateU2，对2号手柄的赋值数组
        ArrayList<String> nextImitateU1 = new ArrayList<>();
        ArrayList<String> nextImitateU2 = new ArrayList<>();
        //nextImitateTime，下一次模仿开始的时间
        int nextImitateTime;
        //如果包含分号，代表包含两个手柄的事件信息
        if (nextImitateList.contains(";")) {
            //分割分号，将两个手柄的事件信息分开
            String[] strArr = nextImitateList.split(";");
            //清除空格、空值
            strArr = clearStringArray(strArr);
            //分割逗号，将1号手柄的索引同值分开来
            String[] tempArr1 = strArr[0].split(",");
            //清除空格、空值
            tempArr1 = clearStringArray(tempArr1);
            //分割逗号，将2号手柄的索引同值分开来
            String[] tempArr2 = strArr[1].split(",");
            //清除空格、空值
            tempArr2 = clearStringArray(tempArr2);
            //获取下一次模仿开始的时间（毫秒）
            nextImitateTime = Integer.parseInt(tempArr1[0]);

            nextImitateU1 = new ArrayList<>(Arrays.asList(tempArr1));
            nextImitateU2 = new ArrayList<>(Arrays.asList(tempArr2));
            //将1号手柄的赋值数组的前两个元素删除（第一个元素是事件时间，第二个元素是“u1”，后面用不到）
            nextImitateU1.remove(0);
            nextImitateU1.remove(0);
            //将2号手柄的赋值数组的第一个元素删除（第一个元素是“u2”，后面用不到）
            nextImitateU2.remove(0);
        }
        //不含分号，即 若只包含一个手柄的事件信息
        else {
            //操作大致同上
            String[] tempArr = nextImitateList.split(",");
            tempArr = clearStringArray(tempArr);
            nextImitateTime = Integer.parseInt(tempArr[0]);
            if (tempArr[1].equals("u1")) {
                nextImitateU1 = new ArrayList<>(Arrays.asList(tempArr));
                nextImitateU1.remove(0);
                nextImitateU1.remove(0);
            } else if (tempArr[1].equals("u2")) {
                nextImitateU2 = new ArrayList<>(Arrays.asList(tempArr));
                nextImitateU2.remove(0);
                nextImitateU2.remove(0);
            }
        }
        //Debug信息
        if (showDebugData){
            telemetry.addLine("下一次模仿数据是模仿列表的第 " + i + " 个" );
            telemetry.addLine("下一次模仿将在 " + nextImitateTime + "时间开始");
            telemetry.addData("目前时间是", runtime.milliseconds());
            telemetry.addData("下一次的模仿数据[1号手柄][2号手柄]",
                                nextImitateU1.toString() + nextImitateU2.toString());
            telemetry.update();
        }
        //若当前运行时间没到下一次模仿开始的时间，啥也不干
        while (runtime.milliseconds() < nextImitateTime) {
            idle();
        }
        //“午时已到”——模仿启动，对手柄进行赋值
        if (nextImitateU1.size() != 0) updateGamepad(gamepad1, nextImitateU1);
        if (nextImitateU2.size() != 0) updateGamepad(gamepad2, nextImitateU2);
    }

    /*
    readTxtFileIntoStringArrList，用于读取txt，并返回List<String>
                                  参数filePath：指定读取的txt的路径
                                  参数telementry：不解释了
     */
    private static List<String> readTxtFileIntoStringArrList(String filePath, Telemetry telementry) {
        telementry.addLine("读取文件ing");
        List<String> list = new ArrayList<String>();
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null) {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            } else {
                telementry.addLine("找不到指定的文件");
                telementry.update();
                //System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            telementry.addLine("读取文件内容出错");
            telementry.update();
            //System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        telementry.addLine("读取finished");
        return list;
    }

    /*
    updateGamepad，对两个手柄的按钮、摇杆、triggers进行赋值
                   参数whichGamepad：即要对哪个手柄进行赋值，gamepad1或gamepad2
                   参数nextImitateArray，赋值的数组，类似{"b4","true","s2","0.1678"}这样
     */
    private void updateGamepad(Gamepad whichGamepad, ArrayList<String> nextImitateArray) {
        //定义四个ArrayList用于存储对按钮、摇杆、triggers设定的索引和数值。
        ArrayList<Integer> setButtonsIndex = new ArrayList<>();
        ArrayList<Boolean> setButtonsValues = new ArrayList<>();
        ArrayList<Integer> setSticksTriggersIndex = new ArrayList<>();
        ArrayList<Float> setSticksTriggersValues = new ArrayList<>();
        //对a进行 偶数遍历（nextImitateArray的偶数索引的数值是类似“b3”这样的值，该值指向哪个按键）
        for (int a = 0; a < nextImitateArray.size(); a = a + 2) {
            //分离字母与数字
            String[] tempStrArr = nextImitateArray.get(a).split("");
            //清除字符串数组中的空格、空元素
            tempStrArr = clearStringArray(tempStrArr);
            //等于“b”，表示按钮
            if (tempStrArr[0].equals("b")) {
                setButtonsIndex.add(Integer.valueOf(tempStrArr[1]));
                setButtonsValues.add(Boolean.valueOf(nextImitateArray.get(a + 1)));
            }
            //等于“s”，表示摇杆（Sticks）和triggers
            else if (tempStrArr[0].equals("s")) {
                setSticksTriggersIndex.add(Integer.valueOf(tempStrArr[1]));
                setSticksTriggersValues.add(Float.valueOf(nextImitateArray.get(a + 1)));
            }
        }
        try {
            //对按钮进行设值
            setButtons(
                    whichGamepad,
                    setButtonsIndex.toArray(new Integer[setButtonsIndex.size()]),
                    setButtonsValues.toArray(new Boolean[setButtonsValues.size()])
            );
        } catch (Exception e) {
            telemetry.addData("模仿者程序错误报告",
                    "设置按钮数值出错，请检查Recorder的getButtons方法中的buttons" +
                            "是否与本程序的setButtons方法中的buttons一致");
            telemetry.addData("", e);
            telemetry.update();
            e.printStackTrace();
        }
        try {
            //对摇杆、triggers进行设值
            setSticksTriggers(
                    whichGamepad,
                    setSticksTriggersIndex.toArray(new Integer[setSticksTriggersIndex.size()]),
                    setSticksTriggersValues.toArray(new Float[setSticksTriggersValues.size()])
            );
        } catch (Exception e) {
            telemetry.addData("模仿者程序错误报告",
                    "设置摇杆、triggers数值出错，请检查Recorder的getSticksTriggers方法中的values" +
                            "是否与本程序的setSticksTriggers方法中的values一致");
            telemetry.addData("", e);
            telemetry.update();
            e.printStackTrace();
        }
    }

    /*
    setButtons，用于接收索引数组、数值数组，利用Java的反射方法 对指定手柄的属性（按钮）进行赋值
                参数whichGamepad：指定手柄，gamepad1或gamepad2
                参数index，索引数组，指向values数组中的元素
                参数setValues，数值数组，即对应的手柄属性 要赋予的值的数组
     */
    private void setButtons(Gamepad whichGamepad, Integer[] index, Boolean[] setValues) throws Exception {
        if (index.length == 0)return;//索引数组无元素，不执行设值
        //同Recorder程序的getButtons方法中的描述
        Object[] buttons = {
                "dpad_up",
                "dpad_down",
                "dpad_left",
                "dpad_right",
                "left_stick_button",
                "right_stick_button",
                "a",
                "b",
                "x",
                "y",
                "left_bumper",
                "right_bumper",
                "start",
                "back"
        };
        for (int i = 0; i < index.length; i++) {
            int in = index[i];
            //利用Java的反射机制，用字符串（上面buttons中的元素）去获取、设置gamepad的属性
            Field field = whichGamepad.getClass().getField(buttons[in].toString());
            field.set(whichGamepad, setValues[i]);
        }
    }

    /*
    setSticksTriggers，用于接收索引数组、数值数组，利用Java的反射方法 对指定手柄的属性（摇杆、triggers）进行赋值
                       参数whichGamepad：指定手柄，gamepad1或gamepad2
                       参数index，索引数组，指向values数组中的元素
                       参数setValues，数值数组，即对应的手柄属性 要赋予的值的数组
     */
    private void setSticksTriggers(Gamepad whichGamepad, Integer[] index, Float[] setValues) throws Exception {
        if (index.length == 0)return;//索引数组无元素，不执行设值
        //同Recorder程序的getSticksTriggers方法中的描述
        Object[] values = {
                "left_stick_x",
                "left_stick_y",
                "right_stick_x",
                "right_stick_y",
                "left_trigger",
                "right_trigger"
        };
        //遍历索引数组的元素
        for (int i = 0; i < index.length; i++) {
            int in = index[i];
            //利用Java的反射机制，用字符串（上面values中的元素）去获取、设置gamepad的属性
            Field field = whichGamepad.getClass().getField(values[in].toString());
            field.set(whichGamepad, setValues[i]);
        }
    }

    /*
    clearStringArray，用于清除字符串数组中的空格、空值
                      参数stringArray：要操作的目标字符串数组
     */
    private String[] clearStringArray(String[] stringArray) {
        List<String> tmp = new ArrayList<String>();
        for(String str:stringArray){
            if(str!=null && str.length()!=0){
                tmp.add(str);
            }
        }
        return tmp.toArray(new String[0]);
    }
}