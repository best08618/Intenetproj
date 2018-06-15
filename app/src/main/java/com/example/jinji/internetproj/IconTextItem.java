package com.example.jinji.internetproj;

//값을 저장해줄 공간의 틀 짜는 클래스
public class IconTextItem {
    private String[] mData;
    private boolean mSelectable = true;

    //위와 다르게 파라메타 값에 배열이 아닌 직접 본인이 넣을 때
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

    //메소드 getData()는 mData 값을 반환(이유는 다른 자바 클래스에서 쓸 수 있게)
    public String[] getmData() {
        return mData;
    }

    //위의 메소드 getData()는 배열로 되어있기 때문에 한 개씩 값을 가져가기 위해서는 쪼개주는 작업이 필요하다
    //현재 이 getData() 메소드는 파라메타 값에 가져오고 싶은 배열의 순번을 넣어주면 알아서 쪼개주는 역할이다
    public String getmData(int index){
        //이 구문은 getData(index) 값을 줬을 때 값을 불러올 mData에 값이 있는지 혹은 index 값이 mData가 가지고 있는 값을 초과했는지를 판별해주는 조건문
        if(mData == null || index >=mData.length){
            return null;
        }
        return mData[index];
    }
    public void setmData(String[] obj){
        mData = obj;
    }
}