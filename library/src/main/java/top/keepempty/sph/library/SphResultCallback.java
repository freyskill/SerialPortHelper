package top.keepempty.sph.library;

/**
 * 串口数据回调
 * @author ：frey
 * @date：2019/3/30 18:25
 */
public interface SphResultCallback {

    /**
     * 发送命令
     * @param sendCom 串口发送的命令
     */
    void onSendData(SphCmdEntity sendCom);

    /**
     * 收到的数据
     * @param data 串口收到的数据
     */
    void onReceiveData(SphCmdEntity data);

    /**
     * 发送，收取完成
     */
    void onComplete();

}
