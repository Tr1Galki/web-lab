package web.server.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    private String isInArea;
    private String x;
    private String y;
    private String r;
    private String date;
    private String time;

    public UserBean() {
        this.isInArea = "";
        this.x = "";
        this.y = "";
        this.r = "";
        this.date = "";
        this.time = "";
    }

    public UserBean(String isInArea, String x, String y, String r, String date, String time) {
        this.isInArea = isInArea;
        this.x = x;
        this.y = y;
        this.r = r;
        this.date = date;
        this.time = time;
    }

    public String getIsInArea() {
        return isInArea;
    }

    public void setIsInArea(String isInArea) {
        this.isInArea = isInArea;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
