package com.example.jinji.internetproj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class testcode extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testpage1);  // test에 대해 설명해주는 page임
        Button gotit = (Button) findViewById(R.id.gotbutton); // got it 버튼을 누르면 testmain으로 넘어가게 함
        gotit.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         Intent intent = new Intent(getApplicationContext(),testmain.class);
                                         intent.putExtra("stage",1);
                                         startActivity(intent);
                                     }
                                 }
        );
    }

}
