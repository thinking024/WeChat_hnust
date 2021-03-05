package pojo;

public class AccessToken {
    private String value;
    private int expiresIn;
    private String ticket;

    public AccessToken() {
    }

    public AccessToken(String value, int expiresIn, String ticket) {
        this.value = value;
        this.expiresIn = expiresIn;
        this.ticket = ticket;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "accessToken='" + value + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", ticket='" + ticket + '\'' +
                '}';
    }
}
