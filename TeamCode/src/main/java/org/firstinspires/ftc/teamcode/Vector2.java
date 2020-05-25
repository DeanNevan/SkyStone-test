package org.firstinspires.ftc.teamcode;

//import static java.lang.Math.*;

public class Vector2 {
    private double x = 0;
    private double y = 0;
    Vector2 (double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * 获得x值
     * @return x值
     */
    public double getX() {
        return x;
    }

    /**
     * 设置x值
     * @param x x值
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * 获得y值
     * @return y值
     */
    public double getY() {
        return y;
    }

    /**
     * 设置y值
     * @param y y值
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * 设置x、y值
     * @param x x值
     * @param y y值
     */
    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * 获取向量旋转一定度数后的向量
     * @param deg 旋转读数
     * @return 旋转后的向量值
     */
    public Vector2 rotated(double deg){
        double x1 = x * Math.cos(Math.toRadians(deg)) - y * Math.sin(Math.toRadians(deg));
        double y1 = y * Math.cos(Math.toRadians(deg)) + x * Math.sin(Math.toRadians(deg));
        return new Vector2(x1, y1);
    }

    /**
     * 向量归一化
     * @return 获取向量归一化后的向量
     */
    public Vector2 normalized(){
        double length = length();
        return new Vector2(x / length, y / length);
    }

    /**
     * 获得与目标向量的夹角
     * @param target 目标向量
     * @return 二者夹角
     */
    public double angleTo(Vector2 target){
        double x1 = x;
        double x2 = target.getX();
        double y1 = y;
        double y2 = target.getY();
        return Math.acos((x1*x2+y1*y2) / Math.sqrt(x1*x1+y1*y1) * Math.sqrt(x2*x2+y2*y2));
    }

    /**
     * 获取向量长度
     * @return 向量长度
     */
    public double length(){
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 获得与目标向量相加后的向量
     * @param target 目标向量
     * @return 运算后的向量
     */
    public Vector2 plus(Vector2 target){
        return new Vector2(x + target.getX(), y + target.getY());
    }

    /**
     * 获得与目标向量相减后的向量
     * @param target 目标向量
     * @return 运算后的向量
     */
    public Vector2 minus(Vector2 target){
        return new Vector2(x - target.getX(), y - target.getY());
    }

    /**
     * 获得与目标数相乘后的向量
     * @param target 目标数值
     * @return 运算后的向量
     */
    public Vector2 multiply(int target){
        return new Vector2(x * target, y * target);
    }

    /**
     * 获得与目标数相乘后的向量
     * @param target 目标数值
     * @return 运算后的向量
     */
    public Vector2 multiply(float target){
        return new Vector2(x * target, y * target);
    }

    /**
     * 获得与目标数相乘后的向量
     * @param target 目标数值
     * @return 运算后的向量
     */
    public Vector2 multiply(double target){
        return new Vector2(x * target, y * target);
    }

    /**
     * 获得与目标数相除后的向量
     * @param target 目标数值
     * @return 运算后的向量
     */
    public Vector2 devide(int target){
        return new Vector2(x / target, y / target);
    }

    /**
     * 获得与目标数相除后的向量
     * @param target 目标数值
     * @return 运算后的向量
     */
    public Vector2 devide(float target){
        return new Vector2(x / target, y / target);
    }

    /**
     * 获得与目标数相除后的向量
     * @param target 目标数值
     * @return 运算后的向量
     */
    public Vector2 devide(double target){
        return new Vector2(x / target, y / target);
    }

    /**
     * 上向量
     * @return 上向量
     */
    public static Vector2 UP(){
        return new Vector2(0, -1);
    }

    /**
     * 下向量
     * @return 下向量
     */
    public static Vector2 DOWN(){
        return new Vector2(0, 1);
    }

    /**
     * 右向量
     * @return 右向量
     */
    public static Vector2 RIGHT(){
        return new Vector2(1, 0);
    }

    /**
     * 左向量
     * @return 左向量
     */
    public static Vector2 LEFT(){
        return new Vector2(-1, 0);
    }
}
