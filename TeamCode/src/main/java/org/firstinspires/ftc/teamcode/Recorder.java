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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;


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

@TeleOp(name="Recorder", group="Linear Opmode")
//@Disabled
public class Recorder extends LinearOpMode {
    
    private ElapsedTime runtime = new ElapsedTime();//运行时间计时器
    
    /*
    path，记录文件的存储路径。
    您可以自行修改路径与文件名。只要保证Imitator程序中的path属性与此一致，即可。
     */
    private String path = "/storage/emulated/0/myFTCRecorder.txt";
    
    /*
    decimalOfSticksTriggers，记录的摇杆与两个trigger的精确小数位。
    比如调成4，那么会自动保留4位小数。至于如何取舍小数位，取决于getDecimalNumber()方法中的内容。
     */
    private int decimalOfSticksTriggers = 4;//
    
    /*
    events，用于记录运行过程中，两个手柄的按键、摇杆、triggers事件的各种信息。
    最后写入到记录文件（txt）中。
     */
    private StringBuilder events = new StringBuilder();
    
    /*
    updateEvents，一个状态变量。用于判定一次主循环中，是否有按键事件。
     */
    private boolean updateEvents = false;
    
    /*
    lastButtons1、lastButtons2，分别用于记录上一次主循环中
    1号手柄、2号手柄的按钮事件的各种信息。
     */
    private boolean[] lastButtons1 = {};
    private boolean[] lastButtons2 = {};
    
    /*
    lastSticksTriggers1、lastSticksTriggers1，分别用于记录上一次主循环中
    1号手柄、2号手柄的摇杆、triggers事件的各种信息
     */
    private float[] lastSticksTriggers1 = {};
    private float[] lastSticksTriggers2 = {};

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        //赋初始值
        lastButtons1 = getButtons(gamepad1);
        lastButtons2 = getButtons(gamepad2);
        lastSticksTriggers1 = getSticksTriggers(gamepad1);
        lastSticksTriggers2 = getSticksTriggers(gamepad2);

        ThreadRecorder threadRecorder = new ThreadRecorder();//记录程序线程实例化

        telemetry.addLine("等待记录开始");
        telemetry.update();

        waitForStart();
        runtime.reset();
        threadRecorder.start();//启动Recorder线程

        //主循环开始
        while (opModeIsActive()) {
            /*
            在这里放入您的手动阶段的控制代码
             */
            
        }
        //结束程序，开始写入记录文件
        writer(path, events);//写入记录文件
        telemetry.addLine("记录文件 写入完成");
        telemetry.update();
    }

    /*
    声明ThreadRecorder线程，目的是考虑到某些情况下，在控制代码里存在sleep()等延时函数，导致记录延迟
    因此单独放在另一线程中运行记录程序
     */
    public class ThreadRecorder extends Thread {
        private ThreadRecorder () {
        }
        public void run() {
            while (opModeIsActive()){
                recorder();//Recorder记录器的内容都集成在这个recorder函数里了。
            }

        }
    }

    //以下是方法 >_<
    
    /*
    recorder方法，每次主循环都调用。
     */
    private void recorder(){
        int nowRuntime = getIntRuntime();//获取当前运行时间的毫秒数

        //创建两个字符串变量用于存储检测到的手柄事件的各种信息
        String gamepad1DetectResult = "";
        String gamepad2DetectResult = "";

        //创建四个temp临时变量存储手柄的所有数值
        //避免前后重复多次获取手柄全数据的时间延迟导致数据不准确。
        boolean[] tempButtons1 = getButtons(gamepad1);
        float[] tempSticksTriggers1 = getSticksTriggers(gamepad1);
        boolean[] tempButtons2 = getButtons(gamepad2);
        float[] tempSticksTriggers2 = getSticksTriggers(gamepad2);

        //检测是否存在1、2号手柄连接。
        //并将检测的手柄事件信息 存到两个变量中
        if (gamepad1.getUser() != null){
            gamepad1DetectResult = detectGamepad(nowRuntime, 1, tempButtons1, tempSticksTriggers1);
        }
        if (gamepad2.getUser() != null){
            gamepad2DetectResult = detectGamepad(nowRuntime, 2, tempButtons2, tempSticksTriggers2);
        }

        //当有事件发生时，updateEvents变量将会在detectGamepad函数里被设置为true。
        //即，如果有事件发生，就将记录下来的gamepad1DetectResult、gamepad2DetectResult存到events变量中
        if (updateEvents){
            //添加大括号作为一次记录的开头，再加入现在的运行时间和逗号
            events.append("{").append(nowRuntime).append(",");
            //判定是否加分号，以便Imitator程序里的字符串分割操作。
            int _length = gamepad2DetectResult.length();
            if (gamepad1DetectResult.length() != 0){
                events.append(gamepad1DetectResult);
                if (_length != 0){
                    events.append(";");
                }
            }
            if (_length != 0) {
                events.append(gamepad2DetectResult);
            }
            //添加}作为结尾
            events.append("}");
            //完成本次记录，换行，一定要换，因为Imitator的读取文件的方法就是按行读取。
            events.append("\n");
        }
        
        //在最后更新变量，以便下一次循环进行比对。
        lastButtons1 = getButtons(gamepad1);
        lastButtons2 = getButtons(gamepad2);
        lastSticksTriggers1 = getSticksTriggers(gamepad1);
        lastSticksTriggers2 = getSticksTriggers(gamepad2);
        updateEvents = false;

        //自动阶段有30秒，打印剩余时间，以便操作手获知。
        telemetry.addData("30s自动剩余时间", 30 - runtime.seconds());
        telemetry.update();
    }
    
    /*
    writer，用于将指定字符串写入记录文件到指定路径
            参数path：路径
            参数string：字符串
     */
    private void writer(String path, Object content){
        File file = new File(path);
        // 创建文件
        try {
            file.createNewFile();
            // 创建FileWriter对象
            FileWriter writer = new FileWriter(file);
            // 向文件写入内容
            writer.write(String.valueOf(content));
            writer.flush();
            //一定要关闭
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    detectGamepad，用于检测并返回 包含指定手柄的按键、摇杆、triggers事件的各种信息的_events字符串。
                   参数nowRuntime：目前运行时间的毫秒数
                   参数gamepadID：1或2，用于判定是1号手柄还是2号
                   参数nowButtons：即当前按钮布尔数组
                   参数nowSticksTriggers：即当前摇杆、triggers的浮点数数组
     */
    private String detectGamepad(int nowRuntime, int gamepadID, boolean[] nowButtons, float[] nowSticksTriggers){
        //定义_events，用于存储本次检测的结果
        StringBuilder _events = new StringBuilder();
        //用于接下来的判定，即是否加逗号
        boolean gamepadUpdated = false;

        //定义两个变量用于保存上一次判定时的按钮、摇杆、triggers数值信息。
        boolean[] lastButtons = {};
        float[] lastSticksTriggers = {};

        String IDLabel = "";
        //检测1号手柄
        if (gamepadID == 1){
            lastButtons = lastButtons1;
            lastSticksTriggers = lastSticksTriggers1;
            //u1表示1号手柄，会加入到_event字符串中
            IDLabel = "u1";
        }
        //否则检测2号手柄
        else if (gamepadID == 2){
            lastButtons = lastButtons2;
            lastSticksTriggers = lastSticksTriggers2;
            //u2表示2号手柄，会加入到_event字符串中
            IDLabel = "u2";
        }
        boolean buttonsUpdated = false;//用于接下来的判定，即是否加逗号
        //遍历nowButtons元素
        for (int i = 0; i < nowButtons.length; i++){
            //若现在的该按钮值不等于上一次的该按钮值，则判定为一次按钮事件
            if (nowButtons[i] != lastButtons[i]){
                if (buttonsUpdated) _events.append(",");
                buttonsUpdated = true;
                updateEvents = true;
                if (!gamepadUpdated){
                    _events.append(IDLabel).append(",");
                    gamepadUpdated = true;
                }
                //将事件信息加入到_events中。
                //b开头，接i，表示是按钮的第i个按钮的事件，详见getButtons函数中的buttons数组。
                //nowButtons[i]是按钮值
                _events.append("b").append(i).append(",").append(nowButtons[i]);
            }
        }
        boolean stUpdated = false;//用于接下来的判定，即是否加逗号
        //遍历nowSticksTriggers元素
        for (int i = 0; i < nowSticksTriggers.length; i++){
            //若现在的该摇杆或triggers的值不等于上一次的该值，则判定为一次摇杆、triggers事件
            if (nowSticksTriggers[i] != lastSticksTriggers[i]){
                if (stUpdated || buttonsUpdated) _events.append(",");
                stUpdated = true;
                updateEvents = true;
                if (!gamepadUpdated){
                    _events.append(IDLabel).append(",");
                    gamepadUpdated = true;
                }
                //将事件信息加入到_events中。
                //s开头，接i，表示是摇杆（sticks）和triggers的第i个键的事件，详见getSticksTriggers函数中的values数组。
                //nowSticksTriggers[i]是该键的浮点值
                _events.append("s").append(i).append(",").append(nowSticksTriggers[i]);
            }
        }
        return String.valueOf(_events);
    }
    
    /*
    getButtons，获取指定手柄的所有按键的布尔数组
                参数whichGamepad，即gamepad1或gamepad2
     */
    private boolean[] getButtons(Gamepad whichGamepad){
        //如果您有些按钮是用不到的，可以注释掉，但要保证Imitator程序中的
        //setButtons方法的buttons字符串数组的内容与此values元素一致，包括顺序。
        //当然，建议不做更改。
        boolean[] buttons = {
                whichGamepad.dpad_up,
                whichGamepad.dpad_down,
                whichGamepad.dpad_left,
                whichGamepad.dpad_right,
                whichGamepad.left_stick_button,
                whichGamepad.right_stick_button,
                whichGamepad.a,
                whichGamepad.b,
                whichGamepad.x,
                whichGamepad.y,
                whichGamepad.left_bumper,
                whichGamepad.right_bumper,
                whichGamepad.start,
                whichGamepad.back
        };
        return buttons;
    }

    /*
    getSticksTriggers，获取指定手柄的所有摇杆、triggers的浮点数数组
                参数whichGamepad：即gamepad1或gamepad2
     */
    private float[] getSticksTriggers(Gamepad whichGamepad){
        //如果您有些摇杆或triggers是用不到的，可以注释掉，但要保证Imitator程序中的
        // setSticksTriggers方法的values字符串数组的内容与此values元素一致，包括顺序。
        //当然，建议不做更改。
        float[] values = {
                whichGamepad.left_stick_x,
                whichGamepad.left_stick_y,
                whichGamepad.right_stick_x,
                whichGamepad.right_stick_y,
                whichGamepad.left_trigger,
                whichGamepad.right_trigger
        };
        float[] result = new float[values.length];
        //遍历values，获取whichGamepad手柄的所有摇杆、triggers的数值，
        //并保留指定位数（decimalOfSticksTriggers属性）的小数。
        for (int i = 0; i < values.length; i++){
            result[i] = getDecimalNumber(decimalOfSticksTriggers, values[i]);
        }
        return result;
    }

    /*
    getIntRuntime，获取当前运行时间的毫秒数
     */
    private int getIntRuntime(){
        return (Double.valueOf(runtime.milliseconds())).intValue();
    }
    
    /*
    getDecimalNumber，返回保留指定位数的浮点数。
                      参数decimal：保留小数位数。
                      参数number：要操作的目标浮点数。
     */
    private float getDecimalNumber(int decimal, float number){
        BigDecimal b = new BigDecimal(number);
        float result = b.setScale(decimal, BigDecimal.ROUND_HALF_DOWN).floatValue();
        return result;
    }
}
