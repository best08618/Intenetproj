package com.example.jinji.internetproj;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class IconTextView extends LinearLayout {
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    public IconTextView(Context context, IconTextItem aItem) {
        super(context);

        //해당 listview에 들어갈 디자인 레이아웃 xml 설정정
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item, this, true);

        //각 변수를 설정하고 그 변수가 어떤 값을 의미하는지 또, 무엇을 읽어오고 보여주는지를 설정
        tv1 = (TextView) findViewById(R.id.dataItem00);
        tv1.setText(aItem.getmData(0));

        tv2 = (TextView) findViewById(R.id.dataItem01);
        tv2.setText(aItem.getmData(1));

        tv3 = (TextView) findViewById(R.id.dataItem02);
        tv3.setText(aItem.getmData(2));
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setText(int index, String data){

        if (index == 0){

            tv1.setText(data);

        } else if (index == 1){

            tv2.setText(data);

        } else if (index == 2){

            tv3.setText(data);

        } else {

            throw new IllegalArgumentException();

        }

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}