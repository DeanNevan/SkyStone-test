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
     * 获取向量长度
     * @return 向量长度
     */
    public double length(){
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 获得与目标向量相加后的向量
     * @param target 目标向量
     */
    public void plus(Vector2 target){
        this.x += target.getX();
        this.y += target.getY();
    }

    /**
     * 获得与目标向量相减后的向量
     * @param target 目标向量
     */
    public void minus(Vector2 target){
        this.x -= target.getX();
        this.y -= target.getY();
    }

    /**
     * 获得与目标数相乘后的向量
     * @param target 目标数值
     */
    public void multiply(int target){
        this.x *= target;
        this.y *= target;
    }

    /**
     * 获得与目标数相乘后的向量
     * @param target 目标数值
     */
    public void multiply(float target){
        this.x *= target;
        this.y *= target;
    }

    /**
     * 获得与目标数相乘后的向量
     * @param target 目标数值
     */
    public void multiply(double target){
        this.x *= target;
        this.y *= target;
    }

    /**
     * 获得与目标数相除后的向量
     * @param target 目标数值
     */
    public void devide(int target){
        this.x /= target;
        this.y /= target;
    }

    /**
     * 获得与目标数相除后的向量
     * @param target 目标数值
     */
    public void devide(float target){
        this.x /= target;
        this.y /= target;
    }

    /**
     * 获得与目标数相除后的向量
     * @param target 目标数值
     */
    public void devide(double target){
        this.x /= target;
        this.y /= target;
    }
}
