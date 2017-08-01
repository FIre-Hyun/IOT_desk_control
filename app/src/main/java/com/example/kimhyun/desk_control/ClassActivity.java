package com.example.kimhyun.desk_control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClassActivity extends AppCompatActivity implements View.OnClickListener{

    Button [] btn = new Button[4];

    TextView tvHtml;


    private String ip = "192.168.1.5"; // IP
    private int port = 3000; // PORT번호

    String Html;

    String student_num;

    String [] desk = new String[10];

    int desk_num = 0;

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

            Log.d("2222", "2222");

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
                            if (desk[i].equals("0"))
                                btn[i].setText("off");
                            else
                                btn[i].setText("on");
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    public void onClick(View v) {

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

        }
        new Thread() {
            public void run() {
                try {
                    String Clicked_Desk = postdesk(desk_num);
                    Log.d("Clicked", Clicked_Desk);
                    if(desk[desk_num].equals("0"))
                        desk[desk_num] = "1";
                    else
                        desk[desk_num] = "0";
//                    btn[Integer.parseInt(Clicked_Desk)].setText("ON");
                    Log.d("desk", desk[0]+desk[1]+desk[2]+desk[3]);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("제발", "짠");
                            for(int i = 0; i < 4; i++) {
                                Log.d("setButton" + i, desk[i]);
                                if (desk[i].equals("0"))
                                    btn[i].setText("off");
                                else
                                    btn[i].setText("on");
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private String postdesk(int desk_num) throws IOException {

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
