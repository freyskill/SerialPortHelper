# SerialPortHelper

Android串口通讯助手可以用于需要使用串口通信的Android外设，该库有如下特点：

1. 串口通信部分使用C++实现，在笔者接触的部分设备上实测，使用C++实现与Google官方提供的Demo的方式要快；
2. 支持且必须设置串口接收最大数据长度，初始化库时填入该参数，这样设置的原因是考虑在实际使用中，规定的串口通信协议格式一般会固定有最大长度，方便对数据进行处理；
3. 支持命令一发一收，通过对串口的读写线程进行同步控制，命令会先加入到队列然后依次发送和接收，前提需要设置超时时间以及超时处理，参考下面第4、5点；
4. 支持超时设置，设置超时时间后，如果命令在设置的时间内未反馈，则会根据设置的操作进行重发或退出该命令；
5. 支持超时重发（可以N次重发，具体按需设置）与退出，退出会调用接收回调的 **onComplete** 方法。

##### 1、DEMO演示

使用该库简单实现的串口调试助手工具，[APK下载](https://github.com/freyskill/SerialPortHelper/blob/master/SerialPortHelperV1.0.1.apk)

![image](https://github.com/freyskill/SerialPortHelper/blob/master/SerialPortHelper.png)



##### 2、接入方式

Step 1. Add the JitPack repository to your build file

Add it in your root **build.gradle** at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```

Step 2. Add the dependency

```groovy
dependencies {
         implementation 'com.github.freyskill:SerialPortHelper:v1.0.1'
}
```

##### 3、使用说明

初始化需要设置maxSize，也可以设置isReceiveMaxSize该参数默认为false，详细说明如下：

int maxSize;  // 设置串口读取的最大数据长度

boolean isReceiveMaxSize; // 设置是否接收命令按最大长度进行返回，比如串口协议定义的格式长度为16个字节，这样可以设置maxSize为16，然后设置该参数为true，则接收的命令就会返回16个字节的长度。

**提示：**设置isReceiveMaxSize为true是为了处理命令返回不完整的情况，例如完整命令长度为16，但是串口读的过程分几次返回。

```java
SerialPortHelper serialPortHelper = new SerialPortHelper(32);
SerialPortHelper serialPortHelper = SerialPortHelper(32,true);
```

###### 1.初始化串口

```java
//方式一：快速接入方式，设置好串口地址，或者地址和波特率即可，数据位、停止位、校验类型分别默认为8、1、N。
SerialPortHelper serialPortHelper = new SerialPortHelper(32);
//serialPortHelper.openDevice("dev/ttyS0");
serialPortHelper.openDevice("dev/ttyS0",11520);
// 数据接收回调
serialPortHelper.setSphResultCallback(new SphResultCallback() {
            @Override
            public void onSendData(SphCmdEntity sendCom) {
                Log.d(TAG, "发送命令：" + sendCom.commandsHex);
            }

            @Override
            public void onReceiveData(SphCmdEntity data) {
                Log.d(TAG, "收到命令：" + data.commandsHex);
            
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成");
            }
        });
```

```java
//方式二：通过SerialPortConfig设置相关串口参数

//串口参数
SerialPortConfig serialPortConfig = new SerialPortConfig();
serialPortConfig.mode = 0;            // 是否使用原始模式(Raw Mode)方式来通讯
serialPortConfig.path = path;         // 串口地址
serialPortConfig.baudRate = baudRate; // 波特率
serialPortConfig.dataBits = dataBits; // 数据位 取值 位 7或 8
serialPortConfig.parity   = checkBits;// 检验类型 取值 N ,E, O
serialPortConfig.stopBits = stopBits; // 停止位 取值 1 或者 2

// 初始化串口
serialPortHelper = new SerialPortHelper(16);
// 设置串口参数
serialPortHelper.setConfigInfo(serialPortConfig);
// 开启串口
isOpen = serialPortHelper.openDevice();
if(!isOpen){
    Toast.makeText(this,"串口打开失败！",Toast.LENGTH_LONG).show();
}
// 数据接收回调
serialPortHelper.setSphResultCallback(new SphResultCallback() {
    @Override
    public void onSendData(SphCmdEntity sendCom) {
        Log.d(TAG, "发送命令：" + sendCom.commandsHex);
    }

    @Override
    public void onReceiveData(SphCmdEntity data) {
        Log.d(TAG, "收到命令：" + data.commandsHex);
        
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "完成");
    }
});
```

2、数据发送与接收

```java
// 发送数据
serialPortHelper.addCommands(sendHexTxt);   // 发送十六进制字符串
serialPortHelper.addCommands(sendComBytes); // 发送字节数组

// 发送数据实体
SphCmdEntity comEntry = new SphCmdEntity();
comEntry.commands = commands; // 发送命令字节数组
comEntry.flag = flag;         // 备用标识
comEntry.commandsHex = DataConversion.encodeHexString(commands);  // 发送十六进制字符串
comEntry.timeOut = 100;       // 超时时间 ms
comEntry.reWriteCom = false;  // 超时是否重发 默认false
comEntry.reWriteTimes = 5;    // 重发次数 
comEntry.receiveCount = 1;    // 接收数据条数，默认为1
serialPortHelper.addCommands(comEntry);
```

```java
// 数据接收回调
serialPortHelper.setSphResultCallback(new SphResultCallback() {
    @Override
    public void onSendData(SphCmdEntity sendCom) {
        Log.d(TAG, "发送命令：" + sendCom.commandsHex);
    }

    @Override
    public void onReceiveData(SphCmdEntity data) {
        // 对于接受数据的SphCmdEntity，其中需要使用的有 
        // commandsHex 返回的十六进制数据
        // commands    返回的字节数组
        // flag        备用标识，例如标识该命令是相关操作
        Log.d(TAG, "收到命令：" + data.commandsHex);
        
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "完成");
    }
});
```

##### 4、关闭串口

```java
serialPortHelper.closeDevice();
```
