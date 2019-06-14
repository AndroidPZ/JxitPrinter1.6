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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jxit.jxitbluetoothprintersdk1_4.Jxit_esc;
import com.jxit.jxitprinter1_6.R;

import java.util.ArrayList;
import java.util.List;

public class TextActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener {
    private static final int CONNECT_FAILD_TEXT = 111;
    private static final int PRINT_FAILD_TEXT = 112;
    private static final int PRINT_SUCCESS_TEXT = 113;

    private Toolbar toolbar;
    private CheckBox mCbBold,mCbUnderline,mCbReverse;
    private Spinner mSpCharType,mSpCharAlign,mSpCharRotate,mSpCharWidth,mSpCharHeight;
    private EditText mEtTextContent;
    private Button mBtnTextSend,mBtnTextDefault;

    private List<String> listCharacterFont,listAlign,listRotate,listWinth,listHeight;
    private byte[] charBoldBytes,charUnderlineBytes,charReverseBytes;
    private byte[] charFontBytes,charAlignBytes,charRotateBytes,charWidthBytes,charHeightBytes;
    private String textContent ;
    private String address;

    private Jxit_esc mJxit_esc_text;

    /**
     * Handler
     */
    private Handler mTextHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_FAILD_TEXT:
                    mBtnTextSend.setEnabled(true);
                    mJxit_esc_text.close();
                    Toast.makeText(TextActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                    break;
                case PRINT_FAILD_TEXT:
                    mBtnTextSend.setEnabled(true);
                    mJxit_esc_text.close();
                    Toast.makeText(TextActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                    break;
                case PRINT_SUCCESS_TEXT:
                    mBtnTextSend.setEnabled(true);
                    mJxit_esc_text.close();
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
        setContentView(R.layout.activity_text);

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

        charBoldBytes = new byte[3];
        charUnderlineBytes = new byte[3];
        charReverseBytes = new byte[3];
        charFontBytes = new byte[3];
        charAlignBytes = new byte[3];
        charRotateBytes = new byte[3];
        charWidthBytes = new byte[3];
        charHeightBytes = new byte[3];
        mCbBold = (CheckBox) findViewById(R.id.cb_text_bold);
        mCbUnderline = (CheckBox) findViewById(R.id.cb_text_underline);
        mCbReverse = (CheckBox) findViewById(R.id.cb_text_reverse);
        mSpCharType = (Spinner) findViewById(R.id.sp_text_type);
        mSpCharAlign = (Spinner) findViewById(R.id.sp_text_align) ;
        mSpCharRotate =(Spinner) findViewById(R.id.sp_text_rotate);
        mSpCharWidth = (Spinner) findViewById(R.id.sp_char_width) ;
        mSpCharHeight = (Spinner) findViewById(R.id.sp_char_height);
        mEtTextContent = (EditText) findViewById(R.id.et_text_content);
        mBtnTextSend = (Button) findViewById(R.id.btn_text_send);
        mBtnTextDefault = (Button) findViewById(R.id.btn_text_default);
        mCbBold.setOnCheckedChangeListener(this);
        mCbUnderline.setOnCheckedChangeListener(this);
        mCbReverse.setOnCheckedChangeListener(this);
        listCharacterFont = new ArrayList<>();
        listCharacterFont.add("字体D");
        listCharacterFont.add("字体A");
        listCharacterFont.add("字体B");
        listCharacterFont.add("字体C");
        ArrayAdapter<String> listAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listCharacterFont);
        listAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpCharType.setAdapter(listAdapter1);
        mSpCharType.setOnItemSelectedListener(this);
        listAlign = new ArrayList<>();
        listAlign.add("左对齐");
        listAlign.add("居中对齐");
        listAlign.add("右对齐");
        ArrayAdapter<String> listAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listAlign);
        listAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpCharAlign.setAdapter(listAdapter2);
        mSpCharAlign.setOnItemSelectedListener(this);
        listRotate = new ArrayList<>();
        listRotate.add("不旋转");
        listRotate.add("顺时针旋转90°");
        listRotate.add("顺时针旋转180°");
        listRotate.add("顺时针旋转270°");
        ArrayAdapter<String> listAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listRotate);
        listAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpCharRotate.setAdapter(listAdapter3);
        mSpCharRotate.setOnItemSelectedListener(this);
        listWinth = new ArrayList<>();
        listWinth.add("1倍宽");
        listWinth.add("2倍宽");
        listWinth.add("3倍宽");
        listWinth.add("4倍宽");
        ArrayAdapter<String> listAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listWinth);
        listAdapter4.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpCharWidth.setAdapter(listAdapter4);
        mSpCharWidth.setOnItemSelectedListener(this);
        listHeight = new ArrayList<>();
        listHeight.add("1倍高");
        listHeight.add("2倍高");
        listHeight.add("3倍高");
        listHeight.add("4倍高");
        ArrayAdapter<String> listAdapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listHeight);
        listAdapter5.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpCharHeight.setAdapter(listAdapter5);
        mSpCharHeight.setOnItemSelectedListener(this);
        mBtnTextSend.setOnClickListener(this);
        mBtnTextDefault.setOnClickListener(this);

        mJxit_esc_text = Jxit_esc.getInstance();
    }

    /**
     * initToolbar
     */
    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.text_bar);
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
        if(view.getId() == R.id.btn_text_send){
            mBtnTextSend.setEnabled(false);
            textContent = mEtTextContent.getText().toString();
            if(!BluetoothAdapter.checkBluetoothAddress(address)){
                Toast.makeText(this,"请选择打印机",Toast.LENGTH_SHORT).show();
                return;
            }
            connectPrint();
        }else if(view.getId() == R.id.btn_text_default){
            mCbBold.setChecked(false);
            mCbUnderline.setChecked(false);
            mCbReverse.setChecked(false);
            mSpCharType.setSelection(0,true);
            mSpCharWidth.setSelection(0,true);
            mSpCharHeight.setSelection(0,true);
            mSpCharAlign.setSelection(0,true);
            mSpCharRotate.setSelection(0,true);
            mEtTextContent.setText("文本打印测试！");
        }
    }

    /**
     * connectPrint
     */
    private void connectPrint() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mJxit_esc_text.connectDevice(address)) {
                    mTextHandler.sendEmptyMessage(CONNECT_FAILD_TEXT);
                } else {
                    if(!printText()){
                        mTextHandler.sendEmptyMessage(PRINT_FAILD_TEXT);
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mJxit_esc_text.close();
                        mTextHandler.sendEmptyMessage(PRINT_SUCCESS_TEXT);
                    }
                }

            }
            private boolean printText(){
                if(!mJxit_esc_text.esc_write_bytes(new byte[]{0x1B, 0x40})) return false;
                if(!mJxit_esc_text.esc_write_bytes(charBoldBytes)) return false;
                if(!mJxit_esc_text.esc_write_bytes(charUnderlineBytes)) return false;
                if(!mJxit_esc_text.esc_write_bytes(charReverseBytes)) return false;
                if(!mJxit_esc_text.esc_write_bytes(charFontBytes)) return false;
                if(!mJxit_esc_text.esc_write_bytes(charAlignBytes)) return false;
                byte[] charWidthHeight = new byte[3];
                charWidthHeight[0] = 0x1D;
                charWidthHeight[1] = 0x21;
                charWidthHeight[2] = (byte) (charWidthBytes[2]+charHeightBytes[2]);
                if(!mJxit_esc_text.esc_write_bytes(charWidthHeight)) return false;
                if(!mJxit_esc_text.esc_write_bytes(charRotateBytes)) return false;
                if(!mJxit_esc_text.esc_print_text(textContent)) return false;
                return mJxit_esc_text.esc_write_bytes(new byte[]{0x0D, 0x0A});
            }
        }).start();
    }

    /**
     * onItemSelected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.sp_text_type) {
            switch (i){
                case 0:
                    charFontBytes[0]= 0x1B;
                    charFontBytes[1]= 0x4D;
                    charFontBytes[2]= 0x03;
                    break;
                case 1:
                    charFontBytes[0]= 0x1B;
                    charFontBytes[1]= 0x4D;
                    charFontBytes[2]= 0x00;
                    break;
                case 2:
                    charFontBytes[0]= 0x1B;
                    charFontBytes[1]= 0x4D;
                    charFontBytes[2]= 0x01;
                    break;
                case 3:
                    charFontBytes[0]= 0x1B;
                    charFontBytes[1]= 0x4D;
                    charFontBytes[2]= 0x02;
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_text_align) {
            switch (i){
                case 0:
                    charAlignBytes[0]= 0x1B;
                    charAlignBytes[1]= 0x61;
                    charAlignBytes[2]= 0x00;
                    break;
                case 1:
                    charAlignBytes[0]= 0x1B;
                    charAlignBytes[1]= 0x61;
                    charAlignBytes[2]= 0x01;
                    break;
                case 2:
                    charAlignBytes[0]= 0x1B;
                    charAlignBytes[1]= 0x61;
                    charAlignBytes[2]= 0x02;
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_text_rotate) {
            switch (i){
                case 0:
                    charRotateBytes[0]= 0x1B;
                    charRotateBytes[1]= 0x56;
                    charRotateBytes[2]= 0x00;
                    break;
                case 1:
                    charRotateBytes[0]= 0x1B;
                    charRotateBytes[1]= 0x56;
                    charRotateBytes[2]= 0x01;
                    break;
                case 2:
                    charRotateBytes[0]= 0x1B;
                    charRotateBytes[1]= 0x56;
                    charRotateBytes[2]= 0x02;
                    break;
                case 3:
                    charRotateBytes[0]= 0x1B;
                    charRotateBytes[1]= 0x56;
                    charRotateBytes[2]= 0x03;
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_char_width) {
            switch (i){
                case 0:
                    charWidthBytes[0]= 0x1D;
                    charWidthBytes[1]= 0x21;
                    charWidthBytes[2]= 0x00;
                    break;
                case 1:
                    charWidthBytes[0]= 0x1D;
                    charWidthBytes[1]= 0x21;
                    charWidthBytes[2]= 0x10;
                    break;
                case 2:
                    charWidthBytes[0]= 0x1D;
                    charWidthBytes[1]= 0x21;
                    charWidthBytes[2]= 0x20;
                    break;
                case 3:
                    charWidthBytes[0]= 0x1D;
                    charWidthBytes[1]= 0x21;
                    charWidthBytes[2]= 0x30;
                    break;
            }
        }else if(adapterView.getId() == R.id.sp_char_height) {
            switch (i){
                case 0:
                    charHeightBytes[0]= 0x1D;
                    charHeightBytes[1]= 0x21;
                    charHeightBytes[2]= 0x00;
                    break;
                case 1:
                    charHeightBytes[0]= 0x1D;
                    charHeightBytes[1]= 0x21;
                    charHeightBytes[2]= 0x01;
                    break;
                case 2:
                    charHeightBytes[0]= 0x1D;
                    charHeightBytes[1]= 0x21;
                    charHeightBytes[2]= 0x02;
                    break;
                case 3:
                    charHeightBytes[0]= 0x1D;
                    charHeightBytes[1]= 0x21;
                    charHeightBytes[2]= 0x03;
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * onCheckedChanged
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.getId() == R.id.cb_text_bold) {
            if(b){
                charBoldBytes[0]= 0x1b;
                charBoldBytes[1]= 0x45;
                charBoldBytes[2]= 0x01;
            }else {
                charBoldBytes[0]= 0x1b;
                charBoldBytes[1]= 0x45;
                charBoldBytes[2]= 0x00;
            }
        }else if(compoundButton.getId() == R.id.cb_text_underline) {
            if(b){
                charUnderlineBytes[0]= 0x1b;
                charUnderlineBytes[1]= 0x2d;
                charUnderlineBytes[2]= 0x02;
            }else {
                charUnderlineBytes[0]= 0x1b;
                charUnderlineBytes[1]= 0x2d;
                charUnderlineBytes[2]= 0x00;
            }
        }else if(compoundButton.getId() == R.id.cb_text_reverse) {
            if(b){
                charReverseBytes[0]= 0x1D;
                charReverseBytes[1]= 0x42;
                charReverseBytes[2]= 0x01;
            }else {
                charReverseBytes[0]= 0x1D;
                charReverseBytes[1]= 0x42;
                charReverseBytes[2]= 0x00;
            }
        }
    }
}
