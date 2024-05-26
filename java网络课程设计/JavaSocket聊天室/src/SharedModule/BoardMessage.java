package SharedModule;

import java.io.Serializable;
import java.util.Calendar;

public class BoardMessage implements Serializable,Comparable<BoardMessage>{  //Serializable序列化提供的接口
    private String sendUserID;
    private Calendar sendTime;
    private String content;

    public BoardMessage(String sendUserID, String content){
        this.sendUserID = sendUserID;
        this.content = content;
        this.sendTime = Calendar.getInstance();
    }

    @Override
    public int compareTo(BoardMessage o) {
        return this.sendTime.compareTo(o.getSendTime());
    }
    public String getSendUserID() {
        return sendUserID;
    }
    public Calendar getSendTime() {
        return sendTime;
    }
    public String getContent() {
        return content;
    }
    @Override
    public String toString(){
        return sendUserID + "(" + sendTime.getTime() + "):\n" + content + "\n";
    }
}
