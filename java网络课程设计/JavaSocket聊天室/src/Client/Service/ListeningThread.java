package Client.Service;

import Client.Frame.ClientFrame;
import SharedModule.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Vector;

public class ListeningThread extends Thread{
    private final ClientFrame curFrame;  //��ǰ����

    public ListeningThread(ClientFrame frame){
        curFrame = frame;
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (true){
                try {
                    Response response = (Response) SingleBuffer.getOis().readObject();
                    switch (response.getResponseType()){
                        case SYSTEM_NOTICE:
                            System.out.println("ϵͳ֪ͨ");
                            dealSystemNotice(response);
                            break;
                        case FORCED_OFFLINE:
                            System.out.println("ǿ������");
                            dealForcedLogout();
                            break;
                        case FRIEND_LOGIN:
                            System.out.println("��������");
                            dealFriendLogin(response);
                            break;
                        case FRIEND_LOGOUT:
                            System.out.println("��������");
                            dealFriendLogout(response);
                            break;
                        case SEND_MESSAGE:
                            System.out.println("����������Ϣ");
                            dealMessage(response);
                            break;
                        case ADD_FRIEND:
                            System.out.println("������Ӻ�������");
                            dealAddFriendRequest(response);
                            break;
                        case ADD_GROUP:
                            System.out.println("�������Ⱥ������");
                            dealAddGroupRequest(response);
                            break;
                        case SUCCESS_ADD:
                            System.out.println("����ɹ���Ӻ�����Ӧ");
                            dealAddFriendResponse(response);
                            break;
                        case SUCCESS_ADD_GROUP:
                            System.out.println("����ɹ����Ⱥ����Ӧ");
                            // Handle case where adding to group is successful
                            dealAddGroupResponse(response);
                            break;
//                        case REFUSE_ADD:
//                            System.out.println("������Ӻ�����Ӧ����ͬ�⣩");
//                            dealAddFriendResponse(response);
//                            break;
//                        case REFUSE_ADD_GROUP:
//                            System.out.println("�������Ⱥ����Ӧ����ͬ�⣩");
//                            // Handle case where adding to group is successful
//                            dealAddGroupResponse(response);
//                            break;
                        case WRONG_ID:
                            System.out.println("������Ӻ�����Ӧ�����Ѳ����ڣ�");
                            dealAddFriendResponse(response);
                            break;
                        case WRONG_GROUP_ID:
                            System.out.println("�������Ⱥ��Ĵ�����Ӧ��Ⱥ�鲻���ڣ�");
                            // Handle case where the group ID is incorrect
                            dealAddGroupResponse(response);
                            break;
                        default:
                            System.out.println("listenThread");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //����ϵͳ֪ͨ
    public void dealSystemNotice(Response response){
        JOptionPane.showMessageDialog(curFrame,
                response.getDataByKey("notice"),
                "������֪ͨ", JOptionPane.INFORMATION_MESSAGE);
    }
     
    //����ǿ������
    public void dealForcedLogout(){
        JOptionPane.showMessageDialog(curFrame,
                "���ѱ�ǿ������",
                "ϵͳ֪ͨ", JOptionPane.INFORMATION_MESSAGE);
        curFrame.dispose();
        System.exit(0);
    }
    
    //�����������
    public void dealFriendLogin(Response response){
        DefaultTableModel friendsTableModel = curFrame.getFriendsTableModel();
        UserInfo loginFriend = (UserInfo) response.getDataByKey("userInfo");
        SingleBuffer.getOnlineFriends().add(loginFriend); //���߻�������Ӹ��û�
        if (curFrame.getCurSendToUserID() != null && curFrame.getCurSendToUserID().equals(loginFriend.getUserID())){
            curFrame.setCurSendToUserState("����");
            curFrame.getSendTargetField1().setText("���ѣ�" + curFrame.getCurSendToUserNickname() +" ����");
        }
        
        //���ú�������״̬
        Vector<Vector> friends = friendsTableModel.getDataVector();
        for (int i = 0; i < friends.size(); i++) {
            Vector<String> friend = friends.get(i);
            if (friend.get(1).equals(loginFriend.getUserID())) {
                friendsTableModel.removeRow(i);
                break;
            }
        }
        
        //ͷ������
        friendsTableModel.insertRow(0, new String[]{loginFriend.getNickName(), loginFriend.getUserID(), "����"});
    }

    //�����������
    public void dealFriendLogout(Response response){
        DefaultTableModel friendsTableModel = curFrame.getFriendsTableModel();
        UserInfo logoutFriend = (UserInfo) response.getDataByKey("userInfo");
        SingleBuffer.getOnlineFriends().remove(logoutFriend);//���߻������Ƴ����û�
        if (curFrame.getCurSendToUserID() != null && curFrame.getCurSendToUserID().equals(logoutFriend.getUserID())){
            curFrame.setCurSendToUserState("����");
            curFrame.getSendTargetField1().setText("���ѣ�" + curFrame.getCurSendToUserNickname() +" ����");
        }
        
        //���ú�������״̬
        Vector<Vector> friends = friendsTableModel.getDataVector();
        for (int i = 0; i < friends.size(); i++) {
            Vector<String> friend = friends.get(i);
            if (friend.get(1).equals(logoutFriend.getUserID())) {
                friendsTableModel.removeRow(i);
                break;
            }
        }
        //β������
        friendsTableModel.insertRow(friendsTableModel.getRowCount(), new String[]{logoutFriend.getNickName(), logoutFriend.getUserID(), "����"});
    }
    
    //����������Ϣ
    public void dealMessage(Response response){
        Object message = response.getDataByKey("msg");
        if (message instanceof P2PMessage){
            StringBuilder curSb = SingleBuffer.getP2pMessageHistory().get(((P2PMessage) message).getSendUserID());
            curSb.append(message.toString());
            if (curFrame.getCurSendToUserID() != null && curFrame.getCurSendToUserID().equals(((P2PMessage) message).getSendUserID())) {
                curFrame.getMessageShowArea1().append(message.toString());
            }
            JOptionPane.showMessageDialog(curFrame,
                    ((P2PMessage) message).getSendUserID() + "����������Ϣ",
                    "����Ϣ", JOptionPane.INFORMATION_MESSAGE);
        } else if (message instanceof BoardMessage){
            curFrame.getMessageShowArea2().append(message.toString());
            JOptionPane.showMessageDialog(curFrame,
                    "�㲥������Ϣ",
                    "����Ϣ", JOptionPane.INFORMATION_MESSAGE);
        }else if (message instanceof GroupMessage){
            StringBuilder curSb = SingleBuffer.getGroupChatMessageHistory().get(((GroupMessage) message).getGroupID());
            curSb.append(message.toString());
//            if (curFrame.getCurGroupID() != null && curFrame.getCurGroupID().equals(((GroupMessage) message).getSendUserID())) {
            curFrame.getMessageShowArea3().append(message.toString());
//            }
//                }
//            }
            JOptionPane.showMessageDialog(curFrame,
                    "Ⱥ " + ((GroupMessage) message).getGroupID() + " ������Ϣ",
                    "����Ϣ", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    //������Ӻ�������
    public void dealAddFriendRequest(Response response){
        UserInfo user = (UserInfo) response.getDataByKey("userInfo");
        SingleBuffer.getFriends().add(user);
        SingleBuffer.getOnlineFriends().add(user);
        SingleBuffer.getP2pMessageHistory().put(user.getUserID(), new StringBuilder());
        curFrame.getFriendsTableModel().insertRow(0, new String[]{user.getNickName(), user.getUserID(), "����"});
        JOptionPane.showMessageDialog(curFrame,
                user.getNickName() + "�����Ϊ����",
                "�º���", JOptionPane.INFORMATION_MESSAGE);
    }

    //�����յ������Ⱥ��֪ͨ
    public void dealAddGroupRequest(Response response) {
        GroupInfo groupInfo = (GroupInfo) response.getDataByKey("groupInfo");
        // Add the group to the local cache
//        SingleBuffer.getGroupChats().add(groupInfo);
        // Initialize the message history for this group
//        SingleBuffer.getGroupChatMessageHistory().put(groupInfo.getGroupId(), new StringBuilder());
//         Update the group list table model
//        curFrame.getGroupChatsTableModel().addRow(new String[]{groupInfo.getGroupId(), groupInfo.getGroupName()});
        // Inform user about the new group invitation
//        SingleBuffer.getGroupChatMessageHistory().put(groupInfo.getGroupId(), new StringBuilder());
        JOptionPane.showMessageDialog(curFrame,
                response.getDataByKey("fromUserID")+"�������Ⱥ�飺" + groupInfo.getGroupName(),
                "���˼���", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //������Ӻ�����Ӧ
    private void dealAddFriendResponse(Response response) {
        if (response.getResponseType() == ResponseType.WRONG_ID){
            JOptionPane.showMessageDialog(curFrame,
                    "���˺Ų�����",
                    "�����˺�", JOptionPane.INFORMATION_MESSAGE);
        } else if(response.getResponseType() == ResponseType.SUCCESS_ADD){
            JOptionPane.showMessageDialog(curFrame,
                    "�ɹ���Ӻ���",
                    "��ӳɹ�", JOptionPane.INFORMATION_MESSAGE);
            SingleBuffer.getFriends().add((UserInfo) response.getDataByKey("userInfo"));
            if ((boolean) response.getDataByKey("isOnline")) {
                SingleBuffer.getOnlineFriends().add((UserInfo) response.getDataByKey("userInfo"));
                curFrame.getFriendsTableModel().insertRow(0, new String[]{((UserInfo) response.getDataByKey("userInfo")).getNickName(),
                        ((UserInfo) response.getDataByKey("userInfo")).getUserID(),
                        "����"});
            } else{
                curFrame.getFriendsTableModel().insertRow(0, new String[]{((UserInfo) response.getDataByKey("userInfo")).getNickName(),
                        ((UserInfo) response.getDataByKey("userInfo")).getUserID(),
                        "����"});
            }
            SingleBuffer.getP2pMessageHistory().put(((UserInfo) response.getDataByKey("userInfo")).getUserID(), new StringBuilder());
        }
//        else if (response.getResponseType() == ResponseType.REFUSE_ADD) {
//            JOptionPane.showMessageDialog(curFrame,
//                    "���ĺ������뱻�ܾ���",
//                    "���ʧ��", JOptionPane.INFORMATION_MESSAGE);
//        }
    }

    //�������Ⱥ���������Ӧ
    private void dealAddGroupResponse(Response response) {
        if (response.getResponseType() == ResponseType.WRONG_GROUP_ID) {
            JOptionPane.showMessageDialog(curFrame,
                    "��Ⱥ�鲻����",
                    "����Ⱥ��", JOptionPane.INFORMATION_MESSAGE);
        } else if (response.getResponseType() == ResponseType.SUCCESS_ADD_GROUP) {
            GroupInfo groupInfo = (GroupInfo) response.getDataByKey("groupInfo");
//         Update the group list table model
            JOptionPane.showMessageDialog(curFrame,
                    "�ɹ�����Ⱥ�飺" + groupInfo.getGroupName(),
                    "����ɹ�", JOptionPane.INFORMATION_MESSAGE);
            // You might want to update your group chat list UI here
            curFrame.getGroupChatsTableModel().insertRow(0, new String[]{groupInfo.getGroupId(), groupInfo.getGroupName()});
            SingleBuffer.getP2pMessageHistory().put(groupInfo.getGroupId(), new StringBuilder());

        }
//      else if (response.getResponseType() == ResponseType.REFUSE_ADD_GROUP) {
//            JOptionPane.showMessageDialog(curFrame,
//                    "���ļ�Ⱥ���뱻�ܾ���",
//                    "���ʧ��", JOptionPane.INFORMATION_MESSAGE);
//        }
    }


}
