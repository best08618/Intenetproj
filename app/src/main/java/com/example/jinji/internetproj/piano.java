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

public class piano extends Activity implements View.OnTouchListener{

    static {
        System.loadLibrary("jnipiezo");
    }
    public native int PiezoControl(int value);
    int PiezoData;
    static ImageButton[] white;
    static ImageButton[] black;
    public int[] answer = new int[6];
    public int answer_index = 0;
    private Socket socket;
    private DataOutputStream writeSocket;
    private DataInputStream readSocket;
    private Handler mHandler = new Handler();

    private ConnectivityManager cManager;
    private NetworkInfo wifi;
    private ServerSocket serverSocket;
    String ip = "";
    int port = 0;
    int foo=0;
    Timer timer_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.piano);
        (new Connect()).start();

        PiezoData = 0;
        PiezoControl(PiezoData);
        Intent  intent = getIntent();
        ip = intent.getStringExtra("ip");
        port = intent.getIntExtra("port", -1);
        foo = intent.getIntExtra("foo",  -1);

        white = new ImageButton[7];
        black = new ImageButton[5];

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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        boolean bret = false;
        if (view instanceof ImageButton) {
            ImageButton imgBtn = (ImageButton) view;
            bret = pianoKeyHandle(imgBtn, action);
        }
        return bret;
    }

    private boolean pianoKeyHandle(ImageButton imgBtn, int action) {
        boolean bret = false;
        Object obj = imgBtn.getTag();
        if (obj != null) {
            if (obj instanceof int[]) {
                int[] tag = (int[]) obj;
                //answer[answer_index++/2] = tag[0];
                if(foo == tag[0])
                    setToast(" good!!bb");
                else{
                    setToast("NNOOOOOOOㅠㅠ");
                }
                Log.e(this.getClass().getName(), ip+port);
                (new sendMessage()).start();
                if (tag.length == 3) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        PiezoControl(tag[0]);
                        imgBtn.setImageResource(tag[1]);
                    } else if (action == MotionEvent.ACTION_UP) {
                        imgBtn.setImageResource(tag[2]);
                        /*try {
                            Thread.sleep(9 * 10);
                          } catch (InterruptedException e) { }*/
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

    class sendMessage extends Thread {
        public void run() {
            try {
                Log.e(this.getClass().getName(), "sendmessage");
                byte[] b = new byte[100];
                b = "123".getBytes();
                writeSocket.write(b);
                Log.e(this.getClass().getName(), writeSocket.toString());
            } catch (Exception e) {
                final String recvInput = "메시지 전송에 실패하였습니다.";
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //setToast(recvInput);
                    }
                });
            }
        }
    }
    void setToast(String msg) {
        Toast.makeText( getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    class Connect extends Thread {
        public void run() {
            Log.d("Connect", "--Run Connect");
            String ip_s = null;
            int port_s = 0;

            try {
                //ip = et1.getText().toString();
                //port = Integer.parseInt(et2.getText().toString());
            } catch (Exception e) {
                final String recvInput = "정확히 입력하세요!";
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }
                });
            }
            try {
                socket = new Socket(ip, port);
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast("연결에 성공하였습니다_piano");
                    }
                });
                (new recvSocket()).start();
            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //setToast(recvInput);
                    }

                });

            }

        }
    }
    class recvSocket extends Thread {
        public void run() {
            try {
                readSocket = new DataInputStream(socket.getInputStream());
                while (true) {
                    byte[] b = new byte[100];
                    int ac = readSocket.read(b, 0, b.length);
                    final String input = new String(b, 0, b.length);
                    final String recvInput = input.trim();
                    if (ac == -1)
                        break;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setToast(recvInput);
                        }

                    });
                }
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast("연결이 종료되었습니다.");
                    }

                });
            } catch (Exception e) {
                final String recvInput = "연결에 문제가 발생하여 종료되었습니다..";
                Log.d("SetServer", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }

        }
    }
}