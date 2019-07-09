package top.keepempty.sph.library;

/**
 * 串口配置数据
 * @author ：frey
 * @date：2019/4/03 16:15
 */
public class SerialPortConfig {

    /**
     * 串口地址
     */
    public String path;
    /**
     * 波特率
     */
    public int baudRate = 9600;
    /**
     * 数据位 取值 位 7或 8
     */
    public int dataBits = 8;
    /**
     * 停止位 取值 1 或者 2
     */
    public int stopBits = 1;
    /**
     * 校验类型 取值 N ,E, O,
     */
    public char parity  = 'n';

    /**
     * 是否使用原始模式(Raw Mode)方式来通讯
     * 取值 0=nothing,
     *     1=Raw mode,
     *     2=no raw mode
     */
    public int mode = 0;
}
