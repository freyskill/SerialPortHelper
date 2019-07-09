package top.keepempty;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import top.keepempty.sph.library.SerialPortConfig;
import top.keepempty.sph.library.SerialPortFinder;
import top.keepempty.sph.library.SerialPortHelper;
import top.keepempty.sph.library.SphCmdEntity;
import top.keepempty.sph.library.SphResultCallback;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "SerialPortHelper";

    private SerialPortHelper serialPortHelper;

    private TextView mShowReceiveTxt;

    private Button mSendBtn;
    private Button mOpenBtn;

    private EditText mSendDataEt;

    private Spinner mPathSpinner;
    private Spinner mBaudRateSpinner;
    private Spinner mDataSpinner;
    private Spinner mCheckSpinner;
    private Spinner mStopSpinner;

    private int baudRate = 9600;
    private int dataBits = 8;
    private char checkBits = 'N';
    private int stopBits = 1;
    private String path;

    private SerialPortFinder mSerialPortFinder;
    private String[] entryValues;
    private boolean isOpen;
    private StringBuilder receiveTxt = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mPathSpinner = findViewById(R.id.sph_path);
        mBaudRateSpinner = findViewById(R.id.sph_baudRate);
        mDataSpinner = findViewById(R.id.sph_data);
        mCheckSpinner = findViewById(R.id.sph_check);
        mStopSpinner = findViewById(R.id.sph_stop);
        mOpenBtn = findViewById(R.id.sph_openBtn);
        mSendBtn = findViewById(R.id.sph_sendBtn);
        mShowReceiveTxt = findViewById(R.id.sph_showReceiveTxt);
        mSendDataEt = findViewById(R.id.sph_sendDataEt);

        mBaudRateSpinner.setSelection(13);
        mDataSpinner.setSelection(3);
        mCheckSpinner.setSelection(0);
        mStopSpinner.setSelection(0);

        mPathSpinner.setOnItemSelectedListener(this);
        mBaudRateSpinner.setOnItemSelectedListener(this);
        mDataSpinner.setOnItemSelectedListener(this);
        mCheckSpinner.setOnItemSelectedListener(this);
        mStopSpinner.setOnItemSelectedListener(this);

        mSerialPortFinder = new SerialPortFinder();
        entryValues = mSerialPortFinder.getAllDevicesPath();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
              this, android.R.layout.simple_spinner_item,
               entryValues);
        mPathSpinner.setAdapter(adapter);

        mSendBtn = findViewById(R.id.sph_sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendTxt = mSendDataEt.getText().toString().trim();
                if(TextUtils.isEmpty(sendTxt)){
                    Toast.makeText(MainActivity.this,"请输入发送命令！",Toast.LENGTH_LONG).show();
                    return;
                }
                if (sendTxt.length() % 2 == 1) {
                    Toast.makeText(MainActivity.this,"命令错误！",Toast.LENGTH_LONG).show();
                    return;
                }
                serialPortHelper.addCommands(sendTxt);
            }
        });

        mOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen){
                    serialPortHelper.closeDevice();
                    isOpen = false;
                }else{
                    openSerialPort();
                }
                showState();
            }
        });
    }

    /**
     * 打开串口
     */
    private void openSerialPort(){

        /**
         * 串口参数
         */
        SerialPortConfig serialPortConfig = new SerialPortConfig();
        serialPortConfig.mode = 0;
        serialPortConfig.path = path;
        serialPortConfig.baudRate = baudRate;
        serialPortConfig.dataBits = dataBits;
        serialPortConfig.parity   = checkBits;
        serialPortConfig.stopBits = stopBits;


        // 初始化串口
        serialPortHelper = new SerialPortHelper(16);
        // 设置串口参数
        serialPortHelper.setConfigInfo(serialPortConfig);
        // 开启串口
        isOpen = serialPortHelper.openDevice();
        if(!isOpen){
            Toast.makeText(this,"串口打开失败！",Toast.LENGTH_LONG).show();
        }
        serialPortHelper.setSphResultCallback(new SphResultCallback() {
            @Override
            public void onSendData(SphCmdEntity sendCom) {
                Log.d(TAG, "发送命令：" + sendCom.commandsHex);
            }

            @Override
            public void onReceiveData(SphCmdEntity data) {
                Log.d(TAG, "收到命令：" + data.commandsHex);
                receiveTxt.append(data.commandsHex).append("\n");
                mShowReceiveTxt.setText(receiveTxt.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sph_path:
                path = entryValues[position];
                break;
            case R.id.sph_baudRate:
                String[] baud_rates = getResources().getStringArray(R.array.baud_rate_arr);
                baudRate = Integer.parseInt(baud_rates[position]);
                break;
            case R.id.sph_data:
                String[] data_rates = getResources().getStringArray(R.array.data_bits_arr);
                dataBits = Integer.parseInt(data_rates[position]);
                break;
            case R.id.sph_check:
                String[] check_rates = getResources().getStringArray(R.array.check_digit_arr);
                checkBits = check_rates[position].charAt(0);
                break;
            case R.id.sph_stop:
                String[] stop_rates = getResources().getStringArray(R.array.stop_bits_arr);
                stopBits = Integer.parseInt(stop_rates[position]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showState(){
        if(isOpen){
            Toast.makeText(this,"串口打开成功！",Toast.LENGTH_LONG).show();
            mOpenBtn.setText("关闭串口");
            mOpenBtn.setTextColor(ContextCompat.getColor(this,R.color.org));
            mOpenBtn.setBackgroundResource(R.drawable.button_style_stroke);
        }else {
            mOpenBtn.setText("打开串口");
            mOpenBtn.setTextColor(ContextCompat.getColor(this,R.color.white));
            mOpenBtn.setBackgroundResource(R.drawable.button_style_org);
        }
    }


    public void clearSend(View view) {
        mSendDataEt.setText("");
    }

    public void clearReceive(View view) {
        receiveTxt = new StringBuilder();
        mShowReceiveTxt.setText("");
    }
}
