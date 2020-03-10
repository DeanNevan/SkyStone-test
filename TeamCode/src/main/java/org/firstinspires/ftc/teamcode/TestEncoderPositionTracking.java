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
import com.qualcomm.robotcore.hardware.DcMotor;
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

@TeleOp(name="EncoderPositionTracking", group="Linear Opmode")
//@Disabled
public class TestEncoderPositionTracking extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor Encoder1 = null;//左编码器
    private DcMotor Encoder2 = null;//右编码器
    private DcMotor Encoder3 = null;//后编码器

    double distaceToCenterEncoder1 = 7.25;//左轮（编码器）到定位中心的距离，单位英寸
    double distaceToCenterEncoder2 = 7.25;//右轮（编码器）到定位中心的距离，单位英寸
    double distaceToCenterEncoder3 = 7.75;//后轮（编码器）到定位中心的距离，单位英寸

    double wheelRadius = 2;//车轮半径，单位英寸
    int resolutionRatio = 1000;//编码器分辨率

    @Override
    public void runOpMode() {
        Encoder1 = hardwareMap.get(DcMotor.class, "Encoder1");
        Encoder2 = hardwareMap.get(DcMotor.class, "Encoder2");
        Encoder3 = hardwareMap.get(DcMotor.class, "Encoder3");
        Encoder1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Encoder2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Encoder3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //启动线程
        ThreadPositionTracking threadPositionTracking = new ThreadPositionTracking();
        threadPositionTracking.start();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            //打印信息
            telemetry.addData("当前绝对角度", threadPositionTracking.getNowAbsoluteOrientation());
            telemetry.addData("当前绝对位置", threadPositionTracking.getNowAbsolutePosition());
            telemetry.update();
        }
    }

    //线程：编码器定位
    public class ThreadPositionTracking extends Thread {
        double resetAbsoluteOrientation = 0;
        double nowAbsoluteOrientation = 0;
        double[] nowAbsolutePosition = new double[2];

        int lastPositionEcoder1 = Encoder1.getCurrentPosition();
        int lastPositionEcoder2 = Encoder2.getCurrentPosition();
        int lastPositionEcoder3 = Encoder3.getCurrentPosition();

        double lastAbsoluteOrientation = 0;
        public ThreadPositionTracking () {
        }
        public void run() {
            resetAllEncoder();
            double[] lastAbsolutePosition = new double[2];
            while (opModeIsActive()){
                //当前编码器读数
                int nowPositionEncoder1 = Encoder1.getCurrentPosition();
                int nowPositionEncoder2 = Encoder2.getCurrentPosition();
                int nowPositionEncoder3 = Encoder3.getCurrentPosition();

                //与上次循环之间车轮行程距离的差值
                double deltaDistanceEncoder1 = wheelRadius * 2 * Math.PI * (nowPositionEncoder1 - lastPositionEcoder1) / resolutionRatio;
                double deltaDistanceEncoder2 = wheelRadius * 2 * Math.PI * (nowPositionEncoder2 - lastPositionEcoder2) / resolutionRatio;
                double deltaDistanceEncoder3 = wheelRadius * 2 * Math.PI * (nowPositionEncoder3 - lastPositionEcoder3) / resolutionRatio;

                //更新编码器读数
                lastPositionEcoder1 = nowPositionEncoder1;
                lastPositionEcoder2 = nowPositionEncoder2;
                lastPositionEcoder3 = nowPositionEncoder3;

                //计算自上次重置以来，左右编码器车轮总行程
                double totalDistanceEncoder1 = wheelRadius * 2 * Math.PI * nowPositionEncoder1 / resolutionRatio;
                double totalDistanceEncoder2 = wheelRadius * 2 * Math.PI * nowPositionEncoder2 / resolutionRatio;

                //计算目前绝对角度
                nowAbsoluteOrientation = resetAbsoluteOrientation + (totalDistanceEncoder1 - totalDistanceEncoder2) / (distaceToCenterEncoder1 + distaceToCenterEncoder2);
                lastAbsoluteOrientation = nowAbsoluteOrientation;

                //计算相对上次绝对角度的差值
                double deltaOrientation = nowAbsoluteOrientation - lastAbsoluteOrientation;

                //计算局部偏移量
                double[] localOffset = new double[2];
                if (deltaOrientation == 0){
                    localOffset[0] = deltaDistanceEncoder3;
                    localOffset[1] = deltaDistanceEncoder2;
                }
                else{
                    localOffset[0] = 2 * (distaceToCenterEncoder3 + deltaDistanceEncoder3 / deltaOrientation);
                    localOffset[1] = 2 * (distaceToCenterEncoder2 + deltaDistanceEncoder2 / deltaOrientation);
                }

                //计算平均角度
                double averageOrientation = lastAbsoluteOrientation + deltaOrientation / 2;

                //旋转 局部偏移量 到 全局偏移量
                double angle = Math.atan(localOffset[1] / localOffset[2]);
                double length = Math.sqrt(localOffset[0] * localOffset[0] + localOffset[1] * localOffset[1]);
                double targetAngle = angle - averageOrientation;
                double[] globalOffset = {length * Math.cos(targetAngle), length * Math.sin(targetAngle)};

                //计算绝对位置
                nowAbsolutePosition[0] = lastAbsolutePosition[0] + globalOffset[0];
                nowAbsolutePosition[1] = lastAbsolutePosition[1] + globalOffset[1];
                lastAbsolutePosition = nowAbsolutePosition;

                //如果任何一个编码器转动超过一圈，重置所有编码器
                if (nowPositionEncoder1 >= resolutionRatio || nowPositionEncoder2 >= resolutionRatio || nowPositionEncoder3 >= resolutionRatio){
                    resetAllEncoder();
                }
            }
        }

        //重置所有编码器
        public void resetAllEncoder(){
            Encoder1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Encoder2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Encoder3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lastPositionEcoder1 = 0;
            lastPositionEcoder2 = 0;
            lastPositionEcoder3 = 0;
            lastAbsoluteOrientation = 0;
            resetAbsoluteOrientation = nowAbsoluteOrientation;
        }

        //获取当前绝对角度
        public double getNowAbsoluteOrientation(){
            return nowAbsoluteOrientation;
        }

        //获取当前绝对位置
        public double[] getNowAbsolutePosition(){
            return nowAbsolutePosition;
        }
    }


}
