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
    int imm;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board);

        //리스트 뷰의 아이디 값을 찾아서 불러온 후 변수에 담아준다
        listView1 = (ListView)findViewById(R.id.listView);
        button = (Button)findViewById(R.id.home);
        intent = getIntent();
        adapter = new IconTextListAdapter(this);
        Resources res = getResources();
        String[][] data = new String[3][2];
        data[0][0] = "2018.05.06";
        data[0][1] = "2500";
        data[1][0] = "2018.06.14";
        imm = intent.getIntExtra("final_score", 0);
        data[1][1] = ""+imm;
        data[2][0] = "";
        String s;
        for(int i = 0; !data[i][0].equals(""); i++){
            s = i + "";
            adapter.addItem(new IconTextItem(s, data[i][0], data[i][1]));
        }
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