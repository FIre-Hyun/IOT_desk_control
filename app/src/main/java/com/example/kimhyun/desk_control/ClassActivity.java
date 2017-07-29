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

    Button [] btn = new Button[4];

    private String html = "";
    private Handler mHandler;

    private Socket socket;
    TextView tvNaverHtml;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    private String ip = "192.168.1.4"; // IP
    private int port = 3000; // PORT번호

    String mJsonString;

    String naverHtml;

    String student_num;

    String [] desk = new String[10];

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
                naverHtml = getNaverHtml();

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
        naverHtml = "";


        Log.d("getNaverHtml", "getNaverHtml");
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
                naverHtml += str + "\n";
                Log.d("naver", naverHtml);
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

        return naverHtml;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String naverHtml = bun.getString("NAVER_HTML");
            tvNaverHtml.setText(naverHtml);
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
                            Log.d("for문", String.valueOf(i));
                            desk[i] = String.valueOf(naverHtml.charAt(i+11));
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
            url = new URL("http://192.168.1.4:3000");
            //url = new URL("http://jun123101.cafe24.com");
            Log.d("post", "start");
            http = (HttpURLConnection) url.openConnection();
            Log.d("post", "connect");
            //start
            String data = URLEncoder.encode("student_num", "UTF-8") + "="
                    + URLEncoder.encode(student_num, "UTF-8");
            data += "&" + URLEncoder.encode("table_num", "UTF-8") + "="
                    + URLEncoder.encode("1", "UTF-8");

            Log.d("post", data);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr =
                    new OutputStreamWriter(conn.getOutputStream());

            Log.d("post", "write전");
            wr.write(data);
            wr.flush();
            Log.d("post", "write후");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            Log.d("post", "00000");

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Log.d("post", "11111");

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
//
//    public String sendHttpWithMsg(String url){
//
////기본적인 설정
//
//        DefaultHttpClient client = new DefaultHttpClient();
//
//        HttpPost post = new HttpPost(url);
//
//        HttpParams params = client.getParams();
//
//        HttpConnectionParams.setConnectionTimeout(params, 3000);
//
//        HttpConnectionParams.setSoTimeout(params, 3000);
//
//        post.setHeader("Content-type", "application/json; charset=utf-8");
//
//
//
//// JSON OBject를 생성하고 데이터를 입력합니다.
//
////여기서 처음에 봤던 데이터가 만들어집니다.
//
//        JSONObject jObj = new JSONObject();
//
//        try {
//
//            jObj.put("name", "hong");
//
//            jObj.put("phone", "000-0000");
//
//
//
//        } catch (JSONException e1) {
//
//            e1.printStackTrace();
//
//        }
//
//
//
//
//
//
//
//        try {
//
//// JSON을 String 형변환하여 httpEntity에 넣어줍니다.
//
//            StringEntity se;
//
//            se = new StringEntity(jObj.toString());
//
//            HttpEntity he=se;
//
//            post.setEntity(he);
//
//
//
//        } catch (UnsupportedEncodingException e1) {
//
//            e1.printStackTrace();	}
//
//
//
//
//
//        try {
//
////httpPost 를 서버로 보내고 응답을 받습니다.
//
//            HttpResponse response = client.execute(post);
//
//// 받아온 응답으로부터 내용을 받아옵니다.
//
//// 단순한 string으로 읽어와 그내용을 리턴해줍니다.
//
//            BufferedReader bufReader =
//
//                    new BufferedReader(new InputStreamReader(
//
//                            response.getEntity().getContent(),
//
//                            "utf-8"
//
//                    )
//
//                    );
//
//
//
//            String line = null;
//
//            String result = "";
//
//
//
//            while ((line = bufReader.readLine())!=null){
//
//                result +=line;
//
//            }
//
//            return result;
//
//
//
//        } catch (ClientProtocolException e) {
//
//            e.printStackTrace();
//
//            return "Error"+e.toString();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//            return "Error"+e.toString();
//
//        }
//
//    }
//


}
