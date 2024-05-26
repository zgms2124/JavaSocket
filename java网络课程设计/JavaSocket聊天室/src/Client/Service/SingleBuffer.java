/*客户端在运行时的缓存数据*/
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
	//饿汉式加载单例实例
    private static final SingleBuffer singleBuffer = new SingleBuffer();

    //本地用户信息
    private static UserInfo userInfo;
    //与服务器通信套接字
    private static Socket socket;
    //与服务器通信输入流
    private static ObjectInputStream ois;
    //与服务器通信输出流
    private static ObjectOutputStream oos;
    //在线好友列表
    private static ArrayList<UserInfo> onlineFriends;
    //好友列表
    private static ArrayList<UserInfo> friends;
    // List of group chats which the user is member of
    private static ArrayList<GroupInfo> groupChats;

    //私聊聊天记录
    private static final HashMap<String, StringBuilder> p2pMessageHistory = new HashMap<>();
    // 群聊聊天记录，以群组ID为key
    private static final HashMap<String, StringBuilder> groupMessageHistory = new HashMap<>();

    //服务器IP地址
    private static String SERVER_IP = "127.0.0.1";
    //服务器端口号
    private static int SERVER_PORT = 8086;
    static {
        System.out.println("创建套接字");
        try {
            SingleBuffer.socket = new Socket(SERVER_IP, SERVER_PORT);
            //先获取输出流，否则会阻塞
            SingleBuffer.oos = new ObjectOutputStream(SingleBuffer.socket.getOutputStream());
            //再获取输入流
            SingleBuffer.ois = new ObjectInputStream(SingleBuffer.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //构造函数私有化保证单例
    private SingleBuffer(){}
    // 添加和获取群聊历史记录的方法
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
