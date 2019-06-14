package com.jxit.jxitprinter1_6.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jxit.jxitbluetoothprintersdk1_4.Jxit_esc;
import com.jxit.jxitprinter1_6.R;

import java.util.ArrayList;
import java.util.List;


public class Barcode1dActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    private static final int CONNECT_FAILD_BARCODE1D = 222;
    private static final int PRINT_FAILD_BARCODE1D = 223;
    private static final int PRINT_SUCCESS_BARCODE1D = 224;

    private Toolbar toolbar;
    private Spinner mSpBarcode1dType,mSpBarcode1dWidth,mSpBarcode1dHRIPosition,mSpBarcode1dHRIFont;
    private EditText  mEtBarcode1dHeight,mEtBarcode1dContent;
    private Button mBtnBarcode1dSend,mBtnbarcode1dDefault;
    private TextView mTvBarcode1dTypeInfo;

    private byte[] barcode1dTypeBytes,barcode1dWidthBytes,barcode1dHeightBytes,barcode1dHRIpositionBytes,barcode1dHRIFontBytes;
    private List<String> listBarcode1dType,listBarcode1dWidth,listBarcode1dHRIposition,listBarcode1dHRIFont;
    private String barcode1dContent ;
    private String address;
    private int barcode1dHeightInt;

    private Jxit_esc mJxit_esc_barcode1d;

    /**
     * Handler
     */
    private Handler mBarcode1dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_FAILD_BARCODE1D:
                    mBtnBarcode1dSend.setEnabled(true);
                    mJxit_esc_barcode1d.close();
                    Toast.makeText(Barcode1dActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                    break;
                case PRINT_FAILD_BARCODE1D:
                    mBtnBarcode1dSend.setEnabled(true);
                    mJxit_esc_barcode1d.close();
                    Toast.makeText(Barcode1dActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                    break;
                case PRINT_SUCCESS_BARCODE1D:
                    mBtnBarcode1dSend.setEnabled(true);
                    mJxit_esc_barcode1d.close();
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
        setContentView(R.layout.activity_barcode1d);

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

        barcode1dTypeBytes = new byte[3];
        barcode1dWidthBytes = new byte[3];
        barcode1dHeightBytes = new byte[3];
        barcode1dHRIpositionBytes = new byte[3];
        barcode1dHRIFontBytes = new byte[3];

        mTvBarcode1dTypeInfo = (TextView) findViewById(R.id.tv_barcode1d_type_info) ;

        mSpBarcode1dType = (Spinner) findViewById(R.id.sp_barcode_1d_type);
        mSpBarcode1dWidth = (Spinner) findViewById(R.id.sp_barcode_1d_width) ;
        mSpBarcode1dHRIPosition =(Spinner) findViewById(R.id.sp_HRI_position);
        mSpBarcode1dHRIFont = (Spinner) findViewById(R.id.sp_HRI_font) ;

        mEtBarcode1dHeight = (EditText) findViewById(R.id.et_barcode_1d_height);
        mEtBarcode1dContent = (EditText) findViewById(R.id.et_barcode_1d_content);

        mBtnBarcode1dSend = (Button) findViewById(R.id.btn_barcode_1d_send);
        mBtnbarcode1dDefault = (Button) findViewById(R.id.btn_barcode_1d_default);

        listBarcode1dType = new ArrayList<>();
        listBarcode1dType.add("UPC-A");
        listBarcode1dType.add("UPC-E");
        listBarcode1dType.add("EAN13");
        listBarcode1dType.add("EAN8");
        listBarcode1dType.add("CODE39");
        listBarcode1dType.add("ITF");
        listBarcode1dType.add("CODABAR");
        listBarcode1dType.add("CODE93");
        listBarcode1dType.add("CODE128");
        ArrayAdapter<String> listAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listBarcode1dType);
        listAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpBarcode1dType.setAdapter(listAdapter1);
        mSpBarcode1dType.setOnItemSelectedListener(this);

        listBarcode1dWidth = new ArrayList<>();
        listBarcode1dWidth.add("2");
        listBarcode1dWidth.add("1");
        listBarcode1dWidth.add("3");
        ArrayAdapter<String> listAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listBarcode1dWidth);
        listAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpBarcode1dWidth.setAdapter(listAdapter2);
        mSpBarcode1dWidth.setOnItemSelectedListener(this);

        listBarcode1dHRIposition = new ArrayList<>();
        listBarcode1dHRIposition.add("显示在下方°");
        listBarcode1dHRIposition.add("不显示");
        listBarcode1dHRIposition.add("显示在上方°");
        ArrayAdapter<String> listAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listBarcode1dHRIposition);
        listAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpBarcode1dHRIPosition.setAdapter(listAdapter3);
        mSpBarcode1dHRIPosition.setOnItemSelectedListener(this);

        listBarcode1dHRIFont = new ArrayList<>();
        listBarcode1dHRIFont.add("字体B");
        listBarcode1dHRIFont.add("字体A");
        ArrayAdapter<String> listAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listBarcode1dHRIFont);
        listAdapter4.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpBarcode1dHRIFont.setAdapter(listAdapter4);
        mSpBarcode1dHRIFont.setOnItemSelectedListener(this);

        mBtnBarcode1dSend.setOnClickListener(this);
        mBtnbarcode1dDefault.setOnClickListener(this);

        mJxit_esc_barcode1d = Jxit_esc.getInstance();
    }

    /**
     * initToolbar
     */
    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.barcode_1d_bar);
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
        if(view.getId() == R.id.btn_barcode_1d_send){
            mBtnBarcode1dSend.setEnabled(false);
            barcode1dHeightInt = Integer.parseInt(mEtBarcode1dHeight.getText().toString());
            if(barcode1dHeightInt <=0 || barcode1dHeightInt >= 255) barcode1dHeightInt = 50;
            barcode1dHeightBytes[0] = 0x1D;
            barcode1dHeightBytes[1] = 0x68;
            barcode1dHeightBytes[2] = (byte) barcode1dHeightInt;
            barcode1dContent = mEtBarcode1dContent.getText().toString();
            if(!BluetoothAdapter.checkBluetoothAddress(MainActivity.btDeviceAddress)){
                Toast.makeText(this,"请选择打印机",Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!mJxit_esc_barcode1d.connectDevice(address)) {
                        mBarcode1dHandler.sendEmptyMessage(CONNECT_FAILD_BARCODE1D);
                    }
                    if(!printText()) {
                        mBarcode1dHandler.sendEmptyMessage(PRINT_FAILD_BARCODE1D);
                    }
                    try {
                        Thread.currentThread().sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mJxit_esc_barcode1d.close();
                    mBarcode1dHandler.sendEmptyMessage(PRINT_SUCCESS_BARCODE1D);
                }
                private boolean printText(){
                    if(!mJxit_esc_barcode1d.esc_write_bytes(new byte[]{0x1B, 0x40})) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(barcode1dWidthBytes)) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(barcode1dHRIpositionBytes)) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(barcode1dHRIFontBytes)) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(barcode1dHeightBytes)) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(barcode1dTypeBytes)) return false;
                    if(!mJxit_esc_barcode1d.esc_print_text(barcode1dContent)) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(new byte[]{0x00})) return false;
                    if(!mJxit_esc_barcode1d.esc_write_bytes(new byte[]{0x0D,0x0A})) return false;
                    return true;
                }
            }).start();
        }else if(view.getId() == R.id.btn_barcode_1d_default){
            mSpBarcode1dType.setSelection(0,true);
            mSpBarcode1dWidth.setSelection(0,true);
            mSpBarcode1dHRIPosition.setSelection(0,true);
            mSpBarcode1dHRIFont.setSelection(0,true);
            mEtBarcode1dHeight.setText("50");
            mEtBarcode1dContent.setText("123456789012");
            mBtnBarcode1dSend.setClickable(true);
        }
    }

    /**
     * onItemSelected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.sp_barcode_1d_type){
            switch (i){
                case 0:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x00;
                    mTvBarcode1dTypeInfo.setText(R.string.upc_a_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEtBarcode1dContent.setText(R.string.upc_a_default);
                    break;
                case 1:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x01;
                    mTvBarcode1dTypeInfo.setText(R.string.upc_e_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEtBarcode1dContent.setText(R.string.upc_e_default);
                    break;
                case 2:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x02;
                    mTvBarcode1dTypeInfo.setText(R.string.ean13_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                    mEtBarcode1dContent.setText(R.string.ean13_default);
                    break;
                case 3:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x03;
                    mTvBarcode1dTypeInfo.setText(R.string.ean8_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    mEtBarcode1dContent.setText(R.string.ean8_default);
                    break;
                case 4:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x04;
                    mTvBarcode1dTypeInfo.setText(R.string.code39_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
                    mEtBarcode1dContent.setText(R.string.code39_default);
                    break;
                case 5:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x05;
                    mTvBarcode1dTypeInfo.setText(R.string.itf_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                    mEtBarcode1dContent.setText(R.string.itf_default);
                    break;
                case 6:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x06;
                    mTvBarcode1dTypeInfo.setText(R.string.codabar_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    mEtBarcode1dContent.setText(R.string.codabar_default);
                    break;
                case 7:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x07;
                    mTvBarcode1dTypeInfo.setText(R.string.code93_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
                    mEtBarcode1dContent.setText(R.string.code93_default);
                    break;
                case 8:
                    barcode1dTypeBytes[0]= 0x1D;
                    barcode1dTypeBytes[1]= 0x6B;
                    barcode1dTypeBytes[2]= 0x08;
                    mTvBarcode1dTypeInfo.setText(R.string.code128_length);
                    mEtBarcode1dContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(80)});
                    mEtBarcode1dContent.setText(R.string.code128_default);
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_barcode_1d_width){
            switch (i){
                case 0:
                    barcode1dWidthBytes[0]= 0x1D;
                    barcode1dWidthBytes[1]= 0x77;
                    barcode1dWidthBytes[2]= 0x02;
                    break;
                case 1:
                    barcode1dWidthBytes[0]= 0x1D;
                    barcode1dWidthBytes[1]= 0x77;
                    barcode1dWidthBytes[2]= 0x01;
                    break;
                case 2:
                    barcode1dWidthBytes[0]= 0x1D;
                    barcode1dWidthBytes[1]= 0x77;
                    barcode1dWidthBytes[2]= 0x03;
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_HRI_position){
            switch (i){
                case 0:
                    barcode1dHRIpositionBytes[0]= 0x1D;
                    barcode1dHRIpositionBytes[1]= 0x48;
                    barcode1dHRIpositionBytes[2]= 0x02;
                    break;
                case 1:
                    barcode1dHRIpositionBytes[0]= 0x1D;
                    barcode1dHRIpositionBytes[1]= 0x48;
                    barcode1dHRIpositionBytes[2]= 0x01;
                    break;
                case 2:
                    barcode1dHRIpositionBytes[0]= 0x1D;
                    barcode1dHRIpositionBytes[1]= 0x48;
                    barcode1dHRIpositionBytes[2]= 0x00;
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_HRI_font){
            switch (i){
                case 0:
                    barcode1dHRIFontBytes[0]= 0x1D;
                    barcode1dHRIFontBytes[1]= 0x66;
                    barcode1dHRIFontBytes[2]= 0x01;
                    break;
                case 1:
                    barcode1dHRIFontBytes[0]= 0x1D;
                    barcode1dHRIFontBytes[1]= 0x66;
                    barcode1dHRIFontBytes[2]= 0x00;
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
