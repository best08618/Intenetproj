package com.example.jinji.internetproj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//java socket 통신함수
public class ShowIpPort extends Activity {
    //activity에 보여주기 위한 변수
    private EditText et1, et2, et3;
    private TextView tv4, tv5;

    //자바 소켓통신을 위한 함수
    private Socket socket;
    private DataOutputStream writeSocket;
    private DataInputStream readSocket;
    private Handler mHandler = new Handler();
    String ip = null;
    int port = 0;

    //ip를 이용 접속 관리변수
    private ConnectivityManager cManager;
    private NetworkInfo wifi;
    private ServerSocket serverSocket;

    //timer 제어변수
    int count;
    Timer timer_sound;

    //string to integer 변환 변수
    int foo;

    //jni library load
    static {
        System.loadLibrary("soundExample");
    }

    //jni 함수 -> piezo부분을 제어하는 c코드 사용
    public native int sPiezo(int value);

    //piezo 주파수 변수
    int PiezoData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socketconnect);

        //처음 소리가 나지 않도록 0으로 초기화
        sPiezo(PiezoData);

        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);

        //*****인터넷 연결 및 정보송수신을 위해 manifest파일에 permission관련 부분 추가 필요****//
        // 시스템서비스 할당 _wifi이용해서 연결하기 위한 설정
        cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //사용 방법 print
        tv5.setText("\nSERVER \n1.write portnum\n2.click set server\n3.click view info\n\nCLIENT\n1.write portnum\n2.ip from server and click connect");
    }

    //각 버튼 event정의
    @SuppressWarnings("deprecation")
    public void OnClick(View v) throws Exception {
        switch (v.getId()) {
            case R.id.button1:
                (new Connect()).start();
                break;
            case R.id.button2:
                (new Disconnect()).start();
                break;
            case R.id.button3:
                (new SetServer()).start();
                break;
            case R.id.button4:
                (new CloseServer()).start();
                break;
            case R.id.button5:
                wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifi.isConnected()) {
                    WifiManager wManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wManager.getConnectionInfo();
                    tv4.setText("IP Address : " + Formatter.formatIpAddress(info.getIpAddress()));
                } else {
                    tv4.setText("Disconnected");
                }
                break;
            case R.id.button6:
                (new sendMessage()).start();
                break;
        }
    }

    //client함수 - server의 ip주소와 port를 가지고 socket통신 open
    class Connect extends Thread {
        public void run() {
            Log.d("Connect", "Run Connect");
            try {
                //activity에서 edittext된 부분을 읽어와서 변수에 저장
                ip = et1.getText().toString();
                port = Integer.parseInt(et2.getText().toString());
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
                //위에서 받아온 ip와 port이용
                socket = new Socket(ip, port);
                //소켓 버퍼 부분 정의
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast("연결에 성공하였습니다.");
                    }

                });
                //연결에 성공시 piano화면으로 전환
                Intent intent = new Intent(getApplicationContext(), piano.class);
                intent.putExtra("ip", ip);
                intent.putExtra("port", port);
                startActivity(intent);
                (new recvSocket()).start();
            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
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

    //client함수 - 연결된 소켓을 close
    class Disconnect extends Thread {
        public void run() {
            try {
                //socket이 끈어진 경우
                if (socket != null) {
                    socket.close();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setToast("연결이 종료되었습니다.");
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
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

    //server함수 - 지정받은 port번호
    class SetServer extends Thread {
        public void run() {
            try {
                //port번호를 user로 부터 받기
                int port = Integer.parseInt(et2.getText().toString());
                //port번호로 socket open
                serverSocket = new ServerSocket(port);
                final String result = "서버 포트 " + port + " 가 준비되었습니다.";
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(result);
                    }
                });

                //연결된 socket으로 초기화
                socket = serverSocket.accept();
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                //계속 listen하며 inputstream으로 값이 오는 경우 print
                while (true) {
                    byte[] b = new byte[100];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    final String recvInput = input.trim();
                    if (ac == -1)
                        break;
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
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
                serverSocket.close();
                socket.close();
            } catch (Exception e) {
                final String recvInput = "서버 준비에 실패하였습니다.";
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

    //client함수 - server로 부터 온 값을 받아서 전달
    class recvSocket extends Thread {
        public void run() {
            try {
                readSocket = new DataInputStream(socket.getInputStream());
                //계속 listen하며 server로 부터 값이 오는 경우 그 값에 맞추어 소리 재생
                while (true) {
                    byte[] b = new byte[100];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    final String recvInput = input.trim();
                    if (ac == -1)
                        break;
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //string으로 들어온 값을 interger로 변환
                            foo = Integer.parseInt(recvInput);
                            //들어온 값에 맞게 변환하여 소리재생(문제)
                            if (foo == 1)
                                sound(0x01);
                            if (foo == 2)
                                sound(0x02);
                            //소리를 재생하며 피아노 화면으로 전환 -> socket연결에 필요한 모든 함수 다시 send
                            Intent intent = new Intent(getApplicationContext(), piano.class);
                            intent.putExtra("foo", foo);
                            intent.putExtra("ip", ip);
                            intent.putExtra("port", port);
                            startActivity(intent);
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

    //server함수 - socket연결 close
    class CloseServer extends Thread {
        public void run() {
            try {
                //server측 socket이 닫힌 경우
                if (serverSocket != null) {
                    serverSocket.close();
                    socket.close();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setToast("서버가 종료되었습니다..");
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "서버 준비에 실패하였습니다.";
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

    //client, server함수 - 메세지 전달
    class sendMessage extends Thread {
        public void run() {
            try {
                //보낼 메세지를 random으로 generate
                byte[] b = new byte[100];
                Random rand = new Random();
                String a = "" + rand.nextInt(4);
                b = a.getBytes();
                //소켓을 통해 전달
                writeSocket.write(b);
            } catch (Exception e) {
                final String recvInput = "메시지 전송에 실패하였습니다.";
                Log.d("SetServer", e.getMessage());
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

    //토스트 함수 정의
    void setToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    //일정시간 동안 소리 재생함수
    public void sound(final int value) {
        count = 0;
        //timer를 이용하여 일정시간동안 재생할 수 있도록 설정
        timer_sound = new Timer();
        //count가 5이상인 경우
        timer_sound.schedule(new TimerTask() {
            @Override
            public void run() {
                if (count > 5) {
                    //소리 재생 및 타이머 종료
                    sPiezo(0x00);
                    timer_sound.cancel();
                } else {
                    count++;
                    //주어진 값으로 소리 재생
                    sPiezo(value);
                }
            }
        }, 100, 100);
    }
}