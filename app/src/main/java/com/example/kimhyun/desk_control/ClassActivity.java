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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ClassActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn1, btn2, btn3, btn4;

    private String html = "";
    private Handler mHandler;

    private Socket socket;
    TextView tvNaverHtml;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    private String ip = "192.9.44.159"; // IP
    private int port = 1234; // PORT번호

    String student_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        btn1.setOnClickListener(this);

        Intent intent = getIntent();
        student_num = intent.getStringExtra("number").toString();
        Log.d("11111", student_num);

        // Thread로 웹서버에 접속
        new Thread() {
            public void run() {
                String naverHtml = getNaverHtml();

                Bundle bun = new Bundle();
                bun.putString("NAVER_HTML", naverHtml);
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();

        tvNaverHtml = (TextView)this.findViewById(R.id.tv_naver_html);
        //tvNaverHtml.setText(naverHtml);
    }

    private String getNaverHtml(){
        String naverHtml = "";

        URL url =null;
        HttpURLConnection http = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try{
            url = new URL("http://" + ip + ":" + port);
            http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(3*1000);
            http.setReadTimeout(3*1000);

            isr = new InputStreamReader(http.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                naverHtml += str + "\n";
            }

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

        return naverHtml;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String naverHtml = bun.getString("NAVER_HTML");
            tvNaverHtml.setText(naverHtml);
        }
    };

    @Override
    public void onClick(View v) {

        new Thread() {
            public void run() {
                String naverHtml = postdesk();
            }
        }.start();


    }

    private String postdesk(){
        String naverHtml = "";

        URL url =null;
        HttpURLConnection http = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try{
            url = new URL("http://" + ip + ":" + port);
            http = (HttpURLConnection) url.openConnection();
            //start
            String data = URLEncoder.encode("student_num", "UTF-8") + "="
                    + URLEncoder.encode(student_num, "UTF-8");
            data += "&" + URLEncoder.encode("table_num", "UTF-8") + "="
                    + URLEncoder.encode("1", "UTF-8");

            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr =
                    new OutputStreamWriter(conn.getOutputStream());

            wr.write(data); //현재 위도, 경도값을 data에 넣고 near.php에 보낸다.
            wr.flush();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }


            reader.close();


            ///end













            http.setConnectTimeout(3*1000);
            http.setReadTimeout(3*1000);

            isr = new InputStreamReader(http.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                naverHtml += str + "\n";
            }

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

        return naverHtml;
    }

}
