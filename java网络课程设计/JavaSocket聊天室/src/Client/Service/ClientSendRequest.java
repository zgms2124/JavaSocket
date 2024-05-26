/*�ͻ�����������˷������ݡ�*/

package Client.Service;

import SharedModule.Request;
import SharedModule.Response;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientSendRequest {
	// ���¼��ע�ᡢ��Ӻ���
    public static Response sendForResponse(Request request) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("----------------------");
        //�õ������
        ObjectOutputStream oos = SingleBuffer.getOos();
        oos.writeObject(request);//�������
        oos.flush();
        System.out.println("�����ķ��ͽ���");
        //�õ�������
        ObjectInputStream ois = SingleBuffer.getOis();
        Response response = (Response) ois.readObject();
        System.out.println("��ȡ��������Ӧ����Ӧ��ʽΪ��" + response.getResponseType());
        System.out.println("----------------------");
        return response;
    }
    
    // ��˽�ġ�Ⱥ��
    public static void sendNotForResponse(Request request) throws IOException {
        System.out.println("----------------------");
        //�õ������
        ObjectOutputStream oos = SingleBuffer.getOos();
        oos.writeObject(request);//
        oos.flush();
        System.out.println("�����ķ��ͽ���");
    }
}
