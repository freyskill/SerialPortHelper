package top.keepempty.sph.library;

/**
 * 串口命令
 * @author：fery
 * @date：2019/3/30 18:35
 */
public class SphCmdEntity {

    public SphCmdEntity() {
    }

    public SphCmdEntity(byte[] commands) {
        this.commands = commands;
        this.commandsHex = DataConversion.encodeHexString(commands);
    }

    /**
     * 串口发送或者返回的命令
     */
    public byte[] commands;

    /**
     * 串口发送或者返回的命令(hex)
     */
    public String commandsHex;

    /**
     * 发送命令超时时间
     */
    public long timeOut = 0;

    /**
     * 是否重复发送命令
     */
    public boolean reWriteCom = false;

    /**
     * 数据重发次数
     */
    public int reWriteTimes = 0;

    /**
     * 备用标识
     */
    public int flag;

    /**
     * 串口回复数据条数
     */
    public int receiveCount = 1;

}
