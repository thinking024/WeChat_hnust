package pojo;

public class Exam {
    private String name;
    private String time;
    private String place;

    public Exam() {
    }

    public Exam(String name, String time, String place) {
        this.name = name;
        this.time = time;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                '}';
    }
}
