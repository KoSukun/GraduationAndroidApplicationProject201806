package com.graduationproject.ptmanager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sukun on 2018-05-07.
 */

public class PerPrice extends AppCompatActivity{
    EditText basicPrice, pricePer;
    RadioButton weekDay, holiday;
    Button btnInput, btnDelete;
    String pricesystem, basicprice, perprice;
    RelativeLayout Layout;
    private static String TAG = "PerPrice";
    StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perprice);

        Layout = (RelativeLayout)findViewById(R.id.Layout);
        basicPrice = (EditText) findViewById(R.id.basicPrice);
        pricePer = (EditText) findViewById(R.id.pricePer);
        weekDay = (RadioButton) findViewById(R.id.weekDay);
        holiday = (RadioButton) findViewById(R.id.holiday);
        btnInput = (Button) findViewById(R.id.btnInput);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        Layout.setVisibility(View.GONE);
        weekDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    Layout.setVisibility(View.VISIBLE);
            }
        });
        holiday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    Layout.setVisibility(View.VISIBLE);
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basicprice = basicPrice.getText().toString();
                perprice = pricePer.getText().toString();

                if(weekDay.isChecked())   // checked 이면 주말에 입력되는 것으로 바꾸면 됨
                    pricesystem = "weekday";
                else if(holiday.isChecked())
                    pricesystem = "holiday";

                // 만약 요금체계가 입력되어있으면 이미 입력되어있다고 받지 않는 것과 메뉴 숨겨져 있다가 선택하면 드러나는 것

                if(basicprice == null)
                    Toast.makeText(getApplicationContext(), "기본 금액을 입력하세요", Toast.LENGTH_SHORT).show();
                else if(perprice == null)
                    Toast.makeText(getApplicationContext(), "10분당 금액을 입력하세요", Toast.LENGTH_SHORT).show();
                else if(!weekDay.isChecked() && !holiday.isChecked())
                    Toast.makeText(getApplicationContext(), "요금 체계가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                else {
                    //DB 저장
                    InsertData task = new InsertData();
                    task.execute(pricesystem, basicprice, perprice);
                    Toast.makeText(getApplicationContext(), "요금 체계가 등록되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pricesystem = null;
                basicprice = null;
                perprice = null;
                basicPrice.setText("");
                pricePer.setText("");
                weekDay.setChecked(false);
                holiday.setChecked(false);
            }
        });
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PerPrice.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String pricesystem = (String)params[0];
            String basicprice = (String)params[1];
            String perprice = (String)params[2];

            String serverURL = "http://10.0.2.2/graduationproject/price_insert.php";
            String postParameters = "pricesystem=" + pricesystem + "&basicprice=" + basicprice + "&perprice=" + perprice;


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

                    Log.d(TAG, "InsertData: Error ", e);
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
