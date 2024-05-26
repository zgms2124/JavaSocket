package Client.Service;

import Client.Frame.ClientFrame;
import SharedModule.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Vector;

public class ListeningThread extends Thread{
    private final ClientFrame curFrame;  //当前窗体

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
                            System.out.println("系统通知");
                            dealSystemNotice(response);
                            break;
                        case FORCED_OFFLINE:
                            System.out.println("强制下线");
                            dealForcedLogout();
                            break;
                        case FRIEND_LOGIN:
                            System.out.println("好友上线");
                            dealFriendLogin(response);
                            break;
                        case FRIEND_LOGOUT:
                            System.out.println("好友下线");
                            dealFriendLogout(response);
                            break;
                        case SEND_MESSAGE:
                            System.out.println("处理聊天消息");
                            dealMessage(response);
                            break;
                        case ADD_FRIEND:
                            System.out.println("处理被添加好友请求");
                            dealAddFriendRequest(response);
                            break;
                        case ADD_GROUP:
                            System.out.println("处理被添加群组请求");
                            dealAddGroupRequest(response);
                            break;
                        case SUCCESS_ADD:
                            System.out.println("处理成功添加好友响应");
                            dealAddFriendResponse(response);
                            break;
                        case SUCCESS_ADD_GROUP:
                            System.out.println("处理成功添加群组响应");
                            // Handle case where adding to group is successful
                            dealAddGroupResponse(response);
                            break;
//                        case REFUSE_ADD:
//                            System.out.println("处理添加好友响应（不同意）");
//                            dealAddFriendResponse(response);
//                            break;
//                        case REFUSE_ADD_GROUP:
//                            System.out.println("处理添加群组响应（不同意）");
//                            // Handle case where adding to group is successful
//                            dealAddGroupResponse(response);
//                            break;
                        case WRONG_ID:
                            System.out.println("处理添加好友响应（好友不存在）");
                            dealAddFriendResponse(response);
                            break;
                        case WRONG_GROUP_ID:
                            System.out.println("处理添加群组的错误响应（群组不存在）");
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

    //处理系统通知
    public void dealSystemNotice(Response response){
        JOptionPane.showMessageDialog(curFrame,
                response.getDataByKey("notice"),
                "服务器通知", JOptionPane.INFORMATION_MESSAGE);
    }
     
    //处理强制下线
    public void dealForcedLogout(){
        JOptionPane.showMessageDialog(curFrame,
                "您已被强制下线",
                "系统通知", JOptionPane.INFORMATION_MESSAGE);
        curFrame.dispose();
        System.exit(0);
    }
    
    //处理好友上线
    public void dealFriendLogin(Response response){
        DefaultTableModel friendsTableModel = curFrame.getFriendsTableModel();
        UserInfo loginFriend = (UserInfo) response.getDataByKey("userInfo");
        SingleBuffer.getOnlineFriends().add(loginFriend); //在线缓存中添加该用户
        if (curFrame.getCurSendToUserID() != null && curFrame.getCurSendToUserID().equals(loginFriend.getUserID())){
            curFrame.setCurSendToUserState("在线");
            curFrame.getSendTargetField1().setText("好友：" + curFrame.getCurSendToUserNickname() +" 在线");
        }
        
        //设置好友在线状态
        Vector<Vector> friends = friendsTableModel.getDataVector();
        for (int i = 0; i < friends.size(); i++) {
            Vector<String> friend = friends.get(i);
            if (friend.get(1).equals(loginFriend.getUserID())) {
                friendsTableModel.removeRow(i);
                break;
            }
        }
        
        //头部插入
        friendsTableModel.insertRow(0, new String[]{loginFriend.getNickName(), loginFriend.getUserID(), "在线"});
    }

    //处理好友下线
    public void dealFriendLogout(Response response){
        DefaultTableModel friendsTableModel = curFrame.getFriendsTableModel();
        UserInfo logoutFriend = (UserInfo) response.getDataByKey("userInfo");
        SingleBuffer.getOnlineFriends().remove(logoutFriend);//在线缓存中移除该用户
        if (curFrame.getCurSendToUserID() != null && curFrame.getCurSendToUserID().equals(logoutFriend.getUserID())){
            curFrame.setCurSendToUserState("离线");
            curFrame.getSendTargetField1().setText("好友：" + curFrame.getCurSendToUserNickname() +" 离线");
        }
        
        //设置好友在线状态
        Vector<Vector> friends = friendsTableModel.getDataVector();
        for (int i = 0; i < friends.size(); i++) {
            Vector<String> friend = friends.get(i);
            if (friend.get(1).equals(logoutFriend.getUserID())) {
                friendsTableModel.removeRow(i);
                break;
            }
        }
        //尾部插入
        friendsTableModel.insertRow(friendsTableModel.getRowCount(), new String[]{logoutFriend.getNickName(), logoutFriend.getUserID(), "离线"});
    }
    
    //处理聊天消息
    public void dealMessage(Response response){
        Object message = response.getDataByKey("msg");
        if (message instanceof P2PMessage){
            StringBuilder curSb = SingleBuffer.getP2pMessageHistory().get(((P2PMessage) message).getSendUserID());
            curSb.append(message.toString());
            if (curFrame.getCurSendToUserID() != null && curFrame.getCurSendToUserID().equals(((P2PMessage) message).getSendUserID())) {
                curFrame.getMessageShowArea1().append(message.toString());
            }
            JOptionPane.showMessageDialog(curFrame,
                    ((P2PMessage) message).getSendUserID() + "给您发来消息",
                    "新消息", JOptionPane.INFORMATION_MESSAGE);
        } else if (message instanceof BoardMessage){
            curFrame.getMessageShowArea2().append(message.toString());
            JOptionPane.showMessageDialog(curFrame,
                    "广播有新消息",
                    "新消息", JOptionPane.INFORMATION_MESSAGE);
        }else if (message instanceof GroupMessage){
            StringBuilder curSb = SingleBuffer.getGroupChatMessageHistory().get(((GroupMessage) message).getGroupID());
            curSb.append(message.toString());
//            if (curFrame.getCurGroupID() != null && curFrame.getCurGroupID().equals(((GroupMessage) message).getSendUserID())) {
            curFrame.getMessageShowArea3().append(message.toString());
//            }
//                }
//            }
            JOptionPane.showMessageDialog(curFrame,
                    "群 " + ((GroupMessage) message).getGroupID() + " 有新消息",
                    "新消息", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    //处理添加好友请求
    public void dealAddFriendRequest(Response response){
        UserInfo user = (UserInfo) response.getDataByKey("userInfo");
        SingleBuffer.getFriends().add(user);
        SingleBuffer.getOnlineFriends().add(user);
        SingleBuffer.getP2pMessageHistory().put(user.getUserID(), new StringBuilder());
        curFrame.getFriendsTableModel().insertRow(0, new String[]{user.getNickName(), user.getUserID(), "在线"});
        JOptionPane.showMessageDialog(curFrame,
                user.getNickName() + "添加您为好友",
                "新好友", JOptionPane.INFORMATION_MESSAGE);
    }

    //处理收到的添加群组通知
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
                response.getDataByKey("fromUserID")+"已添加至群组：" + groupInfo.getGroupName(),
                "新人加入", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //处理添加好友响应
    private void dealAddFriendResponse(Response response) {
        if (response.getResponseType() == ResponseType.WRONG_ID){
            JOptionPane.showMessageDialog(curFrame,
                    "此账号不存在",
                    "错误账号", JOptionPane.INFORMATION_MESSAGE);
        } else if(response.getResponseType() == ResponseType.SUCCESS_ADD){
            JOptionPane.showMessageDialog(curFrame,
                    "成功添加好友",
                    "添加成功", JOptionPane.INFORMATION_MESSAGE);
            SingleBuffer.getFriends().add((UserInfo) response.getDataByKey("userInfo"));
            if ((boolean) response.getDataByKey("isOnline")) {
                SingleBuffer.getOnlineFriends().add((UserInfo) response.getDataByKey("userInfo"));
                curFrame.getFriendsTableModel().insertRow(0, new String[]{((UserInfo) response.getDataByKey("userInfo")).getNickName(),
                        ((UserInfo) response.getDataByKey("userInfo")).getUserID(),
                        "在线"});
            } else{
                curFrame.getFriendsTableModel().insertRow(0, new String[]{((UserInfo) response.getDataByKey("userInfo")).getNickName(),
                        ((UserInfo) response.getDataByKey("userInfo")).getUserID(),
                        "离线"});
            }
            SingleBuffer.getP2pMessageHistory().put(((UserInfo) response.getDataByKey("userInfo")).getUserID(), new StringBuilder());
        }
//        else if (response.getResponseType() == ResponseType.REFUSE_ADD) {
//            JOptionPane.showMessageDialog(curFrame,
//                    "您的好友申请被拒绝了",
//                    "添加失败", JOptionPane.INFORMATION_MESSAGE);
//        }
    }

    //处理添加群组请求的响应
    private void dealAddGroupResponse(Response response) {
        if (response.getResponseType() == ResponseType.WRONG_GROUP_ID) {
            JOptionPane.showMessageDialog(curFrame,
                    "此群组不存在",
                    "错误群组", JOptionPane.INFORMATION_MESSAGE);
        } else if (response.getResponseType() == ResponseType.SUCCESS_ADD_GROUP) {
            GroupInfo groupInfo = (GroupInfo) response.getDataByKey("groupInfo");
//         Update the group list table model
            JOptionPane.showMessageDialog(curFrame,
                    "成功加入群组：" + groupInfo.getGroupName(),
                    "加入成功", JOptionPane.INFORMATION_MESSAGE);
            // You might want to update your group chat list UI here
            curFrame.getGroupChatsTableModel().insertRow(0, new String[]{groupInfo.getGroupId(), groupInfo.getGroupName()});
            SingleBuffer.getP2pMessageHistory().put(groupInfo.getGroupId(), new StringBuilder());

        }
//      else if (response.getResponseType() == ResponseType.REFUSE_ADD_GROUP) {
//            JOptionPane.showMessageDialog(curFrame,
//                    "您的加群申请被拒绝了",
//                    "添加失败", JOptionPane.INFORMATION_MESSAGE);
//        }
    }


}
