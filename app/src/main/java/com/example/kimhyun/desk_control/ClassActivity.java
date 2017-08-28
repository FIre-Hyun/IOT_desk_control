package com.example.kimhyun.desk_control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClassActivity extends AppCompatActivity implements View.OnClickListener{

    Button [] btn = new Button[20];

    Button [] test_btn = new Button[20];

    TextView tvHtml;


    private String ip = "192.168.0.32"; // IP
    private int port = 3000; // PORT번호

    String Html;

    String student_num;

    String desk_kind;

    String [] desk = new String[20];

    int desk_num = 0;

    RadioButton radio_desk, radio_chair;

    RadioGroup radiogroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        btn[0] = (Button) findViewById(R.id.btn1);
        btn[1] = (Button) findViewById(R.id.btn2);
        btn[2] = (Button) findViewById(R.id.btn3);
        btn[3] = (Button) findViewById(R.id.btn4);

        btn[0].setOnClickListener(this);
        btn[1].setOnClickListener(this);
        btn[2].setOnClickListener(this);
        btn[3].setOnClickListener(this);


        test_btn[0] = (Button) findViewById(R.id.test0);
        test_btn[1] = (Button) findViewById(R.id.test1);
        test_btn[2] = (Button) findViewById(R.id.test2);
        test_btn[3] = (Button) findViewById(R.id.test3);
        test_btn[4] = (Button) findViewById(R.id.test4);
        test_btn[5] = (Button) findViewById(R.id.test5);
        test_btn[6] = (Button) findViewById(R.id.test6);
        test_btn[7] = (Button) findViewById(R.id.test7);
        test_btn[8] = (Button) findViewById(R.id.test8);
        test_btn[9] = (Button) findViewById(R.id.test9);
        test_btn[10] = (Button) findViewById(R.id.test10);
        test_btn[11] = (Button) findViewById(R.id.test11);

        test_btn[0].setOnClickListener(this);
        test_btn[1].setOnClickListener(this);
        test_btn[2].setOnClickListener(this);
        test_btn[3].setOnClickListener(this);
        test_btn[4].setOnClickListener(this);
        test_btn[5].setOnClickListener(this);
        test_btn[6].setOnClickListener(this);
        test_btn[7].setOnClickListener(this);
        test_btn[8].setOnClickListener(this);
        test_btn[9].setOnClickListener(this);
        test_btn[10].setOnClickListener(this);
        test_btn[11].setOnClickListener(this);

        radio_desk = (RadioButton) findViewById(R.id.radio_desk);
        radio_chair = (RadioButton) findViewById(R.id.radio_chair);

        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);

        desk_kind  = "a";

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.radio_desk)
                    desk_kind = "a";
                else
                    desk_kind = "b";
            }
        });

        Intent intent = getIntent();
        student_num = intent.getStringExtra("number").toString();

        Log.d("start", "start");

        // Thread로 웹서버에 접속
        new Thread() {
            public void run() {
                Log.d("Thread", "Thread");
                Html = getHtml();

                Bundle bun = new Bundle();
                bun.putString("HTML", Html);
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();

        tvHtml = (TextView)this.findViewById(R.id.tv_html);
    }


    private String getHtml(){
        Html = "";

        URL url =null;

        HttpURLConnection http = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try{
            url = new URL("http://" + ip + ":" + port);
            http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(3*1000);
            http.setReadTimeout(3*1000);

            Log.d("try", "try");
            isr = new InputStreamReader(http.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                Html += str + "\n";
                Log.d("html", Html);
            }


            showResult();

        }catch(Exception e){
            Log.e("Exception", e.toString());
        }finally{
            if(http != null){
                try{http.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(br != null){
                try{br.close();}catch(Exception e){}
            }
        }

        return Html;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String Html = bun.getString("HTML");
            tvHtml.setText(Html);
        }
    };


    private void showResult() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for(int i = 0; i < 4; i++) {
                            desk[i] = String.valueOf(Html.charAt(i+11));
                            Log.d("setButton" + i, desk[i]);
                            if (desk[i].equals("c")) {  //  비어있음
                                btn[i].setText("Empty");
                                btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                            }
                            else if(desk[i].equals("b")){   // 의자 On
                                btn[i].setText(student_num);
                                btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
                            }
                            else if(desk[i].equals("a")){
                                btn[i].setText(student_num);
                                btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorAccent));
                            }
                            else{
                                btn[i].setText("Error");
                                btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));

                            }
                        }

                        for(int i = 0; i < 12; i++){
                            test_btn[i].setText("Empty");
                            test_btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        }

                        desk[4] = "d";

                        Log.d("desk", desk[0]+desk[1]+desk[2]+desk[3] + desk[4]);

                    }
                });
            }
        }).start();


    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.btn1:
                desk_num = 0;
                break;
            case R.id.btn2:
                desk_num = 1;
                break;
            case R.id.btn3:
                desk_num = 2;
                break;
            case R.id.btn4:
                desk_num = 3;
                break;
            default:
                desk_num = 4;
                break;

        }
        new Thread() {
            public void run() {

                Log.d("desk2", desk[0]+desk[1]+desk[2]+desk[3] + desk[4]);

                if(desk_num == 4){
                    test_btn[new Integer(String.valueOf(v.getId()).substring(4))].setText("Empty");
                    test_btn[new Integer(String.valueOf(v.getId()).substring(4))].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
                    Log.d("11111", String.valueOf(v.getId()).substring(4));
                }
                    else if(desk[desk_num].equals("c") && desk_num != 4) {        //추가코딩 해야함.. Empty이면 선택하는부분


                        String Clicked_Desk = null;
                        try {
                            Clicked_Desk = postdesk(desk_num, desk_kind);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Clicked", Clicked_Desk);

                        desk[desk_num] = desk_kind;
                    }

                    else if(!desk[desk_num].equals("c") && desk_num != 4){  // Empty가 아니면

                        String Clicked_Desk = null;
                        try {
                            Clicked_Desk = postdesk(desk_num, "c");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Clicked", Clicked_Desk);

                        desk[desk_num] = "c";


                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("제발", "짠");
                                Log.d("setButton" + desk_num, desk[desk_num]);
                                if (desk[desk_num].equals("c")) {
                                    btn[desk_num].setText("Empty");
                                    btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                                }
                                else if(desk[desk_num].equals("b")) {
                                    btn[desk_num].setText(student_num);
                                    btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
                                }
                                else if(desk[desk_num].equals("a")) {
                                    btn[desk_num].setText(student_num);
                                    btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorAccent));
                                }
                                else{
                                    btn[desk_num].setText("Error");
                                    btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                                }
                        }
                    });

            }
        }.start();


    }

    private String postdesk(int desk_num, String desk_kind) throws IOException {

        URL url =null;

        HttpURLConnection http = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder sb = new StringBuilder();

        try {
            url = new URL("http://" + ip + ":" + port + "/reservation");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Type", "application/json");
            //OutputStream os = con.getOutputStream();

            DataOutputStream os = new DataOutputStream(con.getOutputStream());

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("student_num", student_num);
            jsonParam.put("desk_num", desk_num);
            jsonParam.put("desk_kind", desk_kind);

            Log.d("jsonParam", jsonParam.toString());

//            os.writeBytes(URLEncoder.encode("current="+jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

//            os.writeUTF(URLEncoder.encode("current="+jsonParam.toString(), "UTF-8"));
            os.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line + '\n');
            }
            Log.d("sb", sb.toString());
//            btn[desk_num].setText("ON");

            reader.close();
            os.close();
        }catch(Exception e){
            Log.e("Exception", e.toString());
        }finally{
            if(http != null){
                try{http.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(br != null){
                try{br.close();}catch(Exception e){}
            }
        }


        return sb.toString();



    }


}
