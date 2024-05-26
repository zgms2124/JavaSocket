package Server.Service;

import Client.Service.SingleBuffer;
import SharedModule.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Server{

    private HashMap<String, UserInfo> regUsersInfo = new HashMap<>(200); //��ע���û���Ϣ
    private HashMap<String, GroupInfo> regGroupsInfo = new HashMap<>(200); //����Ⱥ����Ϣ
    private HashSet<String> usedNickname = new HashSet<>(200); //��ʹ���û��ǳ�
    private final HashMap<String, ClientIO> onlineUserIO = new HashMap<>(); //�����û�����TCP�׽��ֵ����������
    private HashMap<String, ArrayList<UserInfo>> friendShip = new HashMap<>(); //���ѹ�ϵ �û�ID-�û���Ϣ
    private HashMap<String, ArrayList<GroupInfo>> groupCharts = new HashMap<>(); //���ѹ�ϵ �û�ID-Ⱥ����Ϣ
    private DefaultTableModel onlineUsersTableModel; //�����������û���ʾ�������
    private HashMap<String, ArrayList<UserInfo>> groupMembers = new HashMap<>();//Ⱥ��-Ⱥ��Ա
    private JTextArea feedbackArea;
    public Server() throws IOException {
        loadServerData();  //��������
        printData();
    }

    private void printData() {
        // չʾ������ע���û���Ϣ
        System.out.println("��ǰ�������洢���û���");
        regUsersInfo.forEach((key, user) -> {
            System.out.println("Key (UserID): " + key + ", User Information: " + user.toString());
        });

// չʾ������ʹ�õ��ǳ�
        System.out.println("\n��ǰ�������洢����ʹ���ǳƣ�");
        usedNickname.forEach(nickname -> {
            System.out.println("Nickname: " + nickname);
        });

// չʾ���������ϵ
        System.out.println("\n��ǰ�������洢�������ϵ��");
        friendShip.forEach((key, friendsList) -> {
            System.out.println("Key (UserID): " + key + ", Friends: " + friendsList.toString());
        });

// չʾ������ע��Ⱥ����Ϣ
        System.out.println("\n��ǰ�������洢����ע��Ⱥ����Ϣ��");
        regGroupsInfo.forEach((key, group) -> {
            System.out.println("Key (GroupID): " + key + ", Group Information: " + group.toString());
        });

// չʾ����Ⱥ����Ϣ
        System.out.println("\n��ǰ�������洢��ÿ���û�����Ⱥ����Ϣ��");
        groupCharts.forEach((key, groupInfoList) -> {
            System.out.println("Key (UserID): " + key + ", Group Chats: " + groupInfoList.toString());
        });

// չʾ����Ⱥ��Ա��Ϣ
        System.out.println("\n��ǰ�������洢��ÿ��ȺȺ��Ա��Ϣ��");
        groupMembers.forEach((key, membersList) -> {
            System.out.println("Key (GroupID): " + key + ", Group Members: " + membersList);
        });
    }

    //����ͻ�������
    public void processRequest(Socket curSocket){
        try{
            ObjectInputStream ois = new ObjectInputStream(curSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(curSocket.getOutputStream());
            ClientIO userIO = new ClientIO(ois, oos);
            boolean flag = true;
            while(flag){ //��ͣ�ض�ȡ�ͻ��˷��������������
                //�������������ж�ȡ���ͻ����ύ���������
                Request clientRe = (Request) ois.readObject();
                System.out.println("------------------");
                System.out.println("Server��ȡ�˿ͻ��˵�����,������ʽ = " + clientRe.getAction());
                System.out.println(clientRe.getDataByKey("msg"));
                switch (clientRe.getAction()) {
                    case SIGN_IN:
                        System.out.println("�����¼");
                        dealSignIn(clientRe, userIO);
                        break;
                    case SIGN_UP:
                        System.out.println("����ע��");
                        dealSignUp(clientRe, userIO);
                        break;
                    case SEND_MESSAGE:
                        System.out.println("������Ϣ����");
                        dealSendMessage(clientRe);
                        break;
                    case LOG_OUT:
                        System.out.println("�����û�����");
                        dealLogout(clientRe, userIO);
                        flag = false;//�û����ߣ�ֹͣ��ȡ����
                        break;
                    case ADD_FRIEND:
                        System.out.println("������Ӻ���");
                        dealAddFriend(clientRe, userIO);
                        break;
                    case ADD_GROUP:
                        System.out.println("�������Ⱥ��");
                        dealAddGroup(clientRe, userIO);
                        break;
                }
                System.out.println("------------------");
            }
        }catch(Exception ignore){
            ignore.printStackTrace();
        }
    }
    
    //�����¼
    public void dealSignIn(Request clientRequest, ClientIO userIO) throws IOException {
        String userID = (String) clientRequest.getDataByKey("userID");
        String password = (String) clientRequest.getDataByKey("password");
        Response response;
        if (!regUsersInfo.containsKey(userID)){  //�û���������
            response = new Response(ResponseStatus.OK, ResponseType.WRONG_ID);
            sendResponse(response, userIO.getOos());//������Ӧ
        } else if (!regUsersInfo.get(userID).getPassword().equals(password)){  //���벻��ȷ
            response = new Response(ResponseStatus.OK, ResponseType.WRONG_PWD);
            sendResponse(response, userIO.getOos());
        } else if(onlineUserIO.containsKey(userID)) { //�ظ���¼
            response = new Response(ResponseStatus.OK, ResponseType.SECOND_LOGIN);
            sendResponse(response, userIO.getOos());
        } else{   //��½�ɹ�
            System.out.println("��¼�ɹ�");
            response = new Response(ResponseStatus.OK, ResponseType.SUCCESS_SIGN_IN);
            response.addData("userInfo", regUsersInfo.get(userID));  //�����û���Ϣ
            ArrayList<UserInfo> friends = friendShip.get(userID);
            response.addData("userFriends", friends);  //�����û������б�
            response.addData("groups",groupCharts.get(userID));
            
            //��������û��б�
            ArrayList<UserInfo> onlineFriends = new ArrayList<>();
            if (friends != null) {
                for (UserInfo curUser : friends) 
                    if (onlineUserIO.containsKey(curUser.getUserID())) 
                        onlineFriends.add(curUser);
            }
            response.addData("userOnlineFriends", onlineFriends);  //�����û����ߺ����б�
            System.out.println("���ݼ������");
            sendResponse(response, userIO.getOos());//������Ӧ
            System.out.println("���ͳɹ�");
            onlineUserIO.put(userID, userIO);//�������û�IO Map����Ӹ��û������������
            addUserToOnlineUsersTable(userID, regUsersInfo.get(userID).getNickName());//���·����������û���ʾ

            //֪ͨ�������ߺ��ѣ����û�����
            response = new Response(ResponseStatus.OK, ResponseType.FRIEND_LOGIN);
            response.addData("userInfo", regUsersInfo.get(userID));
            if (friends != null) {
                for (UserInfo receiveUser : friends) 
                    if (onlineUserIO.containsKey(receiveUser.getUserID()))
                        sendResponse(response, onlineUserIO.get(receiveUser.getUserID()).getOos());
            }
        }
        System.out.println("����������ɴ����¼");
    }

    public void dealSignUp(Request clientRequest, ClientIO userIO) throws Exception {
        UserInfo user = (UserInfo) clientRequest.getDataByKey("user");
        if (usedNickname.contains(user.getNickName())) { //�ǳ��Ѿ�����
            Response response = new Response(ResponseStatus.OK, ResponseType.NICKNAME_EXIST);
            ObjectOutputStream oos = userIO.getOos();
            sendResponse(response, oos); //������Ӧ
        } else {
            synchronized (this.regUsersInfo) { //����
                String userID = generateUniqueUserID();
                //�����˺�
                user.setUserID(userID);
                System.out.println("�����˺�Ϊ��" + userID);
                regUsersInfo.put(userID, user); //��ӵ���ע���û�
                usedNickname.add(user.getNickName()); //��ӵ���ʹ��nickname
                friendShip.put(userID, new ArrayList<>()); //�����պ��ѹ�ϵ

                // Create a new group with the user as the group owner
                String groupName = user.getNickName() + "'s Group";
                GroupInfo newGroup = new GroupInfo("group"+userID, groupName, user);
                newGroup.addMember(user,user); // Add user to their own group
                regGroupsInfo.put(newGroup.getGroupId(), newGroup); // Add new group to the registered groups
                if (!groupCharts.containsKey(userID)) {
                    groupCharts.put(userID, new ArrayList<>());
                }
                groupCharts.get(userID).add(newGroup); // Add new group to the user's list of groups
                groupMembers.put(newGroup.getGroupId(), new ArrayList<UserInfo>() ); // Initialize members list for the group
                groupMembers.get("group"+userID).add(user);

                Response response = new Response(ResponseStatus.OK, ResponseType.SUCCESS_SIGN_UP);
                response.addData("userID", userID);
                ObjectOutputStream oos = userIO.getOos();
                sendResponse(response, oos); //������Ӧ
                System.out.println("����������ɴ���ע��");
            }
        }
    }
    private String generateUniqueUserID() {
        Calendar c = Calendar.getInstance();
        String userID;
        //��������˺�
        do {
            StringBuilder account = new StringBuilder();
            account.append(String.valueOf(c.get(Calendar.YEAR)).substring(2));
            account.append(String.format("%02d", c.get(Calendar.MONTH) + 1));
            account.append(String.format("%02d", c.get(Calendar.DATE)));
            account.append(String.format("%02d", c.get(Calendar.HOUR_OF_DAY)));
            account.append(new Random().nextInt(1000));
            userID = account.toString();
        } while (regUsersInfo.containsKey(userID));
        return userID;
    }
    
    //������Ϣ����
    public void dealSendMessage(Request clientRequest) throws IOException {
        Object msg = clientRequest.getDataByKey("msg");
        Response response = new Response(ResponseStatus.OK, ResponseType.SEND_MESSAGE);
        response.addData("msg", msg);
        if (msg instanceof BoardMessage){  //Ⱥ����Ϣ
            for(Map.Entry<String, ClientIO> receiveUser : onlineUserIO.entrySet())  //�����������û�������Ӧ
                if (!receiveUser.getKey().equals(((BoardMessage) msg).getSendUserID()))  //�������Լ���Map.Entry��Map������һ���ڲ��ӿڣ��˽ӿ�Ϊ���ͣ�����ΪEntry<K,V>
                    sendResponse(response, receiveUser.getValue().getOos()); 
            feedbackArea.append("--------�㲥��Ϣ--------\n");
            feedbackArea.append(msg.toString());
            feedbackArea.append("---------------------\n");
        } else if(msg instanceof P2PMessage){	//˽����Ϣ
            String receiveUserID = ((P2PMessage) msg).getReceiveUserID();
            sendResponse(response, onlineUserIO.get(receiveUserID).getOos());//��˽�Ķ�������Ӧ
        }
        else if (msg instanceof GroupMessage) {    // Ⱥ����Ϣ
            String groupId = ((GroupMessage) msg).getGroupID();
            ArrayList<UserInfo> members = groupMembers.get(groupId);
            String sendUserId=((GroupMessage) msg).getSendUserID();

//            StringBuilder curSb = SingleBuffer.getGroupChatMessageHistory().get(groupId);
//            curSb.append(msg.toString()); // ����Ӧ����һ����������ʽ��GroupMessage������������ʷ�г���

            if(members != null) {
                for (UserInfo userInfo : members) {
                    if(userInfo==null){
                        continue;
                    }
                    String memberId = userInfo.getUserID();
                    if(sendUserId.equals(memberId)){
                        continue;
                    }
                    // ����Ա�Ƿ�����
                    ClientIO memberIO = onlineUserIO.get(memberId);
                    if (memberIO != null) {
                        // ��msgת����Ⱥ���е�ÿ�����߳�Ա
                        sendResponse(response, memberIO.getOos());
                    }
                }
            }

            feedbackArea.append("----Ⱥ��[" + groupId + "]��Ϣ----\n");
            feedbackArea.append(msg.toString());
            feedbackArea.append("-----------------------\n");
        }
    }
    
    //����˷�����Ӧ����
    public void sendResponse(Response response, ObjectOutputStream oos) throws IOException {
        oos.writeObject(response);  //������д������
        oos.flush();  //ˢ����
    }
    
    //�����û�����
    public void dealLogout(Request clientRequest, ClientIO userIO) throws IOException{
        String logoutUserID = (String) clientRequest.getDataByKey("userID");
        UserInfo userInfo = regUsersInfo.get(logoutUserID);
        System.out.println("IDΪ" + userInfo.getUserID() + "���û�����");
        onlineUserIO.remove(userInfo.getUserID());//�ѵ�ǰ���߿ͻ��˵�IO��Map��ɾ��
        userIO.getOos().close();
        userIO.getOis().close();
        removeUserFromOnlineUsersTable(userInfo.getUserID());

        Response response = new Response(ResponseStatus.OK, ResponseType.FRIEND_LOGOUT);
        response.addData("userInfo", userInfo);
        //��ȡ���е����ߺ��ѣ����û�����
        ArrayList<UserInfo> friends = friendShip.get(userInfo.getUserID());
        if (friends != null) {
            for (UserInfo user : friends) 
                if (onlineUserIO.containsKey(user.getUserID()))  //��ǰ��������,��֪ͨ�ú���
                    sendResponse(response, onlineUserIO.get(user.getUserID()).getOos());
        }
    }
    
    //������Ӻ���
    public void dealAddFriend(Request clientRequest, ClientIO userIO) throws IOException {
        String userID = (String) clientRequest.getDataByKey("userID");
        String fromUserID = (String) clientRequest.getDataByKey("fromUserID");
        Response response;
        if (!regUsersInfo.containsKey(userID)){//û�д��û�
            response = new Response(ResponseStatus.OK, ResponseType.WRONG_ID);
            sendResponse(response, userIO.getOos());
        } else {
        	friendShip.get(userID).add(regUsersInfo.get(fromUserID));
        	friendShip.get(fromUserID).add(regUsersInfo.get(userID));
            //�����󷽻�Ӧ
            response = new Response(ResponseStatus.OK, ResponseType.SUCCESS_ADD);
            response.addData("userInfo", regUsersInfo.get(userID));
            response.addData("isOnline", onlineUserIO.containsKey(userID));
            sendResponse(response, userIO.getOos());


            //֪ͨ����ӷ�
            if (onlineUserIO.containsKey(userID)) {
                response = new Response(ResponseStatus.OK, ResponseType.ADD_FRIEND);
                response.addData("userInfo", regUsersInfo.get(fromUserID));
                sendResponse(response, onlineUserIO.get(userID).getOos());
            }
        }
    }

    public void dealAddGroup(Request clientRequest, ClientIO userIO) throws IOException {
        String groupID = (String) clientRequest.getDataByKey("groupID");
        String fromUserID = (String) clientRequest.getDataByKey("userID");
        Response response;

        if (!regGroupsInfo.containsKey(groupID)){ // Check if the group exists
            response = new Response(ResponseStatus.OK, ResponseType.WRONG_GROUP_ID);
            sendResponse(response, userIO.getOos());
        } else {
            GroupInfo groupInfo = regGroupsInfo.get(groupID);
            groupInfo.getMembers().add(regUsersInfo.get(fromUserID));
            groupMembers.get(groupID).add(regUsersInfo.get(fromUserID));
            groupCharts.get(fromUserID).add(regGroupsInfo.get(groupID));

            try {

                response = new Response(ResponseStatus.OK, ResponseType.SUCCESS_ADD_GROUP);
                response.addData("groupInfo", groupInfo);
                sendResponse(response, userIO.getOos());

                // Notify all online group members of the new addition
                for (UserInfo member : groupInfo.getMembers()) {
                    if (onlineUserIO.containsKey(member.getUserID())&&!member.getUserID().equals(fromUserID)) {
                        response = new Response(ResponseStatus.OK, ResponseType.ADD_GROUP);
                        response.addData("groupInfo", groupInfo);
                        response.addData("fromUserID",fromUserID);
                        sendResponse(response, onlineUserIO.get(member.getUserID()).getOos());
                    }
                }
            } catch (Exception e) {
                // Handle exception when adding member fails (e.g., not group owner or user already a member)
                response = new Response(ResponseStatus.OK, ResponseType.FAILURE);
                sendResponse(response, userIO.getOos());
            }
        }
    }
    
    //ǿ������
    public void removeUser(String userID) throws IOException{
        Response response = new Response(ResponseStatus.OK, ResponseType.FORCED_OFFLINE);
        response.addData("notice", "ϵͳ֪ͨ������ǿ�����ߣ�");
        ObjectOutputStream oos = onlineUserIO.get(userID).getOos();
        sendResponse(response, oos);//������Ӧ
        onlineUserIO.remove(userID);//�ѵ�ǰ���߿ͻ��˵�IO��Map��ɾ��
        response = new Response(ResponseStatus.OK, ResponseType.FRIEND_LOGOUT);
        response.addData("userInfo", regUsersInfo.get(userID));
        //��ȡ���е����ߺ��ѣ����û�����
        ArrayList<UserInfo> friends = friendShip.get(userID);
        if (friends != null) {
            for (UserInfo user : friends) 
                if (onlineUserIO.containsKey(user.getUserID()))  //��ǰ��������,��֪ͨ�ú���
                    sendResponse(response, onlineUserIO.get(user.getUserID()).getOos());
        }
    }
    
    //Ⱥ��ϵͳ��Ϣ
    public void massTexting(String sysMessage, JTextArea feedbackArea) throws IOException {
        Response response = new Response(ResponseStatus.OK, ResponseType.SYSTEM_NOTICE);
        response.addData("notice", sysMessage);
        for (Map.Entry<String, ClientIO> user : onlineUserIO.entrySet())
            sendResponse(response, user.getValue().getOos());    
        feedbackArea.append("-------������Ⱥ��-------\n");
        feedbackArea.append("Ⱥ���ɹ�������" + onlineUserIO.size() + "�������û�����ϵͳ��Ϣ��\n");
        feedbackArea.append("----------------------\n");
    }

    public void saveServerData(){
        String filename = "E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\src\\Server\\Service\\database.dat";
        try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(filename))) {
            objOut.writeObject(regUsersInfo);
            objOut.writeObject(usedNickname);
            objOut.writeObject(friendShip);
            objOut.writeObject(groupMembers);
            objOut.writeObject(regGroupsInfo);
            objOut.writeObject(groupCharts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadServerData(){
        String filename = "E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\src\\Server\\Service\\database.dat";
        try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(filename))) {
            regUsersInfo = (HashMap<String, UserInfo>) objIn.readObject();
            usedNickname = (HashSet<String>) objIn.readObject();
            friendShip = (HashMap<String, ArrayList<UserInfo>>) objIn.readObject();
            groupMembers = (HashMap<String, ArrayList<UserInfo>>) objIn.readObject();
            regGroupsInfo = (HashMap<String, GroupInfo>) objIn.readObject();
            groupCharts = (HashMap<String, ArrayList<GroupInfo>>) objIn.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("Data file not found.");
        } catch (IOException e) {
            System.out.println("Error reading from data file.");
        } catch (ClassNotFoundException e) {
            System.out.println("Error casting to class type.");
        }
    }
    
    //�û����ߣ��������û�����ͼ���Ƴ��û�
    private void removeUserFromOnlineUsersTable(String userID){
        Vector vector = onlineUsersTableModel.getDataVector();
        int size = vector.size();
        int targetRowIndex = -1;
        for (int i = 0; i < size; i++) {
            Vector user = (Vector) vector.get(i);
            if (user.get(0).equals(userID)) {
                targetRowIndex = i;
                break;
            }
        }
        onlineUsersTableModel.removeRow(targetRowIndex);
    }
    
    //�������û�����ͼ������û�
    private void addUserToOnlineUsersTable(String userID, String nickname){
        onlineUsersTableModel.addRow(new String[]{userID, nickname});
    }
    public void setOnlineUsersTableModel(DefaultTableModel onlineUsersTableModel) {
        this.onlineUsersTableModel = onlineUsersTableModel;
    }
    public void setFeedbackArea(JTextArea feedbackArea) {
        this.feedbackArea = feedbackArea;
    }
}
