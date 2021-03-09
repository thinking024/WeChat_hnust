package pojo;

import java.sql.Date;

public class User {
    private String account;
    private String password;
    private String openId;
    private Date expire;
    private String info;
    private int currentWeek;

    public User() {

    }

    public User(String account, String password, String openId) {
        this.account = account;
        this.password = password;
        this.openId = openId;
    }

    public User(String account, String password, String openId, Date expire, String info, int currentWeek) {
        this.account = account;
        this.password = password;
        this.openId = openId;
        this.expire = expire;
        this.info = info;
        this.currentWeek = currentWeek;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", openId='" + openId + '\'' +
                ", expire=" + expire +
                ", info='" + info + '\'' +
                ", currentWeek=" + currentWeek +
                '}';
    }
}
