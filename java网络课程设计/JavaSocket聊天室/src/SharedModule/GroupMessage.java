package SharedModule;

import java.io.Serializable;
import java.util.Calendar;

public class GroupMessage implements Serializable, Comparable<GroupMessage> {
    private static final long serialVersionUID = 1L;
    private String sendUserID;
    private String groupID;
    private Calendar sendTime;
    private String content;

    public GroupMessage(String sendUserID, String groupID, String content) {
        this.sendUserID = sendUserID;
        this.groupID = groupID;
        this.content = content;
        this.sendTime = Calendar.getInstance();
    }

    @Override
    public int compareTo(GroupMessage other) {
        return this.sendTime.compareTo(other.getSendTime());
    }

    public String getSendUserID() {
        return sendUserID;
    }

    public String getGroupID() {
        return groupID;
    }

    public Calendar getSendTime() {
        return sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setSendUserID(String sendUserID) {
        this.sendUserID = sendUserID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setSendTime(Calendar sendTime) {
        this.sendTime = sendTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return sendUserID + " (" + sendTime.getTime() + ") [Group: " + groupID + "]:\n" + content + "\n";
    }
}
