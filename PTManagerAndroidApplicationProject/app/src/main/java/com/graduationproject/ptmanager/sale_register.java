package com.graduationproject.ptmanager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by crystal on 2018-05-02.
 */

public class sale_register extends AppCompatActivity{
    EditText saleName, saleDetail;
    RadioButton saleTime, saleCharge;
    Button btnConf, btnReset;
    String Name, Price, Type;
    private static String TAG = "sale_register";
    StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_register);

        saleName = (EditText) findViewById(R.id.saleName);
        saleDetail = (EditText) findViewById(R.id.saleDetail);
        saleTime = (RadioButton) findViewById(R.id.saleTime);
        saleCharge = (RadioButton) findViewById(R.id.saleCharge);
        btnConf = (Button) findViewById(R.id.btnConf);
        btnReset = (Button) findViewById(R.id.btnReset);

        btnConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = saleName.getText().toString();
                Price = saleDetail.getText().toString();
                if(saleTime.isChecked())
                    Type = "Time";
                else if(saleCharge.isChecked())
                    Type = "Charge";

                if(Name == null)
                    Toast.makeText(getApplicationContext(), "할인 이름이 입력되지 않았습니다", Toast.LENGTH_SHORT).show();
                else if(Price == null)
                    Toast.makeText(getApplicationContext(), "할인 내용이 입력되지 않았습니다", Toast.LENGTH_SHORT).show();
                else if(!saleTime.isChecked() && !saleCharge.isChecked())
                    Toast.makeText(getApplicationContext(), "할인 종류가 체크되지 않았습니다", Toast.LENGTH_SHORT).show();
                else {
                    //DB 저장
                    InsertData task = new InsertData();
                    task.execute(Name, Type, Price);
                    Toast.makeText(getApplicationContext(), Name + Type + Price, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = null;
                Price = null;
                Type = null;
                saleName.setText("");
                saleDetail.setText("");
                saleTime.setChecked(false);
                saleCharge.setChecked(false);
            }
        });
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(sale_register.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }

        // 이부분은 우리 php랑 db에 맞게 수정해야함
        @Override
        protected String doInBackground(String... params) {

            String Name = (String) params[0];
            String Type = (String) params[1];
            String Price = (String) params[2];

            String serverURL = "http://10.0.2.2/graduationproject/sale_insert.php";
            String postParameters = "Name=" + Name + "&Type=" + Type + "&Price=" + Price;

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