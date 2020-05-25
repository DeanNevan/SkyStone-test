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

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class Base {
    //四个电机对象
    public DcMotor motorFL = null;
    public DcMotor motorFR = null;
    public DcMotor motorBL = null;
    public DcMotor motorBR = null;

    //用于保存hardwareMap
    HardwareMap hwMap = null;

    /**
     * 初始化底盘硬件
     * @param targetMap hardwareMap
     */
    public void init(HardwareMap targetMap) {
        hwMap = targetMap;

        //获取硬件
        motorFL = hwMap.get(DcMotor.class, "motorFL");
        motorFR = hwMap.get(DcMotor.class, "motorFR");
        motorBL = hwMap.get(DcMotor.class, "motorBL");
        motorBR = hwMap.get(DcMotor.class, "motorBR");

        //四个驱动电机应该不使用编码器，但考虑到外置编码器接的是其中三个驱动电机，会冲突，so注释掉
//        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorFL.setPower(0);
        motorFR.setPower(0);
        motorBL.setPower(0);
        motorBR.setPower(0);

        motorFL.setDirection(DcMotor.Direction.REVERSE);
        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorBL.setDirection(DcMotor.Direction.REVERSE);
        motorBR.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * 底盘驱动的主函数
     * @param linearValue Vector2向量，本地坐标系的移动值（包括值的大小和方向）
     * @param angularValue 旋转值
     * @param isPowerSmoothOutput 是否平滑输出
     */
    public void drive(Vector2 linearValue, double angularValue, boolean isPowerSmoothOutput) {
        //linearValue = linearValue.normalized();//不确定在这里进行向量归一化好不好

        //钳制
        linearValue.set(
                Range.clip(linearValue.getX(), -1, 1),
                Range.clip(linearValue.getY(), -1, 1)
        );
        angularValue = Range.clip(angularValue, -1, 1);

        //判断是否平滑输出
        if (isPowerSmoothOutput){
            linearValue.set(
                    smoothPowerOutput(linearValue.getX()),
                    smoothPowerOutput(linearValue.getY())
            );
            angularValue = smoothPowerOutput(angularValue);
        }

        //目标输出功率
        double targetPowerFL = 0;
        double targetPowerFR = 0;
        double targetPowerBL = 0;
        double targetPowerBR = 0;

        //判断平移
        if (linearValue.length() > 0){
            targetPowerFL += linearValue.getY() + linearValue.getX();
            targetPowerFR += linearValue.getY() - linearValue.getX();
            targetPowerBL += linearValue.getY() - linearValue.getX();
            targetPowerBR += linearValue.getY() + linearValue.getX();
        }

        //判断旋转
        if (angularValue != 0){
            targetPowerFL += angularValue;
            targetPowerFR -= angularValue;
            targetPowerBL += angularValue;
            targetPowerBR -= angularValue;
        }

        //找出四个功率的最大值（绝对值）
        double targetPowerMax = Math.max(
                Math.max(
                        Math.abs(targetPowerFL),
                        Math.abs(targetPowerFR)
                ),
                Math.max(
                        Math.abs(targetPowerBL),
                        Math.abs(targetPowerBR)
                )
        );

        //钳制所有功率在-1到1内
        targetPowerFL /= targetPowerMax;
        targetPowerFR /= targetPowerMax;
        targetPowerBL /= targetPowerMax;
        targetPowerBR /= targetPowerMax;

        //输出功率值
        motorFL.setPower(targetPowerFL);
        motorFR.setPower(targetPowerFR);
        motorBL.setPower(targetPowerBL);
        motorBR.setPower(targetPowerBR);
    }

    /**
     * 枚举，固定移动方式的移动种类
     * FORWARD：向前
     * BACK：向后
     * RIGHT：向右
     * LEFT：向左
     * TURN_RIGHT：向右转
     * TURN_LEFT：向左转
     */
    enum fixDriveMode { FORWARD, BACK, RIGHT, LEFT, TURN_RIGHT, TURN_LEFT}

    /**
     * 固定模式的移动函数
     * @param mode 移动模式
     * @param power 功率大小
     * @param isPowerSmoothOutput 是否平滑输出
     */
    public void fixDrive(fixDriveMode mode, double power, boolean isPowerSmoothOutput){
        switch (mode) {
            case FORWARD:
                drive(new Vector2(0, -power), 0, isPowerSmoothOutput);
            case BACK:
                drive(new Vector2(0, power), 0, isPowerSmoothOutput);
            case RIGHT:
                drive(new Vector2(power, 0), 0, isPowerSmoothOutput);
            case LEFT:
                drive(new Vector2(-power, 0), 0, isPowerSmoothOutput);
            case TURN_RIGHT:
                drive(new Vector2(0, 0), power, isPowerSmoothOutput);
            case TURN_LEFT:
                drive(new Vector2(0, 0), -power, isPowerSmoothOutput);
        }
    }

    /**
     * 用y=(x-10)^3+1000平滑功率输出值
     * @param value 目标值
     * @return 目标值的平滑结果
     */
    private double smoothPowerOutput(double value) {
        if (value == 0){
            return value;
        }
        double result = (Math.pow(Math.abs(value) * 20 - 10, 3) + 1000) / 2000;
        if (value < 0){
            result = -result;
        }
        return result;
    }
 }

