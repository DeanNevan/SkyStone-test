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
import com.qualcomm.robotcore.util.ElapsedTime;

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

@TeleOp(name="MoveController", group="Linear Opmode")
//@Disabled
public class MoveController extends LinearOpMode {
    Base base = new Base();

    private Navigator navigator = new Navigator();//定位器
    private ThreadNavigator threadNavigator = new ThreadNavigator();//定位器的线程

    private ElapsedTime runtime = new ElapsedTime();//运行时间计时器

    @Override
    public void runOpMode() {
        base.init(hardwareMap);
        base.navigator = navigator;

        navigator.debugMode = true;
        navigator.init(hardwareMap);

        waitForStart();
        runtime.reset();

        threadNavigator.start();

        while (opModeIsActive()) {
            //如果不处于自动驾驶状态，就处理手柄输入并驱动底盘
            if (!base.isAutoDriving) {
                Vector2 linearVelocityValue = new Vector2(gamepad1.left_stick_y, gamepad1.left_stick_x);
                double angularVelocityValue = gamepad1.right_trigger - gamepad1.left_trigger;

                base.drive(linearVelocityValue, angularVelocityValue, true);

                if (gamepad1.dpad_up){
                    base.fixDrive(Base.fixDriveMode.FORWARD, 1, true);
                }
                if (gamepad1.dpad_down){
                    base.fixDrive(Base.fixDriveMode.BACK, 1, true);
                }
                if (gamepad1.dpad_left){
                    base.fixDrive(Base.fixDriveMode.LEFT, 1, true);
                }
                if (gamepad1.dpad_right){
                    base.fixDrive(Base.fixDriveMode.RIGHT, 1, true);
                }
            }
        }
    }

    /**
     * 定位器的线程
     */
    private class ThreadNavigator extends Thread {
        private ThreadNavigator () {
        }
        public void run() {
            while (opModeIsActive()){
                boolean updateResult = navigator.update(getIntRuntime());
                if (navigator.debugMode && updateResult){
                    telemetry.addLine("---编码器定位系统---");
                    telemetry.addData("运行时间(mm)", getIntRuntime());
                    telemetry.addData("当前坐标", "( %.2f, %.2f )", navigator.getPosition().getX(), navigator.getPosition().getY());
                    telemetry.addData("当前旋转角度", "%.2f", navigator.getRotation());
                }
            }
        }
    }

    /**
     * 返回保留指定位数的浮点数
     * @param decimal 保留小数位数
     * @param number 要操作的目标浮点数
     * @return 运算结果
     */
    private double getDecimalNumber(int decimal, double number){
        BigDecimal b = new BigDecimal(number);
        return b.setScale(decimal, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    /**
     * 获取当前运行时间的毫秒数
     * @return 当前运行时间的毫秒数
     */
    private int getIntRuntime(){
        return (Double.valueOf(runtime.milliseconds())).intValue();
    }
}
