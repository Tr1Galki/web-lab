package web.server;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Date;

//@Entity(name = "Dot")
@Named("dot")
@SessionScoped
public class Dot implements Serializable {
    private Boolean inArea;
    private Double x, y, r;
    private Date date;
    private Integer time;
    private String owner;
    private String creator;

    public Dot() {
    }

    public Dot(Boolean isInArea, Double x, Double y, Double r, Date date, Integer time, String owner, String creator) {
        this.inArea = isInArea;
        this.x = x;
        this.y = y;
        this.r = r;
        this.date = date;
        this.time = time;
        this.owner = owner;
        this.creator = creator;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
