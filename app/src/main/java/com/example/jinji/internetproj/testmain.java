package com.example.jinji.internetproj;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class testmain extends AppCompatActivity {

    static
    {
        System.loadLibrary("jniExample");
    }
    public native int LEDControl(int value);

    final static int LED[] = {0x01,0x02,0x04,0x08,0x10,0x20,0x40,0x80};
    int LedData;
    TextView score_tv, time_tv, notify, stage_num;
    Timer timer;
    Button button[] = new Button[9];
    int data[] = new int[12];
    int stage;
    int score = 0;
    private Dialog dialog;
    int index = 0;
    int i;
    int j;
    int num;
    Intent intent;
    //SoundPool sound = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testpage_main);
        intent = getIntent();
        stage = intent.getIntExtra("next", 1);
        score = intent.getIntExtra("score", 0);
        LedData = intent.getIntExtra("led_data", 0);
        score_tv = (TextView) findViewById(R.id.score_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);
        notify = (TextView) findViewById(R.id.notify);
        stage_num = (TextView) findViewById(R.id.stage_num);
        //stage_num.setText(stage);
        j = 0;
        num = 0;
        // 시간을 측정할 쓰레드 시작
        //  TimeCountThread timeCountThread = new TimeCountThread();
        // timeCountThread.start();

        button[0] = (Button) findViewById(R.id.button00);
        button[1] = (Button) findViewById(R.id.button01);
        button[2] = (Button) findViewById(R.id.button02);
        button[3] = (Button) findViewById(R.id.button03);
        button[4] = (Button) findViewById(R.id.button04);
        button[5] = (Button) findViewById(R.id.button05);
        button[6] = (Button) findViewById(R.id.button06);
        button[7] = (Button) findViewById(R.id.button07);
        button[8] = (Button) findViewById(R.id.button08);
        score_tv.setText("" + score);
        LEDControl(LedData);
        setRandomItem();

        // 무작위로 아이템을 배열한다
        // 아이템이 나올 위치를 0에서 8까지 총 9개를 랜덤으로 정하기

    }

    public void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //먼저 다이어로그를 build를 통해 만들어낸다.
        View chooseLayout = View.inflate(this, R.layout.dialog, null);//어떤 layout을 다이어로그에 띄울것인지 정해준다.
        TextView text = chooseLayout.findViewById(R.id.text_part);
        text.setText(str);
        builder.setView(chooseLayout);//현재 빌더에 우리가만든 다이어로그 레이아웃뷰를 추가해준다.
        dialog = builder.create(); //지금까지 만든 builder를 생성하고 띄어준다.
        dialog.show();
    }

    public void Next(View v) {
        ++stage;
        if (stage < 4) {
            Intent i = new Intent(testmain.this, testmain.class);
            i.putExtra("next", stage);
            i.putExtra("score",score);
            i.putExtra("led_data", LedData);
            startActivity(i);
        } else {
            Intent i = new Intent(testmain.this, appmain.class);
            startActivity(i);
        }
        // finish();
    }

    public void onButtonClick1(View v) {
        if (data[num] != 0) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick2(View v) {
        if (data[num] != 1) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick3(View v) {
        if (data[num] != 2) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick4(View v) {
        if (data[num] != 3) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick5(View v) {
        if (data[num] != 4) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick6(View v) {
        if (data[num] != 5) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick7(View v) {
        if (data[num] != 6) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick8(View v) {
        if (data[num] != 7) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void onButtonClick9(View v) {
        if (data[num] != 8) {
            showDialog("false");
            timer.cancel();
        }
        num++;
        score = score + 10 * stage;
    }

    public void setRandomItem() {
        // 위치 정보 초기화
        for (i = 0; i < 12; i++) {
            data[i] = 0;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (j >= (stage * 3)) {
                    button[1].post(new Runnable() {
                        public void run() {
                            reset();
                            notify.setText("Enter button");
                            score_tv.setText(""+score);
                            if (num == (stage * 3)) {
                                LedData |= LED[stage-1];
                                LEDControl(LedData);
                                showDialog("Complete");
                                timer.cancel();
                            }
                        }
                    });

                    //timer.cancel();
                } else {
                    Random rand = new Random();
                    index = rand.nextInt(9);
                    if (j > 0)
                        if (index == data[j - 1])
                            if (index == 9)
                                index = index - 1;
                            else
                                index = index + 1;
                    data[j] = index;
                    button[index].post(new Runnable() {
                        public void run() {
                            if (j > 0)
                                button[data[j - 1]].setBackgroundResource(R.drawable.button);
                            button[index].setBackgroundColor(0xFFFF0000);
                            j++;
                        }
                    });
                }
            }
        }, 500, 500);
    }

    public void reset() {
        for (i = 0; i < 9; i++) {
            button[i].setBackgroundResource(R.drawable.button);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Button button;
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                button = (Button) findViewById(R.id.button00);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;

            case KeyEvent.KEYCODE_2:
                button = (Button) findViewById(R.id.button01);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;

            case KeyEvent.KEYCODE_3:
                button = (Button) findViewById(R.id.button02);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;

            case KeyEvent.KEYCODE_4:
                button = (Button) findViewById(R.id.button03);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;
            case KeyEvent.KEYCODE_5:
                button = (Button) findViewById(R.id.button04);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;
            case KeyEvent.KEYCODE_6:
                button = (Button) findViewById(R.id.button05);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;
            case KeyEvent.KEYCODE_7:
                button = (Button) findViewById(R.id.button06);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;
            case KeyEvent.KEYCODE_8:
                button = (Button) findViewById(R.id.button07);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;
            case KeyEvent.KEYCODE_9:
                button = (Button) findViewById(R.id.button08);
                button.performClick();
                button.setBackgroundColor(0xFFFF0000);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}


