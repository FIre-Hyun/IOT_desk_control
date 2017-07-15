package com.example.kimhyun.desk_control;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ClassActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn1, btn2, btn3, btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        btn1.findViewById(R.id.btn1);
        btn2.findViewById(R.id.btn2);
        btn3.findViewById(R.id.btn3);
        btn4.findViewById(R.id.btn4);

        btn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
