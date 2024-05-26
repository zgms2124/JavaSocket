package Client.Frame;

import Client.Service.ClientSendRequest;
import Client.Service.SingleBuffer;
import SharedModule.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends JFrame {
	public Login() {
		this.setTitle("�����ҵ�¼");
		this.setSize(550, 420);
		
		//����Ĭ�ϴ�������Ļ����
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation((x - this.getWidth()) / 2, (y-this.getHeight())/ 2);
        this.setResizable(false);  //�����ϴ���С

        //��ӱ���ͼƬ
        ImageIcon image = new ImageIcon("E:\\java\\projectpractice\\experiment\\java����γ����\\JavaSocket������\\static\\login.png");
        Image img = image.getImage();
        img = img.getScaledInstance(550, 330, Image.SCALE_DEFAULT); //������ͼ������Ű汾,����Ӧ��С��Ĭ��ͼ�������㷨
        image.setImage(img);
        JLabel imgLabel = new JLabel(image);
        imgLabel.setPreferredSize(new Dimension(550,150));
        this.add(imgLabel, BorderLayout.NORTH);

        //��¼��Ϣ���
        JPanel mainPanel = new JPanel();
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED); //ʴ�̱߿�
        mainPanel.setBorder(BorderFactory.createTitledBorder(border, "�����¼��Ϣ", TitledBorder.CENTER,TitledBorder.TOP));  //���������Ϸ����м�
        ((TitledBorder) mainPanel.getBorder()).setTitleFont(new Font("����", Font.BOLD, 16)); //���ñ��������
        this.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(null);
        
        //�˺�������
        JLabel nameLabel = new JLabel("�˻�:");
        nameLabel.setBounds(90, 40, 50, 40);
        nameLabel.setFont(new Font("����", Font.BOLD, 16));
        mainPanel.add(nameLabel);
        JTextField userIdField = new JTextField();
        userIdField.setBounds(137, 40, 275, 35);
        userIdField.setFont(new Font("����", Font.BOLD, 16));
        userIdField.requestFocusInWindow(); //�û�����ý���
        mainPanel.add(userIdField);
        
        //����������
        JLabel pwdLabel = new JLabel("����:");
        pwdLabel.setBounds(90, 100, 50, 40);
        pwdLabel.setFont(new Font("����", Font.BOLD, 16));
        mainPanel.add(pwdLabel);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(137, 100, 275, 35);
        mainPanel.add(passwordField);
        nameLabel.setPreferredSize(new Dimension(550,200));

        //��ť���
        JPanel btnPanel = new JPanel();
        btnPanel.setPreferredSize(new Dimension(550,50));
        btnPanel.setLayout(null);  //�����Զ���ղ���
        this.add(btnPanel, BorderLayout.SOUTH);
        btnPanel.setBorder(new EmptyBorder(2, 8, 4, 8));
        
        //ע���˺Ű�ť
        JButton registerBtn = new JButton("ע���˺�");
        registerBtn.setBounds(0, 20, 100, 30);
        registerBtn.setFocusPainted(false);  //ȥ����
        registerBtn.setForeground(Color.gray); //����������ɫ
        registerBtn.setContentAreaFilled(false); //��ȥĬ�ϱ���
        
        //��¼��ť
        JButton submitBtn = new JButton("��¼");
        submitBtn.setBounds(205, 5, 140, 40);
        submitBtn.setFocusPainted(false);
        submitBtn.setBackground(new Color(27, 127, 176));
        submitBtn.setFont(new Font("����", Font.BOLD, 16));
        submitBtn.setForeground(Color.white);
        
        //��Ӱ�ť�����
        btnPanel.add(registerBtn);
        btnPanel.add(submitBtn);

        //ע�ᰴť�����¼�
        registerBtn.addActionListener(e -> new Register());  //��ע�����

        //��¼��ť�����¼�
        submitBtn.addActionListener(e -> this.login(userIdField, passwordField));
        this.setVisible(true); //�������ɼ�
	}
	
	private void login(JTextField userID, JPasswordField password){
        //����Ϊ��
        if (userID.getText().length() == 0 || password.getPassword().length == 0){
            JOptionPane.showMessageDialog(Login.this,"�˺Ż����벻��Ϊ�գ�","��������",JOptionPane.ERROR_MESSAGE);
            userID.requestFocusInWindow();  //��ý���
            return ;
        }
        //�˺Ÿ�ʽ����
        if (!userID.getText().matches("\\d+")){  //������ʽƥ��
            JOptionPane.showMessageDialog(Login.this,"�˺ű���ȫ�����֣�","��������",JOptionPane.ERROR_MESSAGE);
            userID.setText("");  //����
            userID.requestFocusInWindow();
            return ;
        }
        
        //�������̴߳����¼
        new Thread(() -> {
            Request request = new Request(RequestType.SIGN_IN);
            request.addData("userID", userID.getText());
            request.addData("password", new String(password.getPassword()));
            Response response = null;
            try {
                System.out.println("���ڷ���");
                response = ClientSendRequest.sendForResponse(request);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            System.out.println("�ɹ������������ͨ��");
            if (response == null) {
                JOptionPane.showMessageDialog(Login.this,"������δ���ӣ�Ŀǰ�޷���¼�����Ժ����ԣ�","�������", JOptionPane.ERROR_MESSAGE);
            } else if (response.getResponseStatus() == ResponseStatus.OK) {  
                if (response.getResponseType() == ResponseType.SUCCESS_SIGN_IN) {
                    JOptionPane.showMessageDialog(Login.this,"��ӭ������","��¼�ɹ�", JOptionPane.INFORMATION_MESSAGE);
                    SingleBuffer.setUserInfo((UserInfo) response.getDataByKey("userInfo"));  //���û���
                    SingleBuffer.setFriends((ArrayList<UserInfo>) response.getDataByKey("userFriends"));
                    SingleBuffer.setOnlineFriends((ArrayList<UserInfo>) response.getDataByKey("userOnlineFriends"));
                    ArrayList<GroupInfo> groups=new ArrayList<>();
                    Object res = response.getDataByKey("groups");

                    // Now you have a combined list of GroupInfo, set it in SingleBuffer
                    SingleBuffer.setGroupChats((ArrayList<GroupInfo>) response.getDataByKey("groups"));
                    this.dispose();//�رյ�¼ҳ��
                    ClientFrame mainFrame = new ClientFrame(); //��������ҳ��
                } else if (response.getResponseType() == ResponseType.WRONG_ID) {
                    JOptionPane.showMessageDialog(Login.this,"�˻������ڣ������˻��Ƿ���ȷ��","�˻�����", JOptionPane.ERROR_MESSAGE);
                } else if (response.getResponseType() == ResponseType.WRONG_PWD) {
                    JOptionPane.showMessageDialog(Login.this,"����������������Ƿ���ȷ��","�������", JOptionPane.ERROR_MESSAGE);
                } else if(response.getResponseType() == ResponseType.SECOND_LOGIN){
                    JOptionPane.showMessageDialog(Login.this,"���˺��Ѿ���¼�������ظ���¼��","�ظ���¼", JOptionPane.ERROR_MESSAGE);
                }
            } else {  //����Ӧ
                JOptionPane.showMessageDialog(Login.this,"������δ���ӣ�Ŀǰ�޷���¼�����Ժ�����","�������", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }
}
