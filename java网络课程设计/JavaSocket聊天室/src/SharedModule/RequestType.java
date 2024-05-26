package SharedModule;

import java.io.Serializable;

public enum RequestType implements Serializable {
    //��¼
    SIGN_IN,
    //ע��
    SIGN_UP,
    //����Ϣ
    SEND_MESSAGE,
    //����
    LOG_OUT,
    ADD_GROUP, //��Ӻ���
    ADD_FRIEND
}
