/*�ͻ���������ʱ�Ļ�������*/
package Client.Service;

import SharedModule.GroupInfo;
import SharedModule.UserInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SingleBuffer {
	//����ʽ���ص���ʵ��
    private static final SingleBuffer singleBuffer = new SingleBuffer();

    //�����û���Ϣ
    private static UserInfo userInfo;
    //�������ͨ���׽���
    private static Socket socket;
    //�������ͨ��������
    private static ObjectInputStream ois;
    //�������ͨ�������
    private static ObjectOutputStream oos;
    //���ߺ����б�
    private static ArrayList<UserInfo> onlineFriends;
    //�����б�
    private static ArrayList<UserInfo> friends;
    // List of group chats which the user is member of
    private static ArrayList<GroupInfo> groupChats;

    //˽�������¼
    private static final HashMap<String, StringBuilder> p2pMessageHistory = new HashMap<>();
    // Ⱥ�������¼����Ⱥ��IDΪkey
    private static final HashMap<String, StringBuilder> groupMessageHistory = new HashMap<>();

    //������IP��ַ
    private static String SERVER_IP = "127.0.0.1";
    //�������˿ں�
    private static int SERVER_PORT = 8086;
    static {
        System.out.println("�����׽���");
        try {
            SingleBuffer.socket = new Socket(SERVER_IP, SERVER_PORT);
            //�Ȼ�ȡ����������������
            SingleBuffer.oos = new ObjectOutputStream(SingleBuffer.socket.getOutputStream());
            //�ٻ�ȡ������
            SingleBuffer.ois = new ObjectInputStream(SingleBuffer.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //���캯��˽�л���֤����
    private SingleBuffer(){}
    // ��Ӻͻ�ȡȺ����ʷ��¼�ķ���
//    public static void putGroupMessageHistory(String groupId, StringBuilder history) {
//        groupMessageHistory.put(groupId, history);
//    }

    public static HashMap<String, StringBuilder> getGroupChatMessageHistory() {
        return groupMessageHistory;
    }

    public static void setGroupChats(ArrayList<GroupInfo> groupChats) {
        SingleBuffer.groupChats = groupChats;
    }

    // Method to get group chat list
    public static ArrayList<GroupInfo> getGroupChats() {
        return groupChats;
    }

    // Method to add a group chat to the user's list of groups
    public static void addGroupChat(GroupInfo groupChat) {
        if (!groupChats.contains(groupChat)) {
            groupChats.add(groupChat);
        }
    }

    // Method to retrieve a group chat's info by ID
    public static GroupInfo getGroupChatById(String groupId) {
        for (GroupInfo group : groupChats) {
            if (group.getGroupId().equals(groupId)) {
                return group;
            }
        }
        return null; // or throw an exception as per your error handling
    }


    // Method to append a message to the group chat history
    public static void appendToGroupChatHistory(String groupId, String message) {
        StringBuilder history = groupMessageHistory.get(groupId);
        if (history != null) {
            history.append(message).append("\n");
        } else {
            history = new StringBuilder(message).append("\n");
            groupMessageHistory.put(groupId, history);
        }
    }

    public static void setUserInfo(UserInfo user) {
        SingleBuffer.userInfo = user;
    }
    public static void setOnlineFriends(ArrayList<UserInfo> onlineFriends) {
        SingleBuffer.onlineFriends = onlineFriends;
    }
    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }
    public static void setServerPort(int serverPort) {
        SERVER_PORT = serverPort;
    }
    public static void setFriends(ArrayList<UserInfo> friends) {
        SingleBuffer.friends = friends;
    }
    
    public static UserInfo getUserInfo() {
        return userInfo;
    }
    public static Socket getSocket() {
        return socket;
    }
    public static ObjectInputStream getOis() {
        return ois;
    }
    public static ObjectOutputStream getOos() {
        return oos;
    }
    public static List<UserInfo> getOnlineFriends() {
        return onlineFriends;
    }
    public static String getServerIp() {
        return SERVER_IP;
    }
    public static int getServerPort() {
        return SERVER_PORT;
    }
    public static ArrayList<UserInfo> getFriends() {
        return friends;
    }
    public static HashMap<String, StringBuilder> getP2pMessageHistory() {
        return p2pMessageHistory;
    }
}
