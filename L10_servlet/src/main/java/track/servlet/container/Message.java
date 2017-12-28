package track.servlet.container;

public class Message {
    String ownerLogin;
    String text;
    long ts;

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "track.servlet.container.Message{" +
                "ownerLogin='" + ownerLogin + '\'' +
                ", text='" + text + '\'' +
                ", ts='" + ts + '\'' +
                '}';
    }
}
