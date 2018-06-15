package com.example.jinji.internetproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.content.res.Resources;

public class AdapterActivity extends AppCompatActivity{
    ListView listView1;
    IconTextListAdapter adapter;
    Button button;
    int score;
    Intent intent;

    //최종점수 저장변수
    int imm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board);

        //리스트 뷰의 아이디 값을 찾아서 불러온 후 변수에 담아준다
        listView1 = (ListView)findViewById(R.id.listView);
        button = (Button)findViewById(R.id.home);

        //intent함수를 통해 최종점수 전달
        intent = getIntent();

        //설정한 어뎁터와 연결
        adapter = new IconTextListAdapter(this);

        //이전의 데이터 string 2차원 배열에 날짜 점수 순으로 차례로 저장
        String[][] data = new String[3][2];
        data[0][0] = "2018.05.06";
        data[0][1] = "2500";
        data[1][0] = "2018.06.14";

        //마지막 스테이지 이후 intent를 통해 최종점수 받아오기
        imm = intent.getIntExtra("final_score", 0);
        data[1][1] = ""+imm;
        data[2][0] = "";

        //등수 int to string변환 변수
        String s;

        //저장된 값을 차례대로 listview에 뿌려준다
        for(int i = 0; !data[i][0].equals(""); i++){
            s = i + "";
            adapter.addItem(new IconTextItem(s, data[i][0], data[i][1]));
        }

        //home버튼 클릭시 메인activiry로 전환
        listView1.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),appmain.class);
                startActivity(intent);
            }
        });
    }
}