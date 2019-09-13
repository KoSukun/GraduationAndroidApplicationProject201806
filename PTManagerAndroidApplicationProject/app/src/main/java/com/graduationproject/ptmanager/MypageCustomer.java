package com.graduationproject.ptmanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;


/**
 * Created by crystal on 2018-05-08.
 */

public class MypageCustomer extends AppCompatActivity{

    TextView txtUID, txtCarNo;
    Button btnChange;
    ImageView CardImg;

    String CarNo;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_customer);

        Intent intent = getIntent();
        final String ID = intent.getStringExtra("ID");
        Toast.makeText(getApplicationContext(), ID, Toast.LENGTH_SHORT).show();

        txtUID = (TextView) findViewById(R.id.txtUID);
        txtCarNo = (TextView) findViewById(R.id.txtCarNo);

        CardImg = (ImageView) findViewById(R.id.CardImg);
        String strBarcode = "BarCodeSample";
        Bitmap barcode = createBarcode(strBarcode);
        CardImg.setImageBitmap(barcode);
        CardImg.invalidate();

        btnChange = (Button) findViewById(R.id.btnChange);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MypageCustomer.this);
                alert.setTitle("자동차 번호");
                alert.setMessage("수정할 자동차 번호를 입력하세요.");
                final EditText etCarNo = new EditText(MypageCustomer.this);

                alert.setView(etCarNo);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CarNo = etCarNo.getText().toString();

                        //DB UPDATE 부분
                        DB_Update task = new DB_Update();
                        task.execute(ID, CarNo);
                        CarNo = null;

                    }
                });
                alert.show();
            }
        });
        txtUID.setText(ID);
        //DB Select
        DB_Output task = new DB_Output();
        task.execute(ID);


        //로그아웃
        FirebaseAuth.getInstance().signOut();
    }

    public Bitmap createBarcode(String code) {
        Bitmap bitmap = null;
        MultiFormatWriter gen = new MultiFormatWriter();
        try {
            final int WIDTH = 1280;
            final int HEIGHT = 320;
            BitMatrix bytemap = gen.encode(code, BarcodeFormat.CODE_128, WIDTH, HEIGHT);
            bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < WIDTH; ++i)
                for (int j = 0; j < HEIGHT; ++j) {
                    bitmap.setPixel(i, j, bytemap.get(i, j) ? Color.BLACK : Color.WHITE);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }



    class DB_Output extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MypageCustomer.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            txtCarNo.setText(result);
            sb.delete(0, sb.length());
        }

        @Override
        protected String doInBackground(String... params) {

            String ID = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/Customer_output_2.php";
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


    class DB_Update extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MypageCustomer.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            txtCarNo.setText(result);
            sb.delete(0, sb.length());
        }

        @Override
        protected String doInBackground(String... params) {

            String ID = (String) params[0];
            String CarNo = (String) params[1];

            String serverURL = "http://10.0.2.2/graduationproject/Customer_update_2.php";
            String postParameters = "ID=" + ID + "&CarNo=" + CarNo;

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
}

