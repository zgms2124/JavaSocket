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
    private DefaultTableModel friendsTableModel;  //���ģ�ͣ����ԶԱ�������ɾ��
    private DefaultTableModel groupChatsTableModel;
    private JTextField nicknameField;
    private JTextField sexField;
    private JTextField sendTargetField1;  //˽������
    private JTextArea messageShowArea1;
    private JTextArea inputMessageArea1;
    private JTextField sendTargetField2;
    private JTextArea messageShowArea2;
    private JTextArea inputMessageArea2;
    private String sendToUserID;
    private String sendToUserNickname;
    private String sendToUserState;
    private JTextField sendTargetField3; // Ⱥ��������ʾ��ǰ���ڵ�Ⱥ������
    private JTextArea inputMessageArea3; // Ⱥ����Ϣ��������
    private String curGroupID; // ��ǰѡ�е�Ⱥ��ID
    private JTextArea messageShowArea3; // ��ʾȺ�������¼���ı�����

    // Getter��Setter����
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
        this.setTitle("������");
        this.setSize(900, 600);
        //����Ĭ�ϴ�������Ļ����
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation((x - this.getWidth()) / 2, (y-this.getHeight()) / 2);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.BLACK); //��ȡ�������

        //˽�����
        JPanel p2pPanel = new JPanel();
        p2pPanel.setLayout(null);
        
        //�����б�
        JTable friendsTable = new JTable();  // �����µ� JTable(���) ʵ�����˱��������ʾ�����б�
        friendsTable.setRowHeight(30); // ���ñ���ÿһ�еĸ߶�Ϊ 30
        friendsTable.setEnabled(false); // ���ñ�񲻿ɱ���д���ݣ���������ݲ��ɱ༭
        DefaultTableCellRenderer r = new DefaultTableCellRenderer(); // ����Ĭ�ϵı��Ԫ����Ⱦ�������Զ��嵥Ԫ����ʾ��ʽ
        r.setHorizontalAlignment(JLabel.CENTER); // ������Ⱦ��ˮƽ���ж��룬����������ݻ��ڵ�Ԫ���о�����ʾ
        friendsTable.setDefaultRenderer(Object.class, r); // Ϊ����е��������������Ĭ�ϵ���Ⱦ��
       // ������������ģ�Ͷ����ʵ�����������ö����������ͱ������
        friendsTableModel = new DefaultTableModel();
        String[] friendsTableColumnNames = {"�ǳ�", "�˺�", "״̬"}; // ������������ͷ��
        Object[][] friendsTableData = {}; // �������ݣ���ʼ��Ϊ��
        friendsTableModel.setDataVector(friendsTableData, friendsTableColumnNames); // ����ͷ�����ݵ����ģ��
        friendsTable.setModel(friendsTableModel); // �ѱ��ģ�����ݰ󶨵�����������ͻ���ʾ��ģ���е�����
        JScrollPane friendsTableScroll = new JScrollPane(friendsTable); // ����һ��������������壬���ҽ�֮ǰ�����ı����뵽��������
        friendsTableScroll.setBounds(0, 0, 320, 600);  // �������ı߽��С������������λ�úͿ��
        friendsTableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // ����һֱ��ʾ��ֱ������
        
        //Ϊ�����б�������Ҽ������Խ��з�����Ϣ�Ĳ���
        friendsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {  //��������Ҽ����
                    final int row = friendsTable.rowAtPoint(e.getPoint()); //���ж�λ������һ��
                    if(row != -1){
                        final JPopupMenu popUp = new JPopupMenu();  //�����˵�
                        JMenuItem sendMessage = new JMenuItem("������Ϣ");  //�����˵������ݻ���ѡ��
                        sendMessage.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                sendToUserNickname = (String) friendsTableModel.getValueAt(row, 0);  //��λ���л�ȡ�ǳ�
                                sendToUserID = (String) friendsTableModel.getValueAt(row, 1);
                                sendToUserState = (String) friendsTableModel.getValueAt(row, 2);
                                sendTargetField1.setText("����:" + sendToUserNickname + " " + sendToUserState);
                                //����˽�������¼
                                messageShowArea1.setText(SingleBuffer.getP2pMessageHistory().get(sendToUserID).toString());
                            }
                        });
                        popUp.add(sendMessage);
                        popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });


        //Ⱥ�����
        JPanel GroupPanel = new JPanel();
        GroupPanel.setLayout(null);


        // Ⱥ���б�
        JTable groupChatsTable = new JTable();
        groupChatsTable.setRowHeight(30);
        groupChatsTable.setEnabled(false);
        DefaultTableCellRenderer r2 = new DefaultTableCellRenderer();
        r2.setHorizontalAlignment(JLabel.CENTER);
        groupChatsTable.setDefaultRenderer(Object.class, r2);

        // ������������ģ�Ͷ����ʵ��
        groupChatsTableModel = new DefaultTableModel();
        String[] groupChatsTableColumnNames = {"Ⱥ��ID", "Ⱥ����"};
        Object[][] groupChatsTableData = {};
        groupChatsTableModel.setDataVector(groupChatsTableData, groupChatsTableColumnNames);
        groupChatsTable.setModel(groupChatsTableModel);

        JScrollPane groupChatsTableScroll = new JScrollPane(groupChatsTable);
        groupChatsTableScroll.setBounds(0, 0, 320, 600);
        groupChatsTableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // ΪȺ���б�������Ҽ���������ת����Ӧ��Ⱥ��
        groupChatsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {  // ��������Ҽ����
                    final int row = groupChatsTable.rowAtPoint(e.getPoint()); // ���ж�λ������һ��
                    if(row != -1){
                        final JPopupMenu popUp = new JPopupMenu();  // �����˵�
                        JMenuItem enterGroupChat = new JMenuItem("����Ⱥ��");  // �����˵������ݻ���ѡ��
                        enterGroupChat.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                curGroupID = (String) groupChatsTableModel.getValueAt(row, 0); // ��λ���л�ȡȺ��ID
                                String groupName = (String) groupChatsTableModel.getValueAt(row, 1); // ��ȡȺ������
                                sendTargetField3.setText("Ⱥ�ģ�" + groupName);
                                // ����Ⱥ�������¼���������Ҫʵ�� getGroupChatMessageHistory �������
                                messageShowArea3.setText(SingleBuffer.getGroupChatMessageHistory().get(curGroupID).toString());
                            }
                        });
                        popUp.add(enterGroupChat);
                        popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        
        //˽��������
        JPanel p2pChatPanel = new JPanel();
        p2pChatPanel.setBounds(320, 0, 550, 600);
        JPanel titlePanel1 = new JPanel();
        titlePanel1.setPreferredSize(new Dimension(550, 40)); //������õ�����
        titlePanel1.setBackground(Color.white);
        JPanel contentPanel1 = new JPanel();
        contentPanel1.setPreferredSize(new Dimension(550, 350));
        JPanel inputPanel1 = new JPanel();
        inputPanel1.setPreferredSize(new Dimension(550, 200));
        inputPanel1.setBackground(Color.white);
        p2pChatPanel.add(titlePanel1, BorderLayout.NORTH);
        p2pChatPanel.add(contentPanel1, BorderLayout.CENTER);
        p2pChatPanel.add(inputPanel1, BorderLayout.SOUTH);
        
        //����Ŀ��
        sendTargetField1 = new JTextField();
        sendTargetField1.setEditable(false);
        sendTargetField1.setHorizontalAlignment(JTextField.CENTER);  //�ı����ж���
        sendTargetField1.setFont(new Font("����", Font.BOLD, 15));
        sendTargetField1.setPreferredSize(new Dimension(170, 30));
        titlePanel1.add(sendTargetField1);
        
        //��Ϣ��ʾ��
        messageShowArea1 = new JTextArea();
        messageShowArea1.setFont(new Font("����", Font.BOLD, 15));
        messageShowArea1.setPreferredSize(new Dimension(550, 450));
        messageShowArea1.setEditable(false);
        messageShowArea1.setLineWrap(true);    //�Զ�����
        messageShowArea1.setWrapStyleWord(true);  //���в�����
        contentPanel1.add(messageShowArea1);
        
        //����������
        inputMessageArea1 = new JTextArea();
        inputMessageArea1.setPreferredSize(new Dimension(550, 100));
        inputMessageArea1.setFont(new Font("����", Font.BOLD, 15));
        inputMessageArea1.setLineWrap(true);        //�Զ�����
        inputMessageArea1.setWrapStyleWord(true);    //���в�����
        
        //���Ͱ�ť
        JButton sendBtn1 = new JButton("����");
        sendBtn1.setPreferredSize(new Dimension(80, 30));
        sendBtn1.setFocusPainted(false);//ȥ����
        sendBtn1.setBackground(new Color(27, 127, 176));
        sendBtn1.setFont(new Font("����", Font.BOLD, 16));
        sendBtn1.setForeground(Color.white);//����������ɫ
        inputPanel1.add(inputMessageArea1, BorderLayout.NORTH);
        inputPanel1.add(sendBtn1, BorderLayout.CENTER);
        
        p2pPanel.add(friendsTableScroll);
        p2pPanel.add(p2pChatPanel);

        //�㲥����
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
        
        //����Ŀ��
        sendTargetField2 = new JTextField();
        sendTargetField2.setEditable(false);
        sendTargetField2.setHorizontalAlignment(JTextField.CENTER);
        sendTargetField2.setFont(new Font("����", Font.BOLD, 15));
        sendTargetField2.setPreferredSize(new Dimension(200, 30));
        sendTargetField2.setText("�㲥");
        titlePanel2.add(sendTargetField2);
        
        //��Ϣ��ʾ��
        messageShowArea2 = new JTextArea();
        messageShowArea2.setPreferredSize(new Dimension(820, 350));
        messageShowArea2.setEditable(false);
        messageShowArea2.setFont(new Font("����", Font.BOLD, 15));
        messageShowArea2.setLineWrap(true);        //�Զ�����
        messageShowArea2.setWrapStyleWord(true);    //���в�����
        contentPanel2.add(messageShowArea2);
        
        //����������
        inputMessageArea2 = new JTextArea();
        inputMessageArea2.setPreferredSize(new Dimension(800, 100));
        inputMessageArea2.setFont(new Font("����", Font.BOLD, 15));
        inputMessageArea2.setLineWrap(true);        //�Զ�����
        inputMessageArea2.setWrapStyleWord(true);    // ���в�����
        
        //���Ͱ�ť
        JButton sendBtn2 = new JButton("����");
        sendBtn2.setPreferredSize(new Dimension(100, 30));
        sendBtn2.setFocusPainted(false);//ȥ����
        sendBtn2.setBackground(new Color(27, 127, 176));
        sendBtn2.setFont(new Font("����", Font.BOLD, 16));
        sendBtn2.setForeground(Color.white);
        inputPanel2.add(inputMessageArea2, BorderLayout.NORTH);
        inputPanel2.add(sendBtn2, BorderLayout.CENTER);


        // Ⱥ��������


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

        //����Ŀ��
        sendTargetField3 = new JTextField();
        sendTargetField3.setEditable(false);
        sendTargetField3.setHorizontalAlignment(JTextField.CENTER);  //�ı����ж���
        sendTargetField3.setFont(new Font("����", Font.BOLD, 15));
        sendTargetField3.setPreferredSize(new Dimension(170, 30));
        titlePanel3.add(sendTargetField3);

        //��Ϣ��ʾ��
        messageShowArea3 = new JTextArea();
        messageShowArea3.setPreferredSize(new Dimension(550, 450));
        messageShowArea3.setFont(new Font("����", Font.BOLD, 15));
        messageShowArea3.setEditable(false);
        messageShowArea3.setLineWrap(true);
        messageShowArea3.setWrapStyleWord(true);
        contentPanel3.add(messageShowArea3);

        //����������
        inputMessageArea3 = new JTextArea();
        inputMessageArea3.setPreferredSize(new Dimension(550, 100));
        inputMessageArea3.setFont(new Font("����", Font.BOLD, 15));
        inputMessageArea3.setLineWrap(true);
        inputMessageArea3.setWrapStyleWord(true);

        //���Ͱ�ť
        JButton sendBtn3 = new JButton("����");
        sendBtn3.setPreferredSize(new Dimension(80, 30));
        sendBtn3.setFocusPainted(false);
        sendBtn3.setBackground(new Color(27, 127, 176));
        sendBtn3.setFont(new Font("����", Font.BOLD, 16));
        sendBtn3.setForeground(Color.white);
        inputPanel3.add(inputMessageArea3, BorderLayout.NORTH);
        inputPanel3.add(sendBtn3, BorderLayout.CENTER);

        GroupPanel.add(groupChatsTableScroll);
        GroupPanel.add(groupChatPanel);


        //������Ϣ
        JPanel selfInfoPanel = new JPanel();
        selfInfoPanel.setLayout(new GridLayout(12,1,10,10)); //6��1�У�ˮƽ���10����ֱ���5
        JLabel nicknameLabel = new JLabel("�ǳƣ�");
        JLabel sexLabel = new JLabel("�Ա�");
        nicknameLabel.setFont(new Font("����", Font.BOLD, 16));
        sexLabel.setFont(new Font("����", Font.BOLD, 16));
        nicknameField = new JTextField();
        sexField = new JTextField();

        //�������ݾ���
        nicknameField.setHorizontalAlignment(JTextField.CENTER);
        sexField.setHorizontalAlignment(JTextField.CENTER);
        
        //����������ʽ
        nicknameField.setFont(new Font("����", Font.BOLD, 16));
        sexField.setFont(new Font("����", Font.BOLD, 16));
        
        //���ò��ɱ༭
        nicknameField.setEditable(false);
        sexField.setEditable(false);
        
        //�����������񲼾�
        selfInfoPanel.add(nicknameLabel);
        selfInfoPanel.add(nicknameField);
        selfInfoPanel.add(sexLabel);
        selfInfoPanel.add(sexField);

        //��Ӻ���
        JPanel addPanel = new JPanel();
        addPanel.setLayout(null);
        JLabel addFriendIDLabel = new JLabel("������Ҫ��ӵĺ����˺ţ�");
        addFriendIDLabel.setFont(new Font("����", Font.BOLD, 16));//����������ʽ
        addFriendIDLabel.setBounds(300,50,250, 30);
        addPanel.add(addFriendIDLabel);
        JTextField addFriendIDField = new JTextField();
        addFriendIDField.setBounds(275, 80, 250, 40);
        addFriendIDField.setHorizontalAlignment(JTextField.CENTER);
        
        //����������ʽ
        addFriendIDField.setFont(new Font("����", Font.BOLD, 16));
        addPanel.add(addFriendIDField);
        JButton addBtn = new JButton("���");
        addBtn.setBounds(350,150,100, 30);
        addBtn.setFocusPainted(false);//ȥ����
        addBtn.setBackground(new Color(27, 127, 176));
        addBtn.setFont(new Font("����", Font.BOLD, 16));
        addBtn.setForeground(Color.white);
        addPanel.add(addBtn);

        // ���Ⱥ��
        JPanel addGroupPanel = new JPanel();
        addGroupPanel.setLayout(null);
        JLabel addGroupIDLabel = new JLabel("������Ҫ��ӵ�Ⱥ��ID��");
        addGroupIDLabel.setFont(new Font("����", Font.BOLD, 16)); // ����������ʽ
        addGroupIDLabel.setBounds(300, 50, 250, 30);
        addGroupPanel.add(addGroupIDLabel);
        JTextField addGroupIDField = new JTextField();
        addGroupIDField.setBounds(275, 80, 250, 40);
        addGroupIDField.setHorizontalAlignment(JTextField.CENTER);


       // ����������ʽ
        addGroupIDField.setFont(new Font("����", Font.BOLD, 16));
        addGroupPanel.add(addGroupIDField);
        JButton addGroupBtn = new JButton("���");
        addGroupBtn.setBounds(350, 150, 100, 30);
        addGroupBtn.setFocusPainted(false); // ȥ����
        addGroupBtn.setBackground(new Color(27, 127, 176));
        addGroupBtn.setFont(new Font("����", Font.BOLD, 16));
        addGroupBtn.setForeground(Color.white);
        addGroupPanel.add(addGroupBtn);

        // ����һ���µ������������Ӻ����������Ⱥ�����
        JPanel addPanelContainer = new JPanel();
        addPanelContainer.setLayout(new BoxLayout(addPanelContainer, BoxLayout.Y_AXIS));
       // ����Ӻ����������Ⱥ�����ֱ���ӵ��µ������
        addPanelContainer.add(addPanel);
        addPanelContainer.add(addGroupPanel);


        // ����ѡ����
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(900, 600));
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        Image friendsIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\static\\user.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image groupIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\static\\megaphone.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image selfInfoIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\static\\home.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image addIcon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\static\\add.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        Image group2Icon = new ImageIcon("E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\static\\users-alt.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        // �������ѽ���ѡ�
        tabbedPane.addTab(null, new ImageIcon(friendsIcon), p2pPanel, "�鿴����");
        // �����㲥����ѡ�
        tabbedPane.addTab(null, new ImageIcon(groupIcon), groupPanel, "�㲥");
        // ����Ⱥ�Ľ���ѡ�
        tabbedPane.addTab(null, new ImageIcon(group2Icon), GroupPanel, "Ⱥ��");
        // ������ӽ���ѡ�
        tabbedPane.addTab(null, new ImageIcon(addIcon), addPanelContainer, "���");
        // ����������Ϣ����ѡ�
        tabbedPane.addTab(null, new ImageIcon(selfInfoIcon), selfInfoPanel, "�鿴������Ϣ");
        tabbedPane.setUI(new MyTabbedPaneUI()); //����ѡ���UI
        // ����Ĭ��ѡ�е�ѡ�
        tabbedPane.setSelectedIndex(0);
        this.add(tabbedPane);

        //���������߳�
        new ListeningThread(this).start();
        //˽�ķ��Ͱ�ť���¼�
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
        
        //�㲥���Ͱ�ť���¼�
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

        //Ⱥ�ķ��Ͱ�ť���¼�
        sendBtn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // sendToGroupChat����������Ⱥ����Ϣ�ķ���������Ҫʵ���������
                    sendGroupMessage(inputMessageArea3.getText());
                    // �����������
                    inputMessageArea3.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        
        //��Ӻ��Ѱ�ť���¼�
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
        //���Ⱥ�İ�ť���¼�
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
        
        //�˳��ͻ���
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫ�˳���", "�˳�ȷ��", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    ClientFrame.this.dispose();
                    Request request = new Request(RequestType.LOG_OUT);
                    request.addData("userID", SingleBuffer.getUserInfo().getUserID());
                    try {
                        ClientSendRequest.sendNotForResponse(request);//������������˳�����
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        loadInitData();//������ѡ�Ⱥ�顢������Ϣ��

        this.setVisible(true);
    }
    
    //���ر��ػ�������
    private void loadInitData(){
        nicknameField.setText(SingleBuffer.getUserInfo().getNickName());
        sexField.setText(SingleBuffer.getUserInfo().getSex().toString());
        //�������к���
        if (SingleBuffer.getFriends() != null) {
            for (UserInfo friend : SingleBuffer.getFriends()) {
                friendsTableModel.addRow(new String[]{friend.getNickName(), friend.getUserID(), "����"});
            }
        }
        //���ú�������״̬
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
                friendsTableModel.insertRow(0, new String[]{onlineFriend.getNickName(), onlineFriend.getUserID(), "����"});
            }
        }
        //���غ��������¼
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
    
    //����˽����Ϣ
    public void sendP2pMessage(String message) throws IOException {
        Request request = new Request(RequestType.SEND_MESSAGE);
        if (sendToUserID != null){ //˽����Ϣ
            if (sendToUserState.equals("����")) {
                P2PMessage p2pMessage = new P2PMessage(SingleBuffer.getUserInfo().getUserID(), sendToUserID, message);
                request.addData("msg", p2pMessage);
                messageShowArea1.append(p2pMessage.toString());//�����¼����Ӹ�����Ϣ
                SingleBuffer.getP2pMessageHistory().get(sendToUserID).append(p2pMessage);
            } else{
                JOptionPane.showMessageDialog(ClientFrame.this,"�Է����ߣ��޷����͡�","�޷�����", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        ClientSendRequest.sendNotForResponse(request);
    }
    
    //���͹㲥��Ϣ
    public void sendBoardMessage(String message) throws IOException{
        if (message.length() != 0) {
            Request request = new Request(RequestType.SEND_MESSAGE);
            BoardMessage boardMessage = new BoardMessage(SingleBuffer.getUserInfo().getUserID(), message);
            request.addData("msg", boardMessage);
            messageShowArea2.append(boardMessage.toString());//�����¼����Ӹ�����Ϣ
            ClientSendRequest.sendNotForResponse(request);
        }
    }

    //����Ⱥ����Ϣ
    private void sendGroupMessage(String message) throws IOException {
        if (message.length() != 0) {
            Request request = new Request(RequestType.SEND_MESSAGE);
            GroupMessage groupMessage = new GroupMessage(SingleBuffer.getUserInfo().getUserID(), curGroupID, message);
            request.addData("msg", groupMessage);
            messageShowArea3.append(groupMessage.toString());//�����¼����Ӹ�����Ϣ
            ClientSendRequest.sendNotForResponse(request);
            SingleBuffer.getGroupChatMessageHistory().get(curGroupID).append(groupMessage);

        }
    }
    
    //��Ӻ���
    public void addFriend(String userID) throws InterruptedException, IOException, ClassNotFoundException {
        boolean isFriend = false;
        Iterator<UserInfo> iterator = SingleBuffer.getFriends().iterator(); //������
        while (iterator.hasNext()){ //����Ƿ����Ǻ���
            if (iterator.next().getUserID().equals(userID)){
                isFriend = true;
                break;
            }
        }
        if (userID.equals(SingleBuffer.getUserInfo().getUserID())){
            JOptionPane.showMessageDialog(ClientFrame.this,"����������Լ�Ϊ���ѡ�","��Ӵ���", JOptionPane.INFORMATION_MESSAGE);
        } else if (isFriend){
            JOptionPane.showMessageDialog(ClientFrame.this,"����" + userID + "���Ǻ��ѣ������ظ���ӡ�","���Ǻ���", JOptionPane.INFORMATION_MESSAGE);
        } else{
            Request request = new Request(RequestType.ADD_FRIEND);
            request.addData("userID", userID);
            request.addData("fromUserID", SingleBuffer.getUserInfo().getUserID());
            ClientSendRequest.sendNotForResponse(request);
        }
    }

    //���Ⱥ��
    public void addGroup(String groupID) throws IOException {
        if (groupID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    ClientFrame.this,
                    "Ⱥ��ID����Ϊ�ա�",
                    "��Ӵ���",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if the group ID already exists in the local cache
        boolean isGroupExisting = isAtGroup(groupID);

        if (isGroupExisting) {
            JOptionPane.showMessageDialog(
                    ClientFrame.this,
                    "�����ڸ�Ⱥ���С�",
                    "��Ӵ���",
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
    	//�Զ���ѡ��ĸߣ���Ĭ�ϵĸ߶� ��10
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 10;
    }
    /**
     * �Զ���ѡ��ı���ɫ
     * @param g ͼ������
     * @param tabPlacement ��ǩλ��
     * @param tabIndex ��ǩ�±�
     * @param x x��
     * @param y y��
     * @param w ��
     * @param h ��
     * @param isSelected �Ƿ�ѡ��
     */
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Color defaultColor = Color.white;
        Color selectedColor = new Color(132, 99, 201);
        //����ѡ��ʱ��δ��ѡ��ʱ����ɫ
        g.setColor(!isSelected ? defaultColor : selectedColor);
        //���ͼ�Σ���ѡ�Ϊ����
        g.fillRect(x, y, w, h);
    }

    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected) {  //�Զ���ѡ��ı߿�ɫ
        Color defaultColor = Color.white;
        Color selectedColor = new Color(132, 99, 201);
        g.setColor(!isSelected ? defaultColor : selectedColor); //����ѡ��ʱ��δ��ѡ��ʱ����ɫ
    }

    protected void paintFocusIndicator(Graphics g,
                                       int tabPlacement, Rectangle[] rects,
                                       int tabIndex, Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {  //��������������û�еĻ���ѡ���ѡ��ʱ���ڲ�������ߡ�
    }

    protected LayoutManager createLayoutManager() { // ����Layout
        return new TabbedPaneLayout();
    }

    public class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
    	// Ҫ��ʵ�֣�1.ѡ��ѡ�ʱ��ѡ�ͻ����ʾ 2.ѡ�֮���м�ࡣ��ô������д���·�������
        protected void calculateTabRects(int tabPlacement, int tabCount) {
            super.calculateTabRects(tabPlacement, tabCount);
            setRec(70);  // ���ü��
        }

        public void setRec(int rec) {
            rects[0].y = rects[0].y + 100; //�׸�ѡ�����ƫ��100
            for (int i = 1; i < rects.length; i++) 
                rects[i].y = rects[i - 1].y + rec;//����ѡ��ļ��Ϊ60 
        }
    }
}