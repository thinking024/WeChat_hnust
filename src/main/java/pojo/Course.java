package pojo;

public class Course {
    private String openId;
    private String name;
    private int day;
    private int orderBegin;
    private int orderEnd;
    private String classroom;
    public Course() {
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getOrderBegin() {
        return orderBegin;
    }

    public void setOrderBegin(int orderBegin) {
        this.orderBegin = orderBegin;
    }

    public int getOrderEnd() {
        return orderEnd;
    }

    public void setOrderEnd(int orderEnd) {
        this.orderEnd = orderEnd;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    @Override
    public String toString() {
        return "Course{" +
                "openId='" + openId + '\'' +
                ",name='" + name + '\'' +
                ", day='" + day + '\'' +
                ", orderBegin='" + orderBegin + '\'' +
                ", orderEnd='" + orderEnd + '\'' +
                ", classroom='" + classroom + '\'' +
                '}';
    }
}

