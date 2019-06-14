package com.jxit.jxitprinter1_6.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jxit.jxitbluetoothprintersdk1_4.Jxit_esc;
import com.jxit.jxitprinter1_6.R;

import java.util.ArrayList;
import java.util.List;

public class Barcode2dActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    private static final int CONNECT_FAILD_BARCODE2D = 2222;
    private static final int PRINT_FAILD_BARCODE2D = 2223;
    private static final int PRINT_SUCCESS_BARCODE2D = 2224;

    private Toolbar toolbar;
    private Spinner mSpBarcode2dType,mSpBarcode2dEnlargeTimes;
    private EditText mEtBarcode2dVersion,mEtBarcode2dErrorCorrectionLevel,mEtBarcode2dContent;
    private Button mBtnBarcode2dSend,mBtnBarcode2dDefault;

    private byte[] barcode2dTypeBytes,barcode2dEnlargeTimesBytes,barcode2dVersionBytes,barcode2dErrorCorrectionLevelBytes;
    private List<String> listBarcode2dType,listBarcode2dEnlargeTimes;
    private String barcode2dContent ;
    private String address;
    private int barcode2dVersionInt,barcode2dErrorCorrectionLevelInt;

    private Jxit_esc mJxit_esc_barcode2d;

    /**
     * Handler
     */
    private Handler mBarcode2dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_FAILD_BARCODE2D:
                    mBtnBarcode2dSend.setEnabled(true);
                    mJxit_esc_barcode2d.close();
                    Toast.makeText(Barcode2dActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                    break;
                case PRINT_FAILD_BARCODE2D:
                    mBtnBarcode2dSend.setEnabled(true);
                    mJxit_esc_barcode2d.close();
                    Toast.makeText(Barcode2dActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                    break;
                case PRINT_SUCCESS_BARCODE2D:
                    mBtnBarcode2dSend.setEnabled(true);
                    mJxit_esc_barcode2d.close();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode2d);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        address=bundle.getString("btDeviceAddress");

        init();
    }

    /**
     * init
     */
    private void init(){
        initToolbar();

        barcode2dTypeBytes = new byte[3];
        barcode2dEnlargeTimesBytes = new byte[3];
        barcode2dVersionBytes = new byte[3];
        barcode2dErrorCorrectionLevelBytes = new byte[3];

        mSpBarcode2dType = (Spinner) findViewById(R.id.sp_barcode_2d_type);
        mSpBarcode2dEnlargeTimes = (Spinner) findViewById(R.id.sp_barcode_2d_enlarge_times) ;

        mEtBarcode2dVersion = (EditText) findViewById(R.id.et_barcode_2d_version);
        mEtBarcode2dErrorCorrectionLevel = (EditText) findViewById(R.id.et_barcode_2d_error_correction_level);
        mEtBarcode2dContent = (EditText) findViewById(R.id.et_barcode_2d_content);

        mBtnBarcode2dSend = (Button) findViewById(R.id.btn_barcode_2d_send);
        mBtnBarcode2dDefault = (Button) findViewById(R.id.btn_barcode_2d_default);

        listBarcode2dType = new ArrayList<>();
        listBarcode2dType.add("QRCODE");
        listBarcode2dType.add("PDF417");
        listBarcode2dType.add("DATAMATRIX");
        ArrayAdapter<String> listAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listBarcode2dType);
        listAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpBarcode2dType.setAdapter(listAdapter1);
        mSpBarcode2dType.setOnItemSelectedListener(this);

        listBarcode2dEnlargeTimes = new ArrayList<>();
        listBarcode2dEnlargeTimes.add("2");
        listBarcode2dEnlargeTimes.add("1");
        listBarcode2dEnlargeTimes.add("3");
        listBarcode2dEnlargeTimes.add("4");
        ArrayAdapter<String> listAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listBarcode2dEnlargeTimes);
        listAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpBarcode2dEnlargeTimes.setAdapter(listAdapter2);
        mSpBarcode2dEnlargeTimes.setOnItemSelectedListener(this);

        mBtnBarcode2dSend.setOnClickListener(this);
        mBtnBarcode2dDefault.setOnClickListener(this);

        mJxit_esc_barcode2d = Jxit_esc.getInstance();
    }

    /**
     * initToolbar
     */
    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.barcode_2d_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * onClick
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_barcode_2d_send){
            mBtnBarcode2dSend.setEnabled(false);
            barcode2dVersionInt = Integer.parseInt(mEtBarcode2dVersion.getText().toString());
            if(barcode2dVersionInt < 0 || barcode2dVersionInt > 20) barcode2dVersionInt = 3;
            barcode2dErrorCorrectionLevelInt = Integer.parseInt(mEtBarcode2dErrorCorrectionLevel.getText().toString());
            if(barcode2dErrorCorrectionLevelInt < 0 || barcode2dErrorCorrectionLevelInt > 4) barcode2dErrorCorrectionLevelInt = 2;
            final byte[] barcode2dVerErrCorLeBytes = new byte[2];
            barcode2dVerErrCorLeBytes[0] = (byte) barcode2dVersionInt;
            barcode2dVerErrCorLeBytes[1] = (byte) barcode2dErrorCorrectionLevelInt;
            barcode2dContent = mEtBarcode2dContent.getText().toString();
            if(!BluetoothAdapter.checkBluetoothAddress(MainActivity.btDeviceAddress)){
                Toast.makeText(this,"请选择打印机",Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!mJxit_esc_barcode2d.connectDevice(address)) {
                        mBarcode2dHandler.sendEmptyMessage(CONNECT_FAILD_BARCODE2D);
                    }else {
                        if(!printText()){
                            mBarcode2dHandler.sendEmptyMessage(PRINT_FAILD_BARCODE2D);
                        } else {
                            mBarcode2dHandler.sendEmptyMessage(PRINT_SUCCESS_BARCODE2D);
                        }
                    }

                }
                private boolean printText(){
                    if(!mJxit_esc_barcode2d.esc_reset()) {
                        return false;
                    }

                    if(!mJxit_esc_barcode2d.esc_write_bytes(barcode2dEnlargeTimesBytes)){
                        return false;
                    }
                    if(!mJxit_esc_barcode2d.esc_write_bytes(barcode2dTypeBytes)){
                        return false;
                    }
                    if(barcode2dTypeBytes[2] == 0x20) {
                        if(!mJxit_esc_barcode2d.esc_write_bytes(barcode2dVerErrCorLeBytes)){
                            return false;
                        }
                    }else {
                        barcode2dTypeBytes[2] = 0x20;
                        if(!mJxit_esc_barcode2d.esc_write_bytes(barcode2dVerErrCorLeBytes)){
                            return false;
                        }
                    }
                    if(!mJxit_esc_barcode2d.esc_print_text(barcode2dContent)){
                        return false;
                    }
                    if(!mJxit_esc_barcode2d.esc_write_bytes(new byte[]{0x00})){
                        return false;
                    }
                    return mJxit_esc_barcode2d.esc_write_bytes(new byte[]{0x0D, 0x0A});
                }
            }).start();
        }else if(view.getId() == R.id.btn_barcode_2d_default){
            mSpBarcode2dType.setSelection(0,true);
            mSpBarcode2dEnlargeTimes.setSelection(0,true);
            mEtBarcode2dVersion.setText("0");
            mEtBarcode2dErrorCorrectionLevel.setText("2");
            mEtBarcode2dContent.setText("123456789012");
        }
    }

    /**
     * onItemSelected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.sp_barcode_2d_type){
            switch (i){
                case 0:
                    barcode2dTypeBytes[0]= 0x1D;
                    barcode2dTypeBytes[1]= 0x6B;
                    barcode2dTypeBytes[2]= 0x20;
                    break;
                case 1:
                    barcode2dTypeBytes[0]= 0x1D;
                    barcode2dTypeBytes[1]= 0x6B;
                    barcode2dTypeBytes[2]= 0x0A;
                    break;
                case 2:
                    barcode2dTypeBytes[0]= 0x1D;
                    barcode2dTypeBytes[1]= 0x6B;
                    barcode2dTypeBytes[2]= 0x0C;
                    break;
                default:
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_barcode_2d_enlarge_times){
            switch (i){
                case 0:
                    barcode2dEnlargeTimesBytes[0]= 0x1D;
                    barcode2dEnlargeTimesBytes[1]= 0x77;
                    barcode2dEnlargeTimesBytes[2]= 0x02;
                    break;
                case 1:
                    barcode2dEnlargeTimesBytes[0]= 0x1D;
                    barcode2dEnlargeTimesBytes[1]= 0x77;
                    barcode2dEnlargeTimesBytes[2]= 0x01;
                    break;
                case 2:
                    barcode2dEnlargeTimesBytes[0]= 0x1D;
                    barcode2dEnlargeTimesBytes[1]= 0x77;
                    barcode2dEnlargeTimesBytes[2]= 0x03;
                    break;
                case 3:
                    barcode2dEnlargeTimesBytes[0]= 0x1D;
                    barcode2dEnlargeTimesBytes[1]= 0x77;
                    barcode2dEnlargeTimesBytes[2]= 0x04;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
