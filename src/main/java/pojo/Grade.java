package pojo;

public class Grade {
    private String term;
    private String name;
    private String score;
    private String credit;
    private String gradePoint;
    private String term_again;
    private String exam_type;
    private String course_type;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(String gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getTerm_again() {
        return term_again;
    }

    public void setTerm_again(String term_again) {
        this.term_again = term_again;
    }

    public String getExam_type() {
        return exam_type;
    }

    public void setExam_type(String exam_type) {
        this.exam_type = exam_type;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "term='" + term + '\'' +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", credit='" + credit + '\'' +
                ", gradePoint='" + gradePoint + '\'' +
                '}';
    }
}
