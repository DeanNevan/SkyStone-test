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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;


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

@TeleOp(name="Navigator", group="Linear Opmode")
@Disabled
public class Navigator {

    HardwareMap hwMap = null;//用于保存hardwareMap

    private final int WRA = 40;//WHEEL_RADIUS_A 单位mm
    private final int WRB = 40;//WHEEL_RADIUS_B 单位mm
    private final int WRC = 40;//WHEEL_RADIUS_C 单位mm

    private final int DCA = 240;//DISTANCE_to_CENTER_A 单位mm
    private final int DCB = 240;//DISTANCE_to_CENTER_B 单位mm
    private final int DCC = 240;//DISTANCE_to_CENTER_C 单位mm

    private final int RRA = 1000;//RESOLUTION_RATIO_A
    private final int RRB = 1000;//RESOLUTION_RATIO_A
    private final int RRC = 1000;//RESOLUTION_RATIO_A

    public double totalValueA = 0;//编码器A总行程
    public double totalValueB = 0;//编码器B总行程
    public double totalValueC = 0;//编码器C总行程

    private int lastValueA = 0;//编码器A上次的读数
    private int lastValueB = 0;//编码器B上次的读数
    private int lastValueC = 0;//编码器C上次的读数

    public Vector2 position = new Vector2(0, 0);//当前坐标 Vector2对象
    public double rotation = 0;//当前yaw轴旋转度数（是角度，不是弧度）

    private Vector2 lastPosition = new Vector2(0, 0);//车体上次的坐标
    private double lastRotation = 0;//车体上次的yaw轴旋转值

    private ElapsedTime runtime = new ElapsedTime();//计时器
    private int lastUpdateTime = 0;//上一次更新位置时的总运行时间

    public DcMotor encoderA = null;//A编码器
    public DcMotor encoderB = null;//B编码器
    public DcMotor encoderC = null;//C编码器

    public boolean debugMode = true;//是否是debug模式

    //初始化方法，用于获取编码器对象、设置编码器模式等等

    /**
     * 初始化编码器硬件
     * @param targetMap hardwareMap
     */
    public void init(HardwareMap targetMap) {
        hwMap = targetMap;

        encoderA  = hwMap.get(DcMotor.class, "motorFL");//编码器A接前左电机口
        encoderB  = hwMap.get(DcMotor.class, "motorFR");//编码器B接前右电机口
        encoderC  = hwMap.get(DcMotor.class, "motorBL");//编码器C接后左电机口

        encoderA.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        encoderA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        encoderB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        encoderB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        encoderC.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        encoderC.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * 刷新位置，一般是在循环中调用
     * @param nowRuntime 当前运行时间
     * @return 布尔值，代表是否成功刷新坐标和yaw轴旋转量
     */
    public boolean update(int nowRuntime){
        //若与上次刷新时的运行时间相同，则不刷新，返回false
        if (lastUpdateTime == nowRuntime) return false;

        //本次编码器数值
        int currentValueA = encoderA.getCurrentPosition();
        int currentValueB = encoderB.getCurrentPosition();
        int currentValueC = encoderC.getCurrentPosition();

        //编码器数值与上次数值的差值
        int deltaValueA = currentValueA - lastValueA;
        int deltaValueB = currentValueB - lastValueB;
        int deltaValueC = currentValueC - lastValueC;

        //编码器轮子相比上次转动的距离
        double deltaDistanceA = (double) (deltaValueA / RRA) * 2 * Math.PI * WRA;
        double deltaDistanceB = (double) (deltaValueB / RRA) * 2 * Math.PI * WRB;
        double deltaDistanceC = (double) (deltaValueC / RRA) * 2 * Math.PI * WRC;

        //坐标、yaw轴旋转值相比上次的偏移量（本地坐标）
        double xOffsetLocal = (deltaDistanceA + deltaDistanceB + (Math.sqrt(2) * DCA * deltaDistanceC) / DCC) / (Math.sqrt(2) * ((double) (DCA / DCC) + 1.0));
        double yOffsetLocal = (deltaDistanceB - deltaDistanceA) / (Math.sqrt(2));
        double rOffset = (xOffsetLocal - deltaDistanceC) / DCC;

        //本地坐标偏移量 转换到 全局坐标偏移量
        Vector2 positionOffsetLocal = new Vector2(xOffsetLocal, yOffsetLocal);
        Vector2 positionOffsetGlobal = positionOffsetLocal.rotated(rotation);

        //更新坐标和yaw轴旋转值
        position.plus(positionOffsetGlobal);
        rotation += rOffset;

        //更新“last”变量们
        lastValueA = currentValueA;
        lastValueB = currentValueB;
        lastValueC = currentValueC;
        lastPosition.set(position.getX(), position.getY());
        lastRotation = rotation;
        lastUpdateTime = nowRuntime;

        return true;
    }

    /**
     * 获取当前坐标 Vector2对象
     * @return 当前坐标 Vector2对象
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * 获取当前yaw轴旋转度数（是角度，不是弧度）
     * @return
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * 重置坐标到Vector2(0, 0)
     */
    public void resetPosition() {
        position.set(0, 0);
        lastPosition.set(0, 0);
    }

    /**
     * 重置yaw轴旋转度数到0
     */
    public void resetRotation() {
        rotation = 0;
        lastRotation = 0;
    }

    /**
     * 重置编码器数值
     */
    public void resetEncoder() {
        encoderA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        encoderB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        encoderC.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        totalValueA = 0;
        totalValueB = 0;
        totalValueC = 0;
        lastValueA = 0;
        lastValueB = 0;
        lastValueC = 0;
    }
}
