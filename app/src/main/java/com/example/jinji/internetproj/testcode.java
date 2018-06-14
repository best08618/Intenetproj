package com.example.jinji.internetproj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class testcode extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testpage1);
        Button gotit = (Button) findViewById(R.id.gotbutton);
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
