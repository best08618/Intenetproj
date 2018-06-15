package com.example.jinji.internetproj;

import android.content.Context;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;


import java.util.ArrayList;

import java.util.List;

//데이터를 최종적으로 보여주기 위한 연결고리
public class IconTextListAdapter extends BaseAdapter {
    private Context mContext;
    private List<IconTextItem> mItems = new ArrayList<IconTextItem>();

    public IconTextListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    //item 추가
    public void addItem(IconTextItem it) {
        mItems.add(it);
    }

    //item 사이즈 받기
    @Override
    public int getCount() {
        return mItems.size();
    }

    //위치 지정
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    //아이템 위치 지정
    @Override
    public long getItemId(int position) {
        return position;
    }


    // 화면 구성하는 getView
    // convertView가 null이 아니면 뷰는 재활용하고 안의 데이터만 바꿔주어 퍼포먼스가 향상 된다
    // null인 경우에는 새로 객체 생성해준다
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IconTextView itemView;
        if (convertView == null) {
            itemView = new IconTextView(mContext, mItems.get(position));
        } else {
            itemView = (IconTextView) convertView;
            itemView.setText(0, mItems.get(position).getmData(0));
            itemView.setText(1, mItems.get(position).getmData(1));
            itemView.setText(2, mItems.get(position).getmData(2));
        }
        return itemView;
    }
}