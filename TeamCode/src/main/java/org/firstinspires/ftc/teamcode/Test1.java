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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Test1", group="Linear Opmode")
@Disabled
public class Test1 extends LinearOpMode {

    private DcMotor testMotor = null;
    private Servo testServo = null;

    @Override
    public void runOpMode() {

        testMotor = hardwareMap.get(DcMotor.class, "MotorTest");
        testServo = hardwareMap.get(Servo.class, "ServoTest");

        testServo = hardwareMap.get(Servo.class, "233");

        ThreadServo threadServo = new ThreadServo(testServo, 800, 0.2, 0.6);

        waitForStart();
        while (opModeIsActive()) {

            if (gamepad1.left_stick_button) {
                threadServo.start();

            }

            testMotor.setPower(gamepad1.left_stick_y);//我们希望能够在舵机自动转动的时候也能执行这一句代码

        }
    }



    public class ThreadServo extends Thread {
        private Servo servo;
        private int time;
        private double startPosition;
        private double endPosition;


        public ThreadServo (Servo servo, int time, double startPosition, double endPosition) {
            this.servo = servo;
            this.time = time;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
        public void run() {
            try {
                servo.setPosition(startPosition);//转动舵机到startPosition位置
                sleep(time);//等待，时长为time
                servo.setPosition(endPosition);//转动舵机到endPosition位置
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
