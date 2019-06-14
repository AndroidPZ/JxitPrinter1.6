package com.jxit.jxitprinter1_6.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jxit.jxitprinter1_6.R;
import com.jxit.jxitprinter1_6.activity.SettingActivity;

public class FragmentDemo extends Fragment {
    public ListView mListView;

    private String [] titles={"打印文本","打印图片","打印一维条码","打印二维条码","打印曲线",
            "打印表格","控制命令","打印餐饮账单","打印巡查结果","打印货品清单","打印彩票单据"};
    int [] resIdsf={R.drawable.text_24,R.drawable.photo_24,R.drawable.barcode_24,
            R.drawable.barcode_2d_24,R.drawable.curve_24,R.drawable.table_24,
            R.drawable.settings_24,R.drawable.billing_24,R.drawable.order_24,R.drawable.invoice_24,R.drawable.lottery24};
    int [] resIdse={R.drawable.arrow_carrot_right_24,R.drawable.arrow_carrot_right_24,R.drawable.arrow_carrot_right_24,
            R.drawable.arrow_carrot_right_24,R.drawable.bullet_grey_1,R.drawable.bullet_grey_1,
            R.drawable.bullet_grey_1,R.drawable.bullet_grey_1,R.drawable.bullet_grey_1,R.drawable.bullet_grey_1,R.drawable.bullet_grey_1};

    private FragmentInteractionDemo listenerDemo;

    public interface FragmentInteractionDemo {
        void processDemo(String str);
    }

    /**
     * onAttach
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentInteractionDemo) {
            listenerDemo = (FragmentInteractionDemo) activity;
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    /**
     * onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    /**
     * onActivityCreated
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)    {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        mListView = (ListView) getActivity().findViewById(R.id.lv_demo);
        mListView.setAdapter(new ListViewAdapter(resIdsf,titles,resIdse));
        mListView.setOnItemClickListener(mDeviceClickListener);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    /**
     * ListViewAdapter
     */
    public class ListViewAdapter extends BaseAdapter {
        View [] itemViews;
        ListViewAdapter(int[] itemImageResf, String[] itemTexts, int[] itemImageRese){
            itemViews = new View[itemImageResf.length];
            for (int i=0; i<itemViews.length; ++i){
                itemViews[i] = makeItemView(itemImageResf[i], itemTexts[i], itemImageRese[i]);
            }
        }
        public int getCount()  {return itemViews.length;}
        public View getItem(int position)  {return itemViews[position];}
        public long getItemId(int position) {return position;}
        private View makeItemView(int resIdf, String strText, int resIde) {
            final String tit = strText;
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.listview_item, null);
            ImageView imagef = (ImageView)itemView.findViewById(R.id.lvitem_image_first);
            imagef.setImageResource(resIdf);
             TextView title = (TextView)itemView.findViewById(R.id.lvitem_textview);
            title.setText(strText);
            final ImageView imagee = (ImageView)itemView.findViewById(R.id.lvitem_image_end);
            imagee.setImageResource(resIde);
            imagee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (tit){
                        case "打印文本":
                            listenerDemo.processDemo("@选择文字打印");
                            break;
                        case "打印图片":
                            listenerDemo.processDemo("@选择图片打印");
                            break;
                        case "打印一维条码":
                            listenerDemo.processDemo("@选择一维码打印");
                            break;
                        case "打印二维条码":
                            listenerDemo.processDemo("@选择二维码打印");
                            break;

                        default:
                            break;
                    }

                }
            });
            return itemView;
        }
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                return itemViews[position];
            }

            return convertView;
        }
    }

    /**
     * mDeviceClickListener
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String cmd = "@"+titles[arg2];
            listenerDemo.processDemo(cmd);
        }
    };

    /**
     * onDetach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        listenerDemo = null;
    }


}
