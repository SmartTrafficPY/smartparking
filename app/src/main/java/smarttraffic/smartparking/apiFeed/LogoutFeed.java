package smarttraffic.smartparking.apiFeed;

public class LogoutFeed {

    public Boolean getMessageReturn() {
        return messageReturn;
    }

    public void setMessageReturn(Boolean messageReturn) {
        this.messageReturn = messageReturn;
    }

    private Boolean messageReturn;

    @Override
    public String toString() {
        return "LogoutFeed{" +
                "messageReturn=" + messageReturn +
                '}';
    }
}
