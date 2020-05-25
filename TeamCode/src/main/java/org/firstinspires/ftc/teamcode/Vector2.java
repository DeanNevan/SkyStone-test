package org.firstinspires.ftc.teamcode;

//import static java.lang.Math.*;

public class Vector2 {
    private double x = 0;
    private double y = 0;
    Vector2 (double x, double y){
        this.x = x;
        this.y = y;
    }

    public String getString() {
        return Double.toString(x) + ", " + Double.toString(y);
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() { return y; }
    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2 rotated(double deg){
        double x1 = x * Math.cos(Math.toRadians(deg)) - y * Math.sin(Math.toRadians(deg));
        double y1 = y * Math.cos(Math.toRadians(deg)) + x * Math.sin(Math.toRadians(deg));
        return new Vector2(x1, y1);
    }

    public Vector2 normalized(){
        double length = length();
        return new Vector2(x / length, y / length);
    }

    public double length(){
        return Math.sqrt(x * x + y * y);
    }

    public void plus(Vector2 target){
        this.x += target.getX();
        this.y += target.getY();
    }

    public void minus(Vector2 target){
        this.x -= target.getX();
        this.y -= target.getY();
    }

    public void multiply(int target){
        this.x *= target;
        this.y *= target;
    }

    public void multiply(float target){
        this.x *= target;
        this.y *= target;
    }

    public void multiply(double target){
        this.x *= target;
        this.y *= target;
    }

    public void devide(int target){
        this.x /= target;
        this.y /= target;
    }

    public void devide(float target){
        this.x /= target;
        this.y /= target;
    }

    public void devide(double target){
        this.x /= target;
        this.y /= target;
    }
}
