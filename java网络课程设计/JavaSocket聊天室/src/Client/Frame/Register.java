package Client.Frame;

import Client.Service.ClientSendRequest;
import SharedModule.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Register extends JFrame {
	public Register() {
		this.setTitle("ע�����˺�");// ���ñ���
		this.setSize(530, 420);
		// ����Ĭ�ϴ�������Ļ����
		int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((x - this.getWidth()) / 2 + 50, (y - this.getHeight()) / 2 + 50);
		this.setResizable(false);

		// ��Ϣ��д���
		JPanel infoPanel = new JPanel();
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		infoPanel.setBorder(BorderFactory.createTitledBorder(border, "����ע����Ϣ", TitledBorder.CENTER, TitledBorder.TOP));
		((TitledBorder) infoPanel.getBorder()).setTitleFont(new Font("����", Font.BOLD, 16));
		this.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(null);

		// �ǳ�������
		JLabel nicknameLabel = new JLabel("�ǳ�:"); // label��ʾ
		nicknameLabel.setBounds(120, 70, 60, 30);
		nicknameLabel.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(nicknameLabel);
		JTextField nicknameField = new JTextField(); // �ǳ�
		nicknameField.setBounds(170, 70, 200, 30);
		nicknameField.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(nicknameField);

		// ����������
		JLabel pwdLabel = new JLabel("����:");
		pwdLabel.setBounds(120, 110, 60, 30);
		pwdLabel.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(pwdLabel);
		JPasswordField pwdField = new JPasswordField();// �����
		pwdField.setBounds(170, 110, 200, 30);
		infoPanel.add(pwdField);

		// ȷ������
		JLabel pwdConfirmLabel = new JLabel("ȷ������:");
		pwdConfirmLabel.setBounds(90, 150, 80, 30);
		pwdConfirmLabel.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(pwdConfirmLabel);
		JPasswordField pwdConfirmField = new JPasswordField();
		pwdConfirmField.setBounds(170, 150, 200, 30);
		infoPanel.add(pwdConfirmField);

		// �Ա�������
		JLabel sexLabel = new JLabel("�Ա�:");
		sexLabel.setBounds(120, 190, 60, 30);
		sexLabel.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(sexLabel);
		JRadioButton sexMale = new JRadioButton("��", true);  //Ĭ��
		sexMale.setFont(new Font("����", Font.BOLD, 16));
		sexMale.setBounds(180, 190, 60, 30);
		infoPanel.add(sexMale);
		JRadioButton sexFemale = new JRadioButton("Ů");
		sexFemale.setFont(new Font("����", Font.BOLD, 16));
		sexFemale.setBounds(240, 190, 60, 30);
		infoPanel.add(sexFemale);
		ButtonGroup buttonGroup = new ButtonGroup();// ��ѡ��ť��
		buttonGroup.add(sexMale);
		buttonGroup.add(sexFemale);

		// �ֻ���������
		JLabel phoneLabel = new JLabel("�ֻ���:"); // label��ʾ
		phoneLabel.setBounds(110, 230, 60, 30);
		phoneLabel.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(phoneLabel);
		JTextField phoneField = new JTextField(); // �ֻ���
		phoneField.setBounds(170, 230, 200, 30);
		phoneField.setFont(new Font("����", Font.BOLD, 16));
		infoPanel.add(phoneField);

		// ��ť���
		JPanel btnPanel = new JPanel();
		this.add(btnPanel, BorderLayout.SOUTH);
		btnPanel.setPreferredSize(new Dimension(550, 50));
		btnPanel.setLayout(null); // ����Ϊ�Զ���ղ���
		btnPanel.setBorder(new EmptyBorder(2, 8, 4, 8));

		// ȷ�ϰ�ť
		JButton confirmBtn = new JButton("ȷ ��");
		confirmBtn.setBounds(110, 10, 100, 30);
		btnPanel.add(confirmBtn);
		this.setBtnStyle(confirmBtn);// ȥ���㣬�豳����ɫ����������ɫ

		// ���ť
		JButton clearBtn = new JButton("�� ��");
		clearBtn.setBounds(330, 10, 100, 30);
		btnPanel.add(clearBtn);
		this.setBtnStyle(clearBtn); 

		// �رմ���
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Register.this.dispose();
			}
		});

		// ���ť�����¼���
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				nicknameField.setText("");
				pwdField.setText("");
				pwdConfirmField.setText("");
				phoneField.setText("");
				nicknameField.requestFocusInWindow();// �û�����ý���
			}
		});

		// ȷ�ϰ�ť�����¼���
		confirmBtn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// �жϸ��ֶ��Ƿ�Ϊ��
				if (nicknameField.getText().length() == 0 || pwdField.getPassword().length == 0
						|| pwdConfirmField.getPassword().length == 0 || phoneField.getText().length() == 0) {
					JOptionPane.showMessageDialog(Register.this, "�뽫��Ϣ��д����!");
				} else if (pwdField.getPassword().length <= 6) {
					JOptionPane.showMessageDialog(Register.this, "����̫�̣���������6λ!");
					pwdField.setText("");
					pwdConfirmField.setText("");
					pwdField.requestFocusInWindow();
				} else if (!new String(pwdField.getPassword()).equals(new String(pwdConfirmField.getPassword()))) {
					JOptionPane.showMessageDialog(Register.this, "�����������벻һ��!");
					pwdField.setText("");
					pwdConfirmField.setText("");
					pwdField.requestFocusInWindow();
				} else if (!Register.this.checkPhoneNumber(phoneField.getText())) {
					JOptionPane.showMessageDialog(Register.this, "��������ȷ���ֻ��ţ�");
					phoneField.setText("");
					phoneField.requestFocusInWindow();
				} else {
					UserInfo user = new UserInfo(nicknameField.getText(), new String(pwdField.getPassword()),
							sexMale.isSelected() ? Sex.M : Sex.F, phoneField.getText());
					try {
						Register.this.register(user);// һ��Ҫ��Register.this��ֻ��this.�������ActionListener���ʵ��
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			}
		});
		this.setVisible(true);
	}

	// ע��
	private void register(UserInfo user) {
		Request request = new Request(RequestType.SIGN_UP);
		request.addData("user", user);
		// ���̴߳���ע�ᣬԭ�̼߳��������¼�����
		new Thread(() -> {
			// ��ȡ��Ӧ����
			Response response = null;
			try {
				response = ClientSendRequest.sendForResponse(request);
			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				e.printStackTrace();
			}
			if (response.getResponseStatus() == ResponseStatus.OK) {// ������Ӧ
				if (response.getResponseType() == ResponseType.SUCCESS_SIGN_UP) {
					JOptionPane.showMessageDialog(Register.this,"���ѳɹ�ע�ᣬ�����ʺ�Ϊ:" + response.getDataByKey("userID") + "��", "ע��ɹ�",
							JOptionPane.INFORMATION_MESSAGE);
					this.dispose();
				} else if (response.getResponseType() == ResponseType.NICKNAME_EXIST) 
					JOptionPane.showMessageDialog(Register.this, "���ǳ��ѱ�ʹ��", "�ǳ��ظ�", JOptionPane.INFORMATION_MESSAGE);
			} else 
				JOptionPane.showMessageDialog(Register.this, "ע��ʧ�ܣ����Ժ����ԣ�", "�������", JOptionPane.ERROR_MESSAGE);
		}).start();
	}

	//���ð�ť��ʽ
    private void setBtnStyle(JButton button){
        button.setFocusPainted(false);//ȥ����
        button.setBackground(new Color(27, 127, 176));
        button.setFont(new Font("����", Font.BOLD, 16));
        button.setForeground(Color.white);//����������ɫ
    }
    
    //�ж��ֻ����Ƿ���ȷ
	private boolean checkPhoneNumber(String phoneNumber) {
		return phoneNumber.matches("^1(3\\d|4[4-9]|5[0-35-9]|6[67]|7[013-8]|8\\d|9\\d)\\d{8}$");
	}
}
