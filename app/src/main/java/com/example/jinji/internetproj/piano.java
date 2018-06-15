package com.example.jinji.internetproj;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

//피아노 화면에서 피아노 버튼 클릭시 그에 맞는 action과 소리 재생 설정함수
public class piano extends Activity implements View.OnTouchListener{

    //소리 재생함수 library설정
    static {
        System.loadLibrary("jnipiezo");
    }
    //jni함수 설정
    public native int PiezoControl(int value);

    //소리 변수
    int PiezoData;

    //피아노 버튼
    static ImageButton[] white;
    static ImageButton[] black;

    //소켓통신을 위한 함수
    private Socket socket;
    private DataOutputStream writeSocket;
    private DataInputStream readSocket;
    private Handler mHandler = new Handler();
    String ip = "";
    int port = 0;

    //메세지 통신 string to int변환 변수
    int foo=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.piano);

        //소리 재생 초기화
        PiezoData = 0;
        PiezoControl(PiezoData);

        //intent를 넘어오면서 변수를 받아와서 비교하여 오류 여부 파악
        Intent  intent = getIntent();
        ip = intent.getStringExtra("ip");
        port = intent.getIntExtra("port", -1);
        foo = intent.getIntExtra("foo",  -1);

        //image버튼 배열로 생성
        white = new ImageButton[7];
        black = new ImageButton[5];

        //각각 눌린 경우 띄워지는 이미지를 settag함수로 설정
        white[0] = (ImageButton)findViewById(R.id.white1);
        white[0].setTag(new int[]{1, R.drawable.whiteback1, R.drawable.white1});
        white[1] = (ImageButton)findViewById(R.id.white2);
        white[1].setTag(new int[]{2, R.drawable.whiteback2, R.drawable.white2});
        white[2] = (ImageButton)findViewById(R.id.white3);
        white[2].setTag(new int[]{3, R.drawable.whiteback3, R.drawable.white3});
        white[3] = (ImageButton)findViewById(R.id.white4);
        white[3].setTag(new int[]{4, R.drawable.whiteback4, R.drawable.white4});
        white[4] = (ImageButton)findViewById(R.id.white5);
        white[4].setTag(new int[]{5, R.drawable.whiteback5, R.drawable.white5});
        white[5] = (ImageButton)findViewById(R.id.white6);
        white[5].setTag(new int[]{6, R.drawable.whiteback6, R.drawable.white6});
        white[6] = (ImageButton)findViewById(R.id.white7);
        white[6].setTag(new int[]{7, R.drawable.whiteback7, R.drawable.white7});

        black[0] = (ImageButton)findViewById(R.id.black1);
        black[0].setTag(new int[]{49, R.drawable.blackback1,R.drawable.black1});
        black[1] = (ImageButton)findViewById(R.id.black2);
        black[1].setTag(new int[]{50, R.drawable.blackback2,R.drawable.black2});
        black[2] = (ImageButton)findViewById(R.id.black3);
        black[2].setTag(new int[]{51, R.drawable.blackback3,R.drawable.black3});
        black[3] = (ImageButton)findViewById(R.id.black4);
        black[3].setTag(new int[]{52, R.drawable.blackback4,R.drawable.black4});
        black[4] = (ImageButton)findViewById(R.id.black5);
        black[4].setTag(new int[]{53, R.drawable.blackback5,R.drawable.black5});

        //터치시 동작하는 함수 지정 ->  onTouch()함수
        white[0].setOnTouchListener(this);
        white[1].setOnTouchListener(this);
        white[2].setOnTouchListener(this);
        white[3].setOnTouchListener(this);
        white[4].setOnTouchListener(this);
        white[5].setOnTouchListener(this);
        white[6].setOnTouchListener(this);

        black[0].setOnTouchListener(this);
        black[1].setOnTouchListener(this);
        black[2].setOnTouchListener(this);
        black[3].setOnTouchListener(this);
        black[4].setOnTouchListener(this);
    }

    //터치시 어느 이미지 버튼이 눌렸는지 구하여 playKeyHandle함수 호출
   public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        boolean bret = false;
        if (view instanceof ImageButton) {
            ImageButton imgBtn = (ImageButton) view;
            bret = pianoKeyHandle(imgBtn, action);
        }
        return bret;
    }

    //피아노키 처리 함수
    private boolean pianoKeyHandle(ImageButton imgBtn, int action) {
        boolean bret = false;
        Object obj = imgBtn.getTag();
        if (obj != null) {
            if (obj instanceof int[]) {
                int[] tag = (int[]) obj;
                if(foo == tag[0])
                    setToast(" good!!bb");
                else{
                    setToast("NNOOOOOOOㅠㅠ");
                }
                //버튼이 눌린경우
                if (tag.length == 3) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        //눌린 이미지 출력 및 소리 재생
                        PiezoControl(tag[0]);
                        imgBtn.setImageResource(tag[1]);
                    } else if (action == MotionEvent.ACTION_UP) {
                        //원래 이미지 출력
                        imgBtn.setImageResource(tag[2]);
                        PiezoControl(0);
                    } else if (action == MotionEvent.ACTION_MOVE){
                        imgBtn.setImageResource(tag[2]);
                        PiezoControl(0);
                    }
                }
            }
        }
        return bret;
    }

    //toast지정함수
    void setToast(String msg) {
        Toast.makeText( getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}