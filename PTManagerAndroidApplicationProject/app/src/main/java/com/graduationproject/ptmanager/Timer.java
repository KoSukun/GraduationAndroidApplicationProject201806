package com.graduationproject.ptmanager;

/**
 * Created by sukun on 2018-03-18.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;

public class Timer extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "MainActivity";

    Button button1;
    Button button2;
    Button button3;
    TextView textView, textView2, txtDescribe;
    RelativeLayout relativeLayout;

    TableLayout tableLayout;
    TableRow row[] = new TableRow[5];
    TextView tableText[][] = new TextView[5][3];

    StringBuilder strB = new StringBuilder();
    int tr = 0;
    boolean isDB = false;
    String[][] sTable = new String[5][3];

    StringBuilder sb = new StringBuilder();

    String strTimer = new String();
    String ID;
    final String colon = " : ";

    int hour = 0;
    int min = 0;
    int sec = 0;
    int tenMilli = 0;
    final int period = 10;
    boolean start = false;
    boolean dbReady = false;
    String inTime;

    long res;

    ThreadTimer threadTimer = null;

    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (start) {
                switch (msg.what) {
                    case period:
                        increaseTime();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");

        DB_Output dbTask = new DB_Output();
        dbTask.execute(ID);



        String url = "http://localhost/";   //url말고 IP적는 편이 좋다
        com.graduationproject.ptmanager.URLConnector task = new com.graduationproject.ptmanager.URLConnector(url);
        task.start();
        try{
            task.join();
        }catch (InterruptedException e) {

        }
        String result = task.getResult();

        button1 = (Button) findViewById(R.id.timerBtn1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.timerBtn2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.timerBtn3);
        button3.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.txt_timer);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative2);
        textView2 = (TextView) findViewById(R.id.textView2);
        tableLayout = (TableLayout) findViewById(R.id.tLayout);
        txtDescribe = (TextView) findViewById(R.id.txtDescribe);

        tableLayout.setVisibility(View.INVISIBLE);
        txtDescribe.setVisibility(View.INVISIBLE);

        threadTimer = new ThreadTimer(mainHandler);
        threadTimer.setDaemon(true);
        threadTimer.start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timerBtn1:
                button1.setBackgroundColor(Color.argb(255, 141, 179, 226));
                button2.setBackgroundColor(Color.argb(255, 84, 141, 212));
                tableLayout.setVisibility(View.INVISIBLE);
                txtDescribe.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);
                relativeLayout.setBackgroundColor(Color.argb(255, 141, 179, 226));
                button3.setBackgroundColor(Color.argb(255, 84, 141, 212));
                button3.setTextColor(Color.BLACK);

                break;

            case R.id.timerBtn2:
                button2.setBackgroundColor(Color.argb(255, 141, 179, 226));
                button1.setBackgroundColor(Color.argb(255, 84, 141, 212));
                textView.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                button3.setVisibility(View.INVISIBLE);
                tableLayout.setVisibility(View.VISIBLE);
                txtDescribe.setVisibility(View.VISIBLE);

                String any = "";
                Price_Output task = new Price_Output();
                task.execute(any);
                break;

            case R.id.timerBtn3:
                threadTimer.getTimerHandler().sendEmptyMessage(0);
                start = false;

                Intent intent_act = new Intent(getApplicationContext(), Calculate.class);
                intent_act.putExtra("Hour", hour);
                intent_act.putExtra("Minute", min);
                intent_act.putExtra("Second", sec);
                intent_act.putExtra("InTime", inTime);
                intent_act.putExtra("ID", ID);
                startActivity(intent_act);
                hour = 0;
                min = 0;
                sec = 0;
                tenMilli = 0;
                break;
        }
    }


    public void increaseTime() {
        tenMilli++;
        if (tenMilli >= 1000 / period) {
            tenMilli = 0;
            sec++;
            Log.d(TAG, "Second: " + sec);
            if (sec >= 60) {
                sec = 0;
                min++;
                Log.d(TAG, "Minute: " + min);
            }
            if (min >= 60) {
                min = 0;
                hour++;
                Log.d(TAG, "Hour: " + hour);
            }
        }

        strTimer = hour + colon + min + colon + sec;

        textView.setText(strTimer);
    }

    class DB_Output extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Timer.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            long now = System.currentTimeMillis();
            long lInTime = 0;
            SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
            try {

                String sTime = result;
                Date inDate = sDF.parse(sTime);
                lInTime = inDate.getTime();

            } catch (Exception e) {}


            res = (now - lInTime) / 10;

            textView2.setText("입차 시간 : " + result);

            hour = (int)res / 360000;
            min = ((int)res % 360000) / 6000;
            sec = (((int)res % 360000) % 6000) / 100;
            strTimer = hour + colon + min + colon + sec;
            textView.setText(strTimer);
            inTime = result;

            dbReady = true;

            if(!start) {
                if(dbReady) {
                    threadTimer.getTimerHandler().sendEmptyMessage(1);
                    start = true;}}
        }

        @Override
        protected String doInBackground(String... params) {

            String ID = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/Timer_output.php";
            String postParameters = "ID=" + ID;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                try {
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();


                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();


                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "POST response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();

                    return sb.toString();

                } catch (Exception e) {
                    Log.d(TAG, "CustomerData: Error ", e);
                    return new String("Error: " + e.getMessage());
                } finally {
                    httpURLConnection.disconnect();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error : ", e);
            }
            return sb.toString();
        }
    }

    class Price_Output extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Timer.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            isDB = true;

            if(isDB) {
                TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                for(int n = 0; n < tr; n++) {
                    row[n] = new TableRow(Timer.this);

                    for(int m = 0; m < 3; m++) {
                        tableText[n][m] = new TextView(Timer.this);
                        tableText[n][m].setText(sTable[n][m]);
                        tableText[n][m].setTextSize(15);
                        tableText[n][m].setGravity(Gravity.CENTER);
                        tableText[n][m].setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                        row[n].addView(tableText[n][m]);
                    }
                    if(sTable[n][0].equals("weekday")) {
                        tableText[n][0].setText("평일");
                        String a = sTable[n][1] + "원";
                        tableText[n][1].setText(a);
                        String b = sTable[n][2] + "원";
                        tableText[n][2].setText(b);

                    }
                    else if(sTable[n][0].equals("holiday")){
                        tableText[n][0].setText("공휴일");
                        String a = sTable[n][1] + "원";
                        tableText[n][1].setText(a);
                        String b = sTable[n][2] + "원";
                        tableText[n][2].setText(b);

                    }

                    tableLayout.addView(row[n], rowLayout);
                }
                Toast.makeText(getApplicationContext(), Integer.toString(tr), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String any = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/price_output.php";
            String postParameters = "Null";

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                try {
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();


                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();


                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "POST response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    StringTokenizer tokens = new StringTokenizer(sb.toString());

                    for(int n = 0; n < sb.length(); n++)
                    {
                        for(int m = 0; m < 3; m++) {
                            sTable[n][m] = tokens.nextToken(",");
                        }
                        tr++;
                    }

                    bufferedReader.close();

                    return sb.toString();

                } catch (Exception e) {
                    Log.d(TAG, "CustomerData: Error ", e);
                    return new String("Error: " + e.getMessage());
                } finally {
                    httpURLConnection.disconnect();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error : ", e);
            }
            return sb.toString();
        }
    }

}

