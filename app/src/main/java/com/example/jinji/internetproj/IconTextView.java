package com.example.jinji.internetproj;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

//그 공간에 어떤 값을 어디로 집어 넣어 줄지 정의하는 함수
public class IconTextView extends LinearLayout {
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    public IconTextView(Context context, IconTextItem aItem) {
        super(context);

        //해당 listview에 들어갈 디자인 레이아웃 xml 설정(만들어진 listitem.xml을 보일 디자인 레이아웃)
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

    //위에 방법과 구성은 동일한 방법이지만 조건 문과 파라메타 값으로 자동적으로 셋팅을 해준다
    //파라메타로 Adapter 자바에서 설정된 int형 index 값과 String형 데이터 값을 가져와서 위처럼 변수를 자동으로 설정한다
    public void setText(int index, String data) {
        if (index == 0) {
            tv1.setText(data);
        } else if (index == 1) {
            tv2.setText(data);
        } else if (index == 2) {
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