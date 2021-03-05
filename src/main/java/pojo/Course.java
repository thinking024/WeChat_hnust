package pojo;

public class Course {
    private String id;
    private String name;
    private int day;
    private int weekBegin;
    private int weekEnd;
    private int orderBegin;
    private int orderEnd;
    private String classroom;
    public Course() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getWeekBegin() {
        return weekBegin;
    }

    public void setWeekBegin(int weekBegin) {
        this.weekBegin = weekBegin;
    }

    public int getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(int weekEnd) {
        this.weekEnd = weekEnd;
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
                "name='" + name + '\'' +
                ", day='" + day + '\'' +
                ", orderBegin='" + orderBegin + '\'' +
                ", orderEnd='" + orderEnd + '\'' +
                ", classroom='" + classroom + '\'' +
                '}';
    }
}

