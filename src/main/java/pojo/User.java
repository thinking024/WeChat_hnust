package pojo;

public class User {
    private String account;
    private String password;
    private String openId;

    public User() {

    }

    public User(String account, String password, String openId) {
        this.account = account;
        this.password = password;
        this.openId = openId;
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

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", openId='" + openId + '\'' +
                '}';
    }
}
