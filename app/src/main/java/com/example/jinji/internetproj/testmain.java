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
 // connect with jni library
    static
    {
        System.loadLibrary("jniExample");
    }
//functions from jni
    public native int LEDControl(int value);        //LED hardware를 변경하는 함수이다
    public native int soundControl(int val);        // piezo hardware 값을 변경하는 함수이다. (사운드를 출력함)

    //TextLcd 화면의 값을 변경하는 함수이다.
    public native int TextLCDOut(String str, String str2);
    public native int IOCtlClear();
    public native int IOCtlReturnHome();
    public native int IOCtlDisplay(boolean bOn);
    public native int IOCtlCursor(boolean bOn);
    public native int IOCtlBlink(boolean bOn);

    final static int LED[] = {0x01,0x02,0x04,0x08,0x10,0x20,0x40,0x80}; // Set LED Register values as an array
    final int SOUND = 0x06; // 문제에서 버튼이 하나씩 변화 될 때 마다 소리가 남 (음계 : 라 )
    int LedData; // LED 바꿀 때 저장할 변수이다.
    TextView score_tv, notify, stage_num;
    Timer timer; // TIMER을 이용하여 일정 주기 마다 버튼의 색을 바뀌게 하고, 소리를 내도록 함.
    Timer timer_led;
    Button button[] = new Button[9];
    int data[] = new int[12]; // random 하게 발생한 문제 데이터 전체를 저장하는 배열
    int stage; // 현재 stage 값을 저장하는 변수
    int score = 0;    // 현재 score를 저장하는 변수
    private Dialog dialog;
    int index = 0;   // random 하게 발생한 변수를 저장하기 위한 변수로, 이후 BUTTON 배열을 조절하는 변수임

    //LOOP에서 비교를 위해 사용되는 변수들임
    int i;
    int j;
    int num;
    int led_num;

    //variables for textLCD
    int text;
    boolean disp,cursor,blink;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testpage_main);

        intent = getIntent(); // testmain 함수는 stage 별로 같은 코드가 반복되기 때문에 intent 를 통해 값을 넘겨 줘야 함.
        stage = intent.getIntExtra("next", 1); //현재 stage 정보를 이전 intent 를 통해 받아옴, default = 1
        score = intent.getIntExtra("score", 0); //이전 intent 까지 얻었던 점수를 받아옴
        LedData = intent.getIntExtra("led_data", 0); // LED 상태를 다음 STAGE에서도 유지해야 하기 때문에 받아옴

        score_tv = (TextView) findViewById(R.id.score_tv);
        notify = (TextView) findViewById(R.id.notify);
        stage_num = (TextView) findViewById(R.id.stage_num);
        //stage_num.setText(stage);
        //변수 초기화
        j = 0;
        num = 0;
        disp = true; cursor = false; blink = false;

        // TextLcd 화면을 초기화하고 설정해주는 함수이다.
        IOCtlClear();
        IOCtlReturnHome();
        IOCtlDisplay(disp);
        IOCtlCursor(cursor);
        IOCtlBlink(blink);

        text = TextLCDOut("    Hello    ", "  Brain Game!  "); //textLcd에 메세지를 띄어줌 (두 줄로 띄어주기 때문에 두 개의 string입력)

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

        LEDControl(LedData); // 이전 stage 별로 정해진 LED DATA를 가지고 LED 설정해준다.
        soundControl(0); // sound 를 초기화 하여 아무 소리도 나지 않게 한다.
        setRandomItem(); // 무작위로 아이템을 설정하고 사용자의 입력을 받는 함수등을 처리하는 함수이다.

    }

    //Dialog를 띄어주는 함수이다. --> 잘 못 눌렀을 경우, 혹은 모두 다 맞았을 경우 띄어짐
    public void showDialog(String str) {
        soundControl(0); // 소리를 안나게 만들어줌
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //먼저 다이어로그를 build를 통해 만들어낸다.
        View chooseLayout = View.inflate(this, R.layout.dialog, null);//어떤 layout을 다이어로그에 띄울것인지 정해준다.
        TextView text = chooseLayout.findViewById(R.id.text_part);
        text.setText(str); // fail , complete 를 구분하기 위해 입력값으로 받은 str 를 띄어줌ㅈ
        builder.setView(chooseLayout);//현재 빌더에 우리가만든 다이어로그 레이아웃뷰를 추가해준다.
        dialog = builder.create(); //지금까지 만든 builder를 생성하고 띄어준다.
        dialog.show();
    }

    //다음 stage로 넘어갈때 dialog에서 next 가 눌릴 경우 실행되는 함수이다.
    public void Next(View v) {
        ++stage; // 다음으로 넘어가기 전에 stage값을 하나 증가 시켜준다.
        if (stage <= 8) { // stage가 8 보다 작을 경우 --> 아직 더 게임이 남았을 경우
            Intent i = new Intent(testmain.this, testmain.class); // 현재 class를 한번더 열어준다. (같은 게임 실행)
            //putExtra를 통해 현재 변수들의 값들을 intent 에게 넘겨준다.
            i.putExtra("next", stage);
            i.putExtra("score",score);
            i.putExtra("led_data", LedData);
            startActivity(i);
        } else { // 8단계까지 끝났을경우
            Intent i = new Intent(getApplicationContext(),AdapterActivity.class); // 점수를 보여주는 intent 로 넘어감
            i.putExtra("final_score",score);
            text = TextLCDOut("  !FINISH!  ", "      "); // 끝났음을 LCD로 보여준다.
            rainbowLED(); // LED를 처음부터 끝까지 한번씩 다 키는 함수이다.
            startActivity(i);
        }
        // finish();
    }

    //9개의 버튼에 ONCLICK 함수로 모두 같은 기능을 수행함, (BUTTON1 만 설명)
    public void onButtonClick1(View v) {
        if (data[num] != 0) { //Random 으로 발생된 변수 (data[num]) 가 0 이 아닐때 첫번째 버튼을 누른 경우
            showDialog("false"); // 틀렸다는 dialog를 띄어줌
            timer.cancel();             //timer 를 중지 시킴  --> 타이머는 사용자에게 버튼을 보여주고 , 사용자가 하나씩 누를때작동되는것으로
                                        // 만약 틀렸을 경우는 다른 intent 로 바로 이동해야 하기 때문에 설정된 timer 를 즉시 꺼준다
        }
        num++;                          // 맞춘 경우에는 num을 하나씩 증가한다, num은 버튼을 누른 횟수를 의미함.
        score = score + 10 * stage; // 점수를 stage 별로 계산해준다.
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

    ///////////////////////////////////////////////////
    //LED를 처음부터 끝까지 하나씩 연속으로 키기 위한 함수이다.
    public void rainbowLED(){
            timer_led = new Timer(); // LED,SOUND와 같은 경우에는 순차적인 함수가 아닌, TIMER를 통해 조절해준다.
            led_num= 0;
        timer_led.schedule(new TimerTask() {
            @Override
            public void run() {
                if (led_num < 8) { // led_num을 하나씩 증가시켜서 0번부터 7번까지의 led를 모두 한번씩 켠다.
                    button[1].post(new Runnable() {
                        public void run() {
                                LEDControl(LED[led_num]);
                                led_num ++ ;
                            }
                    });

                    //timer.cancel();
                } else { // 8번부터
                    LEDControl(0); //LED 꺼준다.
                    IOCtlClear(); // LCD화면을 초기화한다
                    soundControl(0); // Sound 를 초기화한다
                    //초기화를 하는 이유는 현재 함수가 실행되는 것이 모든 stage를 마쳤을 때이기 때문이다.
                    timer_led.cancel(); // timer 를 중지 시킨다.
                }
            }
        }, 100, 100); //0.1초마다 LED값을 하나씩 변화시킴
    }


    //Random으로 문제를 내고, 사용자의 버튼 입력값을 받아 처리하는 함수이다.
    public void setRandomItem() {
        // 데이터 정보 초기화
        for (i = 0; i < 12; i++) {
            data[i] = 0;
        }
        //textview의 주기적인 변화, button값의 주기적인 변화 등이 필요하기 때문에 timer를 사용해야한다.
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                text = TextLCDOut("  NOW ON STAGE  ", "       "+stage); // 현재 stage를 lcd에 찍어준다.
                if (j >= (stage + 2)) { // j는 timer count변수이다. 만약 stage+2 만큼 count가 같아지는 이후에는 다음을 실행한다 --> 사용자가 눌러야함
                    button[1].post(new Runnable() {
                        public void run() {
                            reset(); // reset함수를 통해 버튼들을 초기화면으로 만들어준다.
                            soundControl(0); // 소리를 내지 않는다
                            notify.setText("Enter button"); // 사용자에게 버튼을 누르라는 text를 띄어준다.
                            score_tv.setText(""+score); //점수를 띄어준다.
                            if (num == (stage + 2)) { // 만약 현재 STAGE문제 수 만큼 맞췄다면
                                LedData |= LED[stage-1]; //현재 STAGE에 맞는 LED DATA를 올려준다. OR 을 통해 이전 상태 유지 + 업데아트 해줌
                                LEDControl(LedData); // led 값 바꾼다.
                                showDialog("Complete"); // dialog에 complete라는 메세지를 띄어준다.
                                soundControl(0); // 소리를 초기화해준다.
                                timer.cancel(); // 다른 intent 로 넘어가야 하므로 타이머 종료시킨다.
                            }
                        }
                    });

                    //timer.cancel();
                } else { // 임의로 문제를 제공하는 함수이다.
                    soundControl(0);
                    Random rand = new Random(); // Random class를 통해 임의의 변수를 생성해준다.
                    index = rand.nextInt(9); // 0~8까지의 임의의 변수가 생성됨.

                    //예외처리
                    if (j > 0) // 만약 j 가 0보다 큰경우 즉, 임의의 변수가 1개 이상 생성된 경우
                        if (index == data[j - 1]) // 이전 임의의 변수 값과 현재 값을 비교해준다.--> 동일한 경우에는 2번보여준것으로 사용자가 인식하기 어려움
                            if (index == 8)  //현재 값이 8인경우 더이상 증가시키면 안되기 때문에
                                index = index - 1; //-1을 해주어, 이전과 겹치지 않게 해준다.
                            else
                                index = index + 1; // 아닌경우는 +1을 해주어 이전과 겹치지 않게 해준다.

                    data[j] = index; // 현재 random 변수 값을 data 배열에 저장한다.
                    button[index].post(new Runnable() {
                        public void run() {
                            if (j > 0) //만약 임의의변수가 1개 이상 생성된 경우
                                button[data[j - 1]].setBackgroundResource(R.drawable.button); //이전 버튼의 background를 원래대로 돌려놓는다.
                            button[index].setBackgroundColor(0xFFFF0000); // 현재 임의의 변수에 해당하는 버튼 색을 바꿔준다.
                            soundControl(SOUND); // 소리를 낸다
                            j++; // j를 증가시켜준다
                        }
                    });
                }
            }
        }, 700-(50*stage), 700-(50*stage)); // 0.7초를 기준으로 stage가 올라갈때마다 임의로 버튼값이 바뀌는시간이 빨라짐.
    }

    //버튼을 모두 초기 background로 초기화시켜줌.
    public void reset() {
        for (i = 0; i < 9; i++) {
            button[i].setBackgroundResource(R.drawable.button);
        }
    }

    //Hardware버튼이 눌렀을경우 실행되는 함수임.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Button button;
        switch (keyCode) { // hardware에서 눌린 버튼의 code에 따라 실행됨, 9개 버튼 모두 동일(1번만 설명)
            case KeyEvent.KEYCODE_1: // 1번 버튼 이 눌렸을 경우
                button = (Button) findViewById(R.id.button00); // 앱 상 xml에 있는 버튼을 찾는다.
                button.performClick(); // 앱 상의 버튼을 누른 것으로 설정한다. --> 누를 경우 buton의 onclick이 실행됨
                button.setBackgroundColor(0xFFFF0000); //버튼을 누를때에도 누른 곳 화면의 버튼 색을 변경해준다.
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


