package Client.Frame;



import Client.Service.ClientSendRequest;
import Client.Service.ListeningThread;
import Client.Service.SingleBuffer;
import SharedModule.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class ClientFrame extends JFrame {
    private DefaultTableModel friendsTableModel;  //表格模型，可以对表格进行增删改
    private DefaultTableModel groupChatsTableModel;
    private JTextField nicknameField;
    private JTextField sexField;
    private JTextField sendTargetField1;  //私聊区域
    private JTextArea messageShowArea1;
    private JTextArea inputMessageArea1;
    private JTextField sendTargetField2;
    private JTextArea messageShowArea2;
    private JTextArea inputMessageArea2;
    private String sendToUserID;
    private String sendToUserNickname;
    private String sendToUserState;
    private JTextField sendTargetField3; // 群聊区域，显示当前所在的群聊名称
    private JTextArea inputMessageArea3; // 群聊信息输入区域
    private String curGroupID; // 当前选中的群组ID
    private JTextArea messageShowArea3; // 显示群组聊天记录的文本区域

    // Getter和Setter方法
    public void setCurGroupID(String curGroupID) {
        this.curGroupID = curGroupID;
    }

    public String getCurGroupID() {
        return curGroupID;
    }

    public JTextArea getMessageShowArea3() {
        return messageShowArea3;
    }
    
    public static void main(String[] args) {
    	ClientFrame frame = new ClientFrame();
    }
    public ClientFrame(){
        this.init();
    }
    private void init(){
        this.setTitle("聊天室");
        this.setSize(900, 600);
        //设置默认窗体在屏幕中央
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation((x - this.getWidth()) / 2, (y-this.getHeight()) / 2);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.BLACK); //获取内容面板

        //私聊面板
        JPanel p2pPanel = new JPanel();
        p2pPanel.setLayout(null);
        
        //好友列表
        JTable friendsTable = new JTable();  // 创建新的 JTable(表格) 实例，此表格用于显示好友列表
        friendsTable.setRowHeight(30); // 设置表格的每一行的高度为 30
        friendsTable.setEnabled(false); // 设置表格不可被改写内容，即表格内容不可编辑
        DefaultTableCellRenderer r = new DefaultTableCellRenderer(); // 创建默认的表格单元格渲染器用于自定义单元格显示方式
        r.setHorizontalAlignment(JLabel.CENTER); // 设置渲染器水平居中对齐，这样表格内容会在单元格中居中显示
        friendsTable.setDefaultRenderer(Object.class, r); // 为表格中的所有类对象设置默认的渲染器
       // 创建并定义表格模型对象的实例，以下设置定义啦列名和表格数据
        friendsTableModel = new DefaultTableModel();
        String[] friendsTableColumnNames = {"昵称", "账号", "状态"}; // 表格的列名（行头）
        Object[][] friendsTableData = {}; // 表格的数据，初始化为空
        friendsTableModel.setDataVector(friendsTableData, friendsTableColumnNames); // 绑定行头和数据到表格模型
        friendsTable.setModel(friendsTableModel); // 把表格模型数据绑定到表格，这样表格就会显示出模型中的数据
        JScrollPane friendsTableScroll = new JScrollPane(friendsTable); // 创建一个带滚动条的面板，并且将之前创建的表格加入到这个面板中
        friendsTableScroll.setBounds(0, 0, 320, 600);  // 设置面板的边界大小，即设置面板的位置和宽高
        friendsTableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 设置一直显示垂直滚动条
        
        //为好友列表表格添加右键，可以进行发送消息的操作
        friendsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {  //若是鼠标右键点击
                    final int row = friendsTable.rowAtPoint(e.getPoint()); //进行定位是在哪一行
                    if(row != -1){
                        final JPopupMenu popUp = new JPopupMenu();  //弹出菜单
                        JMenuItem sendMessage = new JMenuItem("发送消息");  //弹出菜单的内容或者选项
                        sendMessage.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                sendToUserNickname = (String) friendsTableModel.getValueAt(row, 0);  //定位到行获取昵称
                                sendToUserID = (String) friendsTableModel.getValueAt(row, 1);
                                sendToUserState = (String) friendsTableModel.getValueAt(row, 2);
                                sendTargetField1.setText("好友:" + sendToUserNickname + " " + sendToUserState);
                                //载入私聊聊天记录
                                messageShowArea1.setText(SingleBuffer.getP2pMessageHistory().get(sendToUserID).toString());
                            }
                        });
                        popUp.add(sendMessage);
                        popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });


        //群聊面板
        JPanel GroupPanel = new JPanel();
        GroupPanel.setLayout(null);


        // 群聊列表
        JTable groupChatsTable = new JTable();
        groupChatsTable.setRowHeight(30);
        groupChatsTable.setEnabled(false);
        DefaultTableCellRenderer r2 = new DefaultTableCellRenderer();
        r2.setHorizontalAlignment(JLabel.CENTER);
        groupChatsTable.setDefaultRenderer(Object.class, r2);

        // 创建并定义表格模型对象的实例
        groupChatsTableModel = new DefaultTableModel();
        String[] groupChatsTableColumnNames = {"群组ID", "群组名"};
        Object[][] groupChatsTableData = {};
        groupChatsTableModel.setDataVector(groupChatsTableData, groupChatsTableColumnNames);
        groupChatsTable.setModel(groupChatsTableModel);

        JScrollPane groupChatsTableScroll = new JScrollPane(groupChatsTable);
        groupChatsTableScroll.setBounds(0, 0, 320, 600);
        groupChatsTableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 为群聊列表表格添加右键，可以跳转到相应的群聊
        groupChatsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {  // 若是鼠标右键点击
                    final int row = groupChatsTable.rowAtPoint(e.getPoint()); // 进行定位是在哪一行
                    if(row != -1){
                        final JPopupMenu popUp = new JPopupMenu();  // 弹出菜单
                        JMenuItem enterGroupChat = new JMenuItem("进入群聊");  // 弹出菜单的内容或者选项
                        enterGroupChat.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                curGroupID = (String) groupChatsTableModel.getValueAt(row, 0); // 定位到行获取群组ID
                                String groupName = (String) groupChatsTableModel.getValueAt(row, 1); // 获取群组名称
                                sendTargetField3.setText("群聊：" + groupName);
                                // 载入群聊聊天记录，你可能需要实现 getGroupChatMessageHistory 这个方法
                                messageShowArea3.setText(SingleBuffer.getGroupChatMessageHistory().get(curGroupID).toString());
                            }
                        });
                        popUp.add(enterGroupChat);
                        popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        
        //私聊聊天区
        JPanel p2pChatPanel = new JPanel();
        p2pChatPanel.setBounds(320, 0, 550, 600);
        JPanel titlePanel1 = new JPanel();
        titlePanel1.setPreferredSize(new Dimension(550, 40)); //标题放置的区域
        titlePanel1.setBackground(Color.white);
        JPanel contentPanel1 = new JPanel();
        contentPanel1.setPreferredSize(new Dimension(550, 350));
        JPanel inputPanel1 = new JPanel();
        inputPanel1.setPreferredSize(new Dimension(550, 200));
        inputPanel1.setBackground(Color.white);
        p2pChatPanel.add(titlePanel1, BorderLayout.NORTH);
        p2pChatPanel.add(contentPanel1, BorderLayout.CENTER);
        p2pChatPanel.add(inputPanel1, BorderLayout.SOUTH);
        
        //发送目标
        sendTargetField1 = new JTextField();
        sendTargetField1.setEditable(false);
        sendTargetField1.setHorizontalAlignment(JTextField.CENTER);  //文本居中对齐
        sendTargetField1.setFont(new Font("宋体", Font.BOLD, 15));
        sendTargetField1.setPreferredSize(new Dimension(170, 30));
        titlePanel1.add(sendTargetField1);
        
        //消息显示区
        messageShowArea1 = new JTextArea();
        messageShowArea1.setFont(new Font("宋体", Font.BOLD, 15));
        messageShowArea1.setPreferredSize(new Dimension(550, 450));
        messageShowArea1.setEditable(false);
        messageShowArea1.setLineWrap(true);    //自动换行
        messageShowArea1.setWrapStyleWord(true);  //断行不断字
        contentPanel1.add(messageShowArea1);
        
        //聊天输入区
        inputMessageArea1 = new JTextArea();
        inputMessageArea1.setPreferredSize(new Dimension(550, 100));
        inputMessageArea1.setFont(new Font("宋体", Font.BOLD, 15));
        inputMessageArea1.setLineWrap(true);        //自动换行
        inputMessageArea1.setWrapStyleWord(true);    //断行不断字
        
        //发送按钮
        JButton sendBtn1 = new JButton("发送");
        sendBtn1.setPreferredSize(new Dimension(80, 30));
        sendBtn1.setFocusPainted(false);//去焦点
        sendBtn1.setBackground(new Color(27, 127, 176));
        sendBtn1.setFont(new Font("宋体", Font.BOLD, 16));
        sendBtn1.setForeground(Color.white);//设置字体颜色
        inputPanel1.add(inputMessageArea1, BorderLayout.NORTH);
        inputPanel1.add(sendBtn1, BorderLayout.CENTER);
        
        p2pPanel.add(friendsTableScroll);
        p2pPanel.add(p2pChatPanel);

        //广播界面
        JPanel groupPanel = new JPanel();
        JPanel titlePanel2 = new JPanel();
        titlePanel2.setPreferredSize(new Dimension(900, 40));
        titlePanel2.setBackground(Color.white);
        JPanel contentPanel2 = new JPanel();
        contentPanel2.setPreferredSize(new Dimension(900, 350));
        JPanel inputPanel2 = new JPanel();
        inputPanel2.setPreferredSize(new Dimension(900, 200));
        inputPanel2.setBackground(Color.white);
        groupPanel.add(titlePanel2, BorderLayout.NORTH);
        groupPanel.add(contentPanel2, BorderLayout.CENTER);
        groupPanel.add(inputPanel2, BorderLayout.SOUTH);
        
        //发送目标
        sendTargetField2 = new JTextField();
        sendTargetField2.setEditable(false);
        sendTargetField2.setHorizontalAlignment(JTextField.CENTER);
        sendTargetField2.setFont(new Font("宋体", Font.BOLD, 15));
        sendTargetField2.setPreferredSize(new Dimension(200, 30));
        sendTargetField2.setText("广播");
        titlePanel2.add(sendTargetField2);
        
        //消息显示区
        messageShowArea2 = new JTextArea();
        messageShowArea2.setPreferredSize(new Dimension(820, 350));
        messageShowArea2.setEditable(false);
        messageShowArea2.setFont(new Font("宋体", Font.BOLD, 15));
        messageShowArea2.setLineWrap(true);        //自动换行
        messageShowArea2.setWrapStyleWord(true);    //断行不断字
        contentPanel2.add(messageShowArea2);
        
        //聊天输入区
        inputMessageArea2 = new JTextArea();
        inputMessageArea2.setPreferredSize(new Dimension(800, 100));
        inputMessageArea2.setFont(new Font("宋体", Font.BOLD, 15));
        inputMessageArea2.setLineWrap(true);        //自动换行
        inputMessageArea2.setWrapStyleWord(true);    // 断行不断字
        
        //发送按钮
        JButton sendBtn2 = new JButton("发送");
        sendBtn2.setPreferredSize(new Dimension(100, 30));
        sendBtn2.setFocusPainted(false);//去焦点
        sendBtn2.setBackground(new Color(27, 127, 176));
        sendBtn2.setFont(new Font("宋体", Font.BOLD, 16));
        sendBtn2.setForeground(Color.white);
        inputPanel2.add(inputMessageArea2, BorderLayout.NORTH);
        inputPanel2.add(sendBtn2, BorderLayout.CENTER);


        // 群聊聊天区


        JPanel groupChatPanel = new JPanel();
        groupChatPanel.setBounds(320, 0, 550, 600);
        JPanel titlePanel3 = new JPanel();
        titlePanel3.setPreferredSize(new Dimension(550, 40));
        titlePanel3.setBackground(Color.white);
        JPanel contentPanel3 = new JPanel();
        contentPanel3.setPreferredSize(new Dimension(550, 350));
        JPanel inputPanel3 = new JPanel();
        inputPanel3.setPreferredSize(new Dimension(550, 200));
        inputPanel3.setBackground(Color.white);
        groupChatPanel.add(titlePanel3, BorderLayout.NORTH);
        groupChatPanel.add(contentPanel3, BorderLayout.CENTER);
        groupChatPanel.add(inputPanel3, BorderLayout.SOUTH);

        //发送目标
        sendTargetField3 = new JTextField();
        sendTargetField3.setEditable(false);
        sendTargetField3.setHorizontalAlignment(JTextField.CENTER);  //文本居中对齐
        sendTargetField3.setFont(new Font("宋体", Font.BOLD, 15));
        sendTargetField3.setPreferredSize(new Dimension(170, 30));
        titlePanel3.add(sendTargetField3);

        //消息显示区
        messageShowArea3 = new JTextArea();
        messageShowArea3.setPreferredSize(new Dimension(550, 450));
        messageShowArea3.setFont(new Font("宋体", Font.BOLD, 15));
        messageShowArea3.setEditable(false);
        messageShowArea3.setLineWrap(true);
        messageShowArea3.setWrapStyleWord(true);
        contentPanel3.add(messageShowArea3);

        //聊天输入区
        inputMessageArea3 = new JTextArea();
        inputMessageArea3.setPreferredSize(new Dimension(550, 100));
        inputMessageArea3.setFont(new Font("宋体", Font.BOLD, 15));
        inputMessageArea3.setLineWrap(true);
        inputMessageArea3.setWrapStyleWord(true);

        //发送按钮
        JButton sendBtn3 = new JButton("发送");
        sendBtn3.setPreferredSize(new Dimension(80, 30));
        sendBtn3.setFocusPainted(false);
        sendBtn3.setBackground(new Color(27, 127, 176));
        sendBtn3.setFont(new Font("宋体", Font.BOLD, 16));
        sendBtn3.setForeground(Color.white);
        inputPanel3.add(inputMessageArea3, BorderLayout.NORTH);
        inputPanel3.add(sendBtn3, BorderLayout.CENTER);

        GroupPanel.add(groupChatsTableScroll);
        GroupPanel.add(groupChatPanel);


        //个人信息
        JPanel selfInfoPanel = new JPanel();
        selfInfoPanel.setLayout(new GridLayout(12,1,10,10)); //6行1列，水平间距10，垂直间距5
        JLabel nicknameLabel = new JLabel("昵称：");
        JLabel sexLabel = new JLabel("性别：");
        nicknameLabel.setFont(new Font("宋体", Font.BOLD, 16));
        sexLabel.setFont(new Font("宋体", Font.BOLD, 16));
        nicknameField = new JTextField();
        sexField = new JTextField();

        //设置内容居中
        nicknameField.setHorizontalAlignment(JTextField.CENTER);
        sexField.setHorizontalAlignment(JTextField.CENTER);
        
        //设置字体样式
        nicknameField.setFont(new Font("宋体", Font.BOLD, 16));
        sexField.setFont(new Font("宋体", Font.BOLD, 16));
        
        //设置不可编辑
        nicknameField.setEditable(false);
        sexField.setEditable(false);
        
        //添加组件，网格布局
        selfInfoPanel.add(nicknameLabel);
        selfInfoPanel.add(nicknameField);
        selfInfoPanel.add(sexLabel);
        selfInfoPanel.add(sexField);

        //添加好友
        JPanel addPanel = new JPanel();
        addPanel.setLayout(null);
        JLabel addFriendIDLabel = new JLabel("请输入要添加的好友账号：");
        addFriendIDLabel.setFont(new Font("宋体", Font.BOLD, 16));//设置字体样式
        addFriendIDLabel.setBounds(300,50,250, 30);
        addPanel.add(addFriendIDLabel);
        JTextField addFriendIDField = new JTextField();
        addFriendIDField.setBounds(275, 80, 250, 40);
        addFriendIDField.setHorizontalAlignment(JTextField.CENTER);
        
        //设置字体样式
        addFriendIDField.setFont(new Font("宋体", Font.BOLD, 16));
        addPanel.add(addFriendIDField);
        JButton addBtn = new JButton("添加");
        addBtn.setBounds(350,150,100, 30);
        addBtn.setFocusPainted(false);//去焦点
        addBtn.setBackground(new Color(27, 127, 176));
        addBtn.setFont(new Font("宋体", Font.BOLD, 16));
        addBtn.setForeground(Color.white);
        addPanel.add(addBtn);

        // 添加群组
        JPanel addGroupPanel = new JPanel();
        addGroupPanel.setLayout(null);
        JLabel addGroupIDLabel = new JLabel("请输入要添加的群组ID：");
        addGroupIDLabel.setFont(new Font("宋体", Font.BOLD, 16)); // 设置字体样式
        addGroupIDLabel.setBounds(300, 50, 250, 30);
        addGroupPanel.add(addGroupIDLabel);
        JTextField addGroupIDField = new JTextField();
        addGroupIDField.setBounds(275, 80, 250, 40);
        addGroupIDField.setHorizontalAlignment(JTextField.CENTER);


       // 设置字体样式
        addGroupIDField.setFont(new Font("宋体", Font.BOLD, 16));
        addGroupPanel.add(addGroupIDField);
        JButton addGroupBtn = new JButton("添加");
        addGroupBtn.setBounds(350, 150, 100, 30);
        addGroupBtn.setFocusPainted(false); // 去焦点
        addGroupBtn.setBackground(new Color(27, 127, 176));
        addGroupBtn.setFont(new Font("宋体", Font.BOLD, 16));
        addGroupBtn.setForeground(Color.white);
        addGroupPanel.add(addGroupBtn);

        // 创建一个新的面板来包含添加好友面板和添加群组面板
        JPanel addPanelContainer = new JPanel();
        addPanelContainer.setLayout(new BoxLayout(addPanelContainer, BoxLayout.Y_AXIS));
       // 将添加好友面板和添加群组面板分别添加到新的面板中
        addPanelContainer.add(addPanel);
        addPanelContainer.add(addGroupPanel);


        // 创建选项卡面板
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(900, 600));
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        Image friendsIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java网络课程设计\\JavaSocket聊天室\\static\\user.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image groupIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java网络课程设计\\JavaSocket聊天室\\static\\megaphone.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image selfInfoIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java网络课程设计\\JavaSocket聊天室\\static\\home.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image addIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java网络课程设计\\JavaSocket聊天室\\static\\add.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image group2Icon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java网络课程设计\\JavaSocket聊天室\\static\\users-alt.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        // 创建好友界面选项卡
        tabbedPane.addTab(null, new ImageIcon(friendsIcon), p2pPanel, "查看好友");
        // 创建广播界面选项卡
        tabbedPane.addTab(null, new ImageIcon(groupIcon), groupPanel, "广播");
        // 创建群聊界面选项卡
        tabbedPane.addTab(null, new ImageIcon(group2Icon), GroupPanel, "群聊");
        // 创建添加界面选项卡
        tabbedPane.addTab(null, new ImageIcon(addIcon), addPanelContainer, "添加");
        // 创建个人信息界面选项卡
        tabbedPane.addTab(null, new ImageIcon(selfInfoIcon), selfInfoPanel, "查看个人信息");
        tabbedPane.setUI(new MyTabbedPaneUI()); //设置选项卡的UI
        // 设置默认选中的选项卡
        tabbedPane.setSelectedIndex(0);
        this.add(tabbedPane);

        //开启监听线程
        new ListeningThread(this).start();
        //私聊发送按钮绑定事件
        sendBtn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendP2pMessage(inputMessageArea1.getText());
                    inputMessageArea1.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        
        //广播发送按钮绑定事件
        sendBtn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendBoardMessage(inputMessageArea2.getText());
                    inputMessageArea2.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        //群聊发送按钮绑定事件
        sendBtn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // sendToGroupChat是用来发送群聊消息的方法，你需要实现这个方法
                    sendGroupMessage(inputMessageArea3.getText());
                    // 清空输入区域
                    inputMessageArea3.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        
        //添加好友按钮绑定事件
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addFriend(addFriendIDField.getText());
                } catch (InterruptedException | ClassNotFoundException | IOException err) {
                    err.printStackTrace();
                }
            }
        });
        //添加群聊按钮绑定事件
        addGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addGroup(addGroupIDField.getText());

                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });
        
        //退出客户端
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "是否要退出？", "退出确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    ClientFrame.this.dispose();
                    Request request = new Request(RequestType.LOG_OUT);
                    request.addData("userID", SingleBuffer.getUserInfo().getUserID());
                    try {
                        ClientSendRequest.sendNotForResponse(request);//向服务器发送退出报文
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        loadInitData();//载入好友、群组、个人信息等

        this.setVisible(true);
    }
    
    //加载本地缓存数据
    private void loadInitData(){
        nicknameField.setText(SingleBuffer.getUserInfo().getNickName());
        sexField.setText(SingleBuffer.getUserInfo().getSex().toString());
        //加载所有好友
        if (SingleBuffer.getFriends() != null) {
            for (UserInfo friend : SingleBuffer.getFriends()) {
                friendsTableModel.addRow(new String[]{friend.getNickName(), friend.getUserID(), "离线"});
            }
        }
        //设置好友在线状态
        if (SingleBuffer.getOnlineFriends() != null) {
            for (UserInfo onlineFriend : SingleBuffer.getOnlineFriends()) {
                Vector<Vector> friends = friendsTableModel.getDataVector();
                for (int i = 0; i < friends.size(); i++) {
                    Vector<String> friend = friends.get(i);
                    if (friend.get(1).equals(onlineFriend.getUserID())) {
                        friendsTableModel.removeRow(i);
                        break;
                    }
                }
                friendsTableModel.insertRow(0, new String[]{onlineFriend.getNickName(), onlineFriend.getUserID(), "在线"});
            }
        }
        //加载好友聊天记录
        if (SingleBuffer.getFriends() != null) {
            for (UserInfo friend : SingleBuffer.getFriends()) {
                SingleBuffer.getP2pMessageHistory().put(friend.getUserID(), new StringBuilder());
            }
        }

        ArrayList<GroupInfo> groupChats = SingleBuffer.getGroupChats();
        if (groupChats != null) {
            for (GroupInfo groupChat : groupChats) {
                // Add row to the group chat table
                groupChatsTableModel.addRow(new String[]{groupChat.getGroupId(), groupChat.getGroupName()});

                // Initialize group chat message history if not already present
                if (!SingleBuffer.getGroupChatMessageHistory().containsKey(groupChat.getGroupId())) {
                    SingleBuffer.getGroupChatMessageHistory().put(groupChat.getGroupId(), new StringBuilder());
                }
            }
        }




    }
    
    //发送私聊消息
    public void sendP2pMessage(String message) throws IOException {
        Request request = new Request(RequestType.SEND_MESSAGE);
        if (sendToUserID != null){ //私发消息
            if (sendToUserState.equals("在线")) {
                P2PMessage p2pMessage = new P2PMessage(SingleBuffer.getUserInfo().getUserID(), sendToUserID, message);
                request.addData("msg", p2pMessage);
                messageShowArea1.append(p2pMessage.toString());//聊天记录中添加该条消息
                SingleBuffer.getP2pMessageHistory().get(sendToUserID).append(p2pMessage);
            } else{
                JOptionPane.showMessageDialog(ClientFrame.this,"对方离线，无法发送。","无法发送", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        ClientSendRequest.sendNotForResponse(request);
    }
    
    //发送广播消息
    public void sendBoardMessage(String message) throws IOException{
        if (message.length() != 0) {
            Request request = new Request(RequestType.SEND_MESSAGE);
            BoardMessage boardMessage = new BoardMessage(SingleBuffer.getUserInfo().getUserID(), message);
            request.addData("msg", boardMessage);
            messageShowArea2.append(boardMessage.toString());//聊天记录中添加该条消息
            ClientSendRequest.sendNotForResponse(request);
        }
    }

    //发送群聊消息
    private void sendGroupMessage(String message) throws IOException {
        if (message.length() != 0) {
            Request request = new Request(RequestType.SEND_MESSAGE);
            GroupMessage groupMessage = new GroupMessage(SingleBuffer.getUserInfo().getUserID(), curGroupID, message);
            request.addData("msg", groupMessage);
            messageShowArea3.append(groupMessage.toString());//聊天记录中添加该条消息
            ClientSendRequest.sendNotForResponse(request);
            SingleBuffer.getGroupChatMessageHistory().get(curGroupID).append(groupMessage);

        }
    }
    
    //添加好友
    public void addFriend(String userID) throws InterruptedException, IOException, ClassNotFoundException {
        boolean isFriend = false;
        Iterator<UserInfo> iterator = SingleBuffer.getFriends().iterator(); //迭代器
        while (iterator.hasNext()){ //检查是否已是好友
            if (iterator.next().getUserID().equals(userID)){
                isFriend = true;
                break;
            }
        }
        if (userID.equals(SingleBuffer.getUserInfo().getUserID())){
            JOptionPane.showMessageDialog(ClientFrame.this,"您不能添加自己为好友。","添加错误", JOptionPane.INFORMATION_MESSAGE);
        } else if (isFriend){
            JOptionPane.showMessageDialog(ClientFrame.this,"您和" + userID + "已是好友，请勿重复添加。","已是好友", JOptionPane.INFORMATION_MESSAGE);
        } else{
            Request request = new Request(RequestType.ADD_FRIEND);
            request.addData("userID", userID);
            request.addData("fromUserID", SingleBuffer.getUserInfo().getUserID());
            ClientSendRequest.sendNotForResponse(request);
        }
    }

    //添加群聊
    public void addGroup(String groupID) throws IOException {
        if (groupID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    ClientFrame.this,
                    "群组ID不能为空。",
                    "添加错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if the group ID already exists in the local cache
        boolean isGroupExisting = isAtGroup(groupID);

        if (isGroupExisting) {
            JOptionPane.showMessageDialog(
                    ClientFrame.this,
                    "您已在该群组中。",
                    "添加错误",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            // Create and send an 'add group' request to the server
            Request request = new Request(RequestType.ADD_GROUP);
            request.addData("groupID", groupID);
            request.addData("userID", SingleBuffer.getUserInfo().getUserID());
            ClientSendRequest.sendNotForResponse(request);
        }
    }

    private boolean isAtGroup(String groupID) {
        ArrayList<GroupInfo> groupChats = SingleBuffer.getGroupChats();
        if (groupChats != null) {
            for (GroupInfo group : groupChats) {
                if(group.getGroupId().equals(groupID)){
                    for(UserInfo userInfo:group.getMembers()){
                        if(userInfo.getUserID().equals(SingleBuffer.getUserInfo().getUserID())){
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    public void setCurSendToUserState(String sendToUserState) {
        this.sendToUserState = sendToUserState;
    }
    public DefaultTableModel getFriendsTableModel() {
        return friendsTableModel;
    }

    public DefaultTableModel getGroupChatsTableModel() {
        return groupChatsTableModel;
    }

    public String getCurSendToUserID() {
        return sendToUserID;
    }
    public String getCurSendToUserState() {
        return sendToUserState;
    }
    public JTextField getSendTargetField1() {
        return sendTargetField1;
    }
    public String getCurSendToUserNickname() {
        return sendToUserNickname;
    }
    public JTextArea getMessageShowArea1() {
        return messageShowArea1;
    }
    public JTextArea getMessageShowArea2() {
        return messageShowArea2;
    }
}

class MyTabbedPaneUI extends BasicTabbedPaneUI {
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
    	//自定义选项卡的高：比默认的高度 高10
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 10;
    }
    /**
     * 自定义选项卡的背景色
     * @param g 图形设置
     * @param tabPlacement 标签位置
     * @param tabIndex 标签下标
     * @param x x轴
     * @param y y轴
     * @param w 宽
     * @param h 高
     * @param isSelected 是否被选中
     */
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Color defaultColor = Color.white;
        Color selectedColor = new Color(132, 99, 201);
        //设置选中时和未被选中时的颜色
        g.setColor(!isSelected ? defaultColor : selectedColor);
        //填充图形，即选项卡为矩形
        g.fillRect(x, y, w, h);
    }

    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected) {  //自定义选项卡的边框色
        Color defaultColor = Color.white;
        Color selectedColor = new Color(132, 99, 201);
        g.setColor(!isSelected ? defaultColor : selectedColor); //设置选中时和未被选中时的颜色
    }

    protected void paintFocusIndicator(Graphics g,
                                       int tabPlacement, Rectangle[] rects,
                                       int tabIndex, Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {  //这个方法定义如果没有的话，选项卡在选中时，内测会有虚线。
    }

    protected LayoutManager createLayoutManager() { // 设置Layout
        return new TabbedPaneLayout();
    }

    public class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
    	// 要想实现：1.选中选项卡时，选项卡突出显示 2.选项卡之间有间距。那么必须重写以下方法！！
        protected void calculateTabRects(int tabPlacement, int tabCount) {
            super.calculateTabRects(tabPlacement, tabCount);
            setRec(70);  // 设置间距
        }

        public void setRec(int rec) {
            rects[0].y = rects[0].y + 100; //首个选项卡向下偏移100
            for (int i = 1; i < rects.length; i++) 
                rects[i].y = rects[i - 1].y + rec;//相邻选项卡的间隔为60 
        }
    }
}