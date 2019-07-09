package top.keepempty.sph.library;

import java.util.ArrayList;

/**
 * 串口读写命令线程同步控制
 * @author：fery
 * @date：2019/3/30 18:35
 */
public class SphConcurrentCom {

    private boolean isGet;

    private SphCmdEntity currentCmdEntity;

    /**
     * 串口发送命令集合
     */
    private ArrayList<SphCmdEntity> mEntryList = new ArrayList<SphCmdEntity>();

    /**
     * 添加串口发送命令
     * @param command 命令数据
     */
    public void addCommands(SphCmdEntity command) {
        this.mEntryList.add(command);
    }

    /**
     * 获取当前命令
     * @return currentCmdEntity
     */
    public SphCmdEntity getCurrentCmdEntity() {
        return currentCmdEntity;
    }

    /**
     * 判断命令是否为空
     * @return
     */
    public boolean isCmdEmpty() {
        return mEntryList.isEmpty();
    }

    /**
     * 从命令集合中取命令数据
     * @return currentCmdEntity
     */
    public synchronized SphCmdEntity get() {
        while (isGet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentCmdEntity = mEntryList.remove(0);
        notify();
        return currentCmdEntity;
    }

    /**
     * 命令接收完成
     */
    public synchronized void doneCom() {
        if(isCmdEmpty()){
            currentCmdEntity = null;
            return;
        }
        while (!isGet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentCmdEntity = null;
        notify();
    }

    /**
     * 设置读写同步状态
     * @param status
     */
    public void setStatus(boolean status){
        this.isGet = status;
    }

}
