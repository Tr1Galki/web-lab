package web.server;

import java.util.Date;

public class Dot {
    private Boolean inArea;
    private Double x, y, r;
    private Date date;
    private Integer time;
    private String owner;

    public Dot() {
    }

    public Dot(Boolean isInArea, Double x, Double y, Double r, Date date, Integer time, String owner) {
        this.inArea = isInArea;
        this.x = x;
        this.y = y;
        this.r = r;
        this.date = date;
        this.time = time;
        this.owner = owner;
    }

    public Boolean getInArea() {
        return inArea;
    }

    public void setInArea(Boolean inArea) {
        this.inArea = inArea;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public Date getDate() {
        return date;
    }

    public String getStringDate() {
        return date.toString()  ;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
