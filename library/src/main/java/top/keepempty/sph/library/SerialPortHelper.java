package top.keepempty.sph.library;

import android.util.Log;

/**
 * 串口助手
 * @author ：frey
 * @date：2019/3/30 18:26
 */
public class SerialPortHelper {

    private final static String TAG = SerialPortHelper.class.getSimpleName();

    private boolean mIsOpen = false;

    private SerialPortConfig serialPortConfig;

    private SphThreads sphThreads;

    /**
     * 最大接收数据的长度
     */
    private int maxSize;

    /**
     * 是否需要返回最大数据接收长度
     */
    private boolean isReceiveMaxSize;

    /**
     * 数据回调
     */
    private SphResultCallback onResultCallback;

    /**
     * 数据处理
     */
    private SphDataProcess processingData;


    /**
     * 初始化串口操作
     * @param maxSize 串口每次读取数据的最大长度
     */
    public SerialPortHelper(int maxSize) {
        this(maxSize,false);
    }

    /**
     * 初始化串口操作
     * @param maxSize           串口每次读取数据的最大长度
     * @param isReceiveMaxSize  是否需要按最大接收数据进行返回
     */
    public SerialPortHelper(int maxSize, boolean isReceiveMaxSize){
        this(maxSize,isReceiveMaxSize, new SerialPortConfig());
    }

    /**
     * 初始化串口操作
     * @param maxSize           串口每次读取数据的最大长度
     * @param isReceiveMaxSize  是否需要按最大接收数据进行返回
     * @param serialPortConfig  串口数据
     */
    public SerialPortHelper(int maxSize,boolean isReceiveMaxSize, SerialPortConfig serialPortConfig) {
        this.maxSize = maxSize;
        this.isReceiveMaxSize = isReceiveMaxSize;
        this.serialPortConfig = serialPortConfig;
    }

    /**
     * 设置数据回调
     * @param onResultCallback 数据回调
     */
    public void setSphResultCallback(SphResultCallback onResultCallback){
        if(sphThreads==null){
            this.onResultCallback = onResultCallback;
            return;
        }
        processingData.setSphResultCallback(onResultCallback);
    }

    /**
     * 串口设置
     */
    public void setConfigInfo(SerialPortConfig serialPortConfig){
        this.serialPortConfig = serialPortConfig;
    }

    /**
     * 打开串口设备
     * @param path  串口地址
     */
    public boolean openDevice(String path){
        this.serialPortConfig.path = path;
        return openDevice();
    }

    /**
     * 打开串口设备
     * @param path      串口地址
     * @param baudRate  波特率
     */
    public boolean openDevice(String path,int baudRate){
        this.serialPortConfig.path = path;
        this.serialPortConfig.baudRate = baudRate;
        return openDevice();
    }


    /**
     * 打开串口设备
     */
    public boolean openDevice(){
        if(serialPortConfig==null){
            throw new IllegalArgumentException("'SerialPortConfig' must can not be null!!! ");
        }
        if(serialPortConfig.path == null){
            throw new IllegalArgumentException("You not have setting the device path, " +
                    "you must 'new SerialPortHelper(String path)' or call 'openDevice(String path)' ");
        }
        int i = SerialPortJNI.openPort(
                this.serialPortConfig.path,
                this.serialPortConfig.baudRate,
                this.serialPortConfig.dataBits,
                this.serialPortConfig.stopBits,
                this.serialPortConfig.parity);

        // 是否设置原始模式(Raw Mode)方式来通讯
        if(serialPortConfig.mode!=0){
            SerialPortJNI.setMode(serialPortConfig.mode);
        }

        // 打开串口成功
        if(i==1){
            mIsOpen = true;
            // 创建数据处理
            processingData = new SphDataProcess(maxSize);
            processingData.setRecevieMaxSize(isReceiveMaxSize);
            processingData.setSphResultCallback(onResultCallback);
            // 开启读写线程
            sphThreads = new SphThreads(processingData);
        }else{
            mIsOpen = false;
            Log.e(TAG,"cannot open the device !!! " +
                    "path:"+serialPortConfig.path);
        }
        return mIsOpen;
    }

    /**
     * 发送串口命令（hex）
     * @param hex  十六进制命令
     */
    public void addCommands(String hex){
        addCommands(hex,0);
    }

    /**
     * 发送串口命令（hex）
     * @param hex  十六进制命令
     * @param flag 备用标识
     */
    public void addCommands(String hex,int flag){
        byte[] bytes = DataConversion.decodeHexString(hex);
        addCommands(bytes,flag);
    }

    /**
     * 发送串口命令
     * @param commands 串口命令
     */
    public void addCommands(byte[] commands){
        addCommands(commands,0);
    }

    /**
     * 发送串口命令
     * @param commands 串口命令
     * @param flag     备用标识
     */
    public void addCommands(byte[] commands,int flag){
        SphCmdEntity comEntry = new SphCmdEntity();
        comEntry.commands = commands;
        comEntry.flag = flag;
        comEntry.commandsHex = DataConversion.encodeHexString(commands);
        addCommands(comEntry);
    }

    /**
     * 发送串口命令
     * @param sphCmdEntity 串口命令数据
     */
    public void addCommands(SphCmdEntity sphCmdEntity){
        if(sphCmdEntity==null){
            Log.e(TAG,"SphCmdEntity can't be null !!!");
            return;
        }
        if(!isOpenDevice()){
            Log.e(TAG,"You not open device !!! ");
            return;
        }
        // 开启写数据线程
        sphThreads.startWriteThread();
        // 添加发送命令
        processingData.addCommands(sphCmdEntity);

    }

    /**
     * 关闭串口
     */
    public void closeDevice(){
        SerialPortJNI.closePort();
        if(sphThreads !=null){
            sphThreads.stop();
        }
    }

    /**
     * 判断串口是否打开
     */
    public boolean isOpenDevice(){
        return mIsOpen;
    }



}
