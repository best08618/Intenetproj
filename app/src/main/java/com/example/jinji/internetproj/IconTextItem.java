package com.example.jinji.internetproj;

//값을 저장해줄 공간의 틀 짜는 클래스
public class IconTextItem {
    private String[] mData;
    private boolean mSelectable = true;

    public IconTextItem(String obj1, String obj2,String obj3){
        mData = new String[3];
        mData[0] = obj1;
        mData[1] = obj2;
        mData[2] = obj3;
    }
    public boolean isSelectable(){
        return mSelectable;
    }
    public boolean isSelectable(boolean selectable){
        return selectable;
    }

    public String[] getmData() {
        return mData;
    }

    public String getmData(int index){
        if(mData == null || index >=mData.length){
            return null;
        }
        return mData[index];
    }
    public void setmData(String[] obj){
        mData = obj;
    }
}