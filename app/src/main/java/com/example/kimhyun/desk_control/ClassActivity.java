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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClassActivity extends AppCompatActivity implements View.OnClickListener {

    Button[] btn = new Button[20];

    Button[] test_btn = new Button[20];

    TextView tvHtml, tv_sum;


    private String ip = "192.168.1.6"; // IP
    private int port = 3000; // PORT번호

    String Html;

    String student_num;

    String[] checked_num = new String[20];

    String desk_kind;

    String[] desk = new String[20];

    int desk_num = 0, student_sum = 0;

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
        btn[4] = (Button) findViewById(R.id.test0);
        btn[5] = (Button) findViewById(R.id.test1);
        btn[6] = (Button) findViewById(R.id.test2);
        btn[7] = (Button) findViewById(R.id.test3);
        btn[8] = (Button) findViewById(R.id.test4);
        btn[9] = (Button) findViewById(R.id.test5);
        btn[10] = (Button) findViewById(R.id.test6);
        btn[11] = (Button) findViewById(R.id.test7);
        btn[12] = (Button) findViewById(R.id.test8);
        btn[13] = (Button) findViewById(R.id.test9);
        btn[14] = (Button) findViewById(R.id.test10);
        btn[15] = (Button) findViewById(R.id.test11);

        btn[0].setOnClickListener(this);
        btn[1].setOnClickListener(this);
        btn[2].setOnClickListener(this);
        btn[3].setOnClickListener(this);
        btn[4].setOnClickListener(this);
        btn[5].setOnClickListener(this);
        btn[6].setOnClickListener(this);
        btn[7].setOnClickListener(this);
        btn[8].setOnClickListener(this);
        btn[9].setOnClickListener(this);
        btn[10].setOnClickListener(this);
        btn[11].setOnClickListener(this);
        btn[12].setOnClickListener(this);
        btn[13].setOnClickListener(this);
        btn[14].setOnClickListener(this);
        btn[15].setOnClickListener(this);

        tv_sum = (TextView) findViewById(R.id.tv_sum);

        radio_desk = (RadioButton) findViewById(R.id.radio_desk);
        radio_chair = (RadioButton) findViewById(R.id.radio_chair);

        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);

        desk_kind = "a";

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio_desk)
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

        tvHtml = (TextView) this.findViewById(R.id.tv_html);
    }


    private String getHtml() {
        Html = "";

        URL url = null;

        HttpURLConnection http = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            url = new URL("http://" + ip + ":" + port);
            http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(3 * 1000);
            http.setReadTimeout(3 * 1000);

            Log.d("try", "try");
            isr = new InputStreamReader(http.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                Html += str + "\n";
                Log.d("html", Html);
            }

            showResult();

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            if (http != null) {
                try {
                    http.disconnect();
                } catch (Exception e) {
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }

        return Html;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String Html = bun.getString("HTML");
//            tvHtml.setText(Html);
        }
    };


    private void showResult() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject obj = new JSONObject(Html);
                            for(int i = 0; i < 16; i++) {
                                desk[i] = obj.getString("desk"+i);
                                checked_num[i] = obj.getString("number"+i);
                                Log.d("jsontest", desk[0] + desk[1] + desk[2] + desk[3]);
                            }

                            for (int i = 0; i < 16; i++) {
                                if (desk[i].equals("c")) {
                                    btn[i].setText("Empty");
                                    btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                                } else if (desk[i].equals("b")) {
                                    student_sum++;
                                    btn[i].setText(checked_num[i]);
                                    btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
                                } else if (desk[i].equals("a")) {
                                    student_sum++;
                                    btn[i].setText(checked_num[i]);
                                    btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorAccent));
                                } else {
                                    btn[i].setText("Error");
                                    btn[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                                }

                            }

                            tv_sum.setText("출석한 학생 수 : " + student_sum);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("desk", desk[0] + desk[1] + desk[2] + desk[3] + desk[4]);

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
            case R.id.test0:
                desk_num = 4;
                break;
            case R.id.test1:
                desk_num = 5;
                break;
            case R.id.test2:
                desk_num = 6;
                break;
            case R.id.test3:
                desk_num = 7;
                break;
            case R.id.test4:
                desk_num = 8;
                break;
            case R.id.test5:
                desk_num = 9;
                break;
            case R.id.test6:
                desk_num = 10;
                break;
            case R.id.test7:
                desk_num = 11;
                break;
            case R.id.test8:
                desk_num = 12;
                break;
            case R.id.test9:
                desk_num = 13;
                break;
            case R.id.test10:
                desk_num = 14;
                break;
            case R.id.test11:
                desk_num = 15;
                break;
            default:
                desk_num = 16;
                break;

        }
        new Thread() {
            public void run() {
                Log.d("1111", "1나왓당");
                Log.d("desk2", desk[0] + desk[1] + desk[2] + desk[3] + desk[4]);


                if (desk[desk_num].equals("c")) {        //추가코딩 해야함.. Empty이면 선택하는부분


                    Log.d("1111", "1나왓당");

                    String Clicked_Desk = null;
                    try {
                        Log.d("1111", "1나왓당");
                        Clicked_Desk = postdesk(desk_num, desk_kind);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Clicked", Clicked_Desk);

                    desk[desk_num] = desk_kind;
                } else if (!desk[desk_num].equals("c")) {  // Empty가 아니면

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
                            student_sum--;
                            btn[desk_num].setText("Empty");
                            btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                        } else if (desk[desk_num].equals("b")) {
                            student_sum++;
                            btn[desk_num].setText(student_num);
                            btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
                        } else if (desk[desk_num].equals("a")) {
                            student_sum++;
                            btn[desk_num].setText(student_num);
                            btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorAccent));
                        } else {
                            btn[desk_num].setText("Error");
                            btn[desk_num].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                        }
                        tv_sum.setText("출석한 학생 수 : " + student_sum);
                    }
                });

            }
        }.start();


    }

    private String postdesk(int desk_num, String desk_kind) throws IOException {

        URL url = null;

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

            while ((line = reader.readLine()) != null) {
                sb.append(line + '\n');
            }
            Log.d("sb", sb.toString());
//            btn[desk_num].setText("ON");

            reader.close();
            os.close();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            if (http != null) {
                try {
                    http.disconnect();
                } catch (Exception e) {
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }


        return sb.toString();


    }


}
