package com.graduationproject.ptmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;


/**
 * Created by crystal on 2018-03-30.
 */

public class Calculate extends AppCompatActivity{

    TextView txtCarNo, txtCarIn, txtPark, txtCharge, txtSale, txtSum;
    Button btnCharge, btnDetail;
    TableLayout miniTable;

    Calendar calendar = Calendar.getInstance();
    int iDayofWeek = calendar.DAY_OF_WEEK;
    StringBuilder sb = new StringBuilder();
    StringBuilder Sale = new StringBuilder();
    String ID;
    String Name;
    String CarNo;
    String[][] sTable = new String[20][3];
    String[][] pTable = new String[20][3];
    TextView[][] tableText = new TextView[20][3];
    TableRow row[] = new TableRow[20];
    int sbCount = 0;
    int detailCount = 0;
    int hour = 0;
    int min = 0;
    int sale_all = 0;
    int time_all = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate);

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        hour = intent.getIntExtra("Hour", 0);
        min = intent.getIntExtra("Minute", 0);
        int sec = intent.getIntExtra("Second", 0);
        String inTime = intent.getStringExtra("InTime");

        String strTimer = hour + ":" +  min + ":" + sec;

        txtCarNo = (TextView) findViewById(R.id.txtCarNo);
        txtCarIn = (TextView) findViewById(R.id.txtCarIn);
        txtPark = (TextView) findViewById(R.id.txtPark);
        txtCharge = (TextView) findViewById(R.id.txtCharge);
        txtSale = (TextView) findViewById(R.id.txtSale);
        txtSum = (TextView) findViewById(R.id.txtSum);
        btnCharge = (Button) findViewById(R.id.btnCharge);
        btnDetail = (Button) findViewById(R.id.btnDetail);


        DB_Output task = new DB_Output();
        task.execute(ID);

        txtCarIn.setText(inTime);
        txtPark.setText(strTimer);

        Price_Output price_output = new Price_Output();
        price_output.execute("");


        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeTable();
            }
        });


        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                String OutTime = sDF.format(now);
                //Toast.makeText(getApplicationContext(), OutTime, Toast.LENGTH_LONG).show();
                //carout db 입력
                Carout_Input input = new Carout_Input();
                input.execute(CarNo, OutTime);
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mMovie:
                String sale = "영화 할인,";
                Sale.append(sale);
                sbCount++;
                InsertData insert = new InsertData();
                insert.execute(ID, Sale.toString());
                break;

            case R.id.mMeal:
                sale = "식사 할인,";
                Sale.append(sale);
                sbCount++;
                InsertData insert2 = new InsertData();
                insert2.execute(ID, Sale.toString());
                break;
        }
        return true;
    }

    public void calculate(int hour, int min) {
        int conv_min = (hour * 60) + min;
        int conv_min_time = conv_min - time_all;

        if(conv_min_time <= 0) {
            //txtCharge.setText("0원");
            txtSum.setText("0원");
        }
        else {
            for(int n = 0; n < 2; n++) {
                if(pTable[n][0].equals("holiday")) {
                    if (iDayofWeek == 1 || iDayofWeek == 7) {
                        if (min < 30 && hour < 1) {
                            int all_pay = Integer.parseInt(pTable[n][1]);
                            String str = Integer.toString(all_pay) + "원";
                            txtCharge.setText(str);

                            int all_pay_sale = all_pay - sale_all;
                            if(all_pay_sale <= 0) {
                                //txtCharge.setText("0원");
                                txtSum.setText("0원");
                            } else {
                                String s = Integer.toString(all_pay_sale) + "원";
                                txtSum.setText(s);
                            }
                        } else {
                            int conv_min_ten = (conv_min - 30) / 10;
                            if(conv_min_ten % 10 > 0)
                                conv_min_ten++;
                            int conv_min_time_ten = (conv_min_time - 30) / 10;
                            if (conv_min_time % 10 > 0)
                                conv_min_time_ten++;

                            int pay_without = Integer.parseInt(pTable[n][1]) + (conv_min_ten * Integer.parseInt(pTable[n][2]));
                            int all_pay = Integer.parseInt(pTable[n][1]) + (conv_min_time_ten * Integer.parseInt(pTable[n][2]));
                            String str = Integer.toString(pay_without) + "원";
                            txtCharge.setText(str);

                            int all_pay_sale = all_pay - sale_all;
                            if(all_pay_sale <= 0) {
                                //txtCharge.setText("0원");
                                txtSum.setText("0원");
                            } else {
                                String s = Integer.toString(all_pay_sale) + "원";
                                txtSum.setText(s);
                            }
                        }
                    }
                }
                else {
                    if (min < 30 && hour < 1) {
                        int all_pay = Integer.parseInt(pTable[n][1]);
                        String str = Integer.toString(all_pay) + "원";
                        txtCharge.setText(str);

                        int all_pay_sale = all_pay - sale_all;
                        if(all_pay_sale < 0) {
                            //txtCharge.setText("0원");
                            txtSum.setText("0원");
                        } else {
                            String s = Integer.toString(all_pay_sale) + "원";
                            txtSum.setText(s);
                        }
                    } else {
                        int conv_min_ten = (conv_min - 30) / 10;
                        if(conv_min_ten % 10 > 0)
                            conv_min_ten++;
                        int conv_min_time_ten = (conv_min_time - 30) / 10;
                        if (conv_min_time % 10 > 0)
                            conv_min_time_ten++;

                        int pay_without = Integer.parseInt(pTable[n][1]) + (conv_min_ten * Integer.parseInt(pTable[n][2]));
                        int all_pay = Integer.parseInt(pTable[n][1]) + (conv_min_time_ten * Integer.parseInt(pTable[n][2]));
                        String str = Integer.toString(pay_without) + "원";
                        txtCharge.setText(str);

                        int all_pay_sale = all_pay - sale_all;
                        if(all_pay_sale < 0) {
                            //txtCharge.setText("0원");
                            txtSum.setText("0원");
                        } else {
                            String s = Integer.toString(all_pay_sale) + "원";
                            txtSum.setText(s);
                        }
                    }
                }
            }
        }
    }

    public  void makeTable() {
        View dialogView = (View) View.inflate(Calculate.this, R.layout.dialog, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(Calculate.this);
        miniTable = (TableLayout) dialogView.findViewById(R.id.miniTable);

        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        for(int n = 0; n < sbCount; n++) {
            row[n] = new TableRow(Calculate.this);

            for(int m = 0; m < 3; m++) {
                tableText[n][m] = new TextView(Calculate.this);
                tableText[n][m].setText(sTable[n][m]);
                tableText[n][m].setTextSize(15);
                tableText[n][m].setGravity(Gravity.CENTER);
                tableText[n][m].setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                row[n].addView(tableText[n][m]);
            }
            if(sTable[n][1].equals("Time")) {
                tableText[n][1].setText("");
                String a = sTable[n][2] + "분";
                tableText[n][2].setText(a);
            }
            else if(sTable[n][1].equals("Charge")){
                tableText[n][1].setText("");
                String a = sTable[n][2] + "원";
                tableText[n][2].setText(a);
            }

            miniTable.addView(row[n], rowLayout);

        }
        dlg.setView(dialogView);
        dlg.setNegativeButton("닫기", null);
        dlg.show();
        detailCount = 0;
    }

    class DB_Output extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Calculate.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            txtCarNo.setText(result);
            CarNo = result;
            sb.delete(0, sb.length());

        }



        @Override
        protected String doInBackground(String... params) {

            String ID = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/Cal_output.php";
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

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Calculate.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            sb.delete(0, sb.length());

            Sale_Output sale_output = new Sale_Output();
            sale_output.execute(Name);
        }

        // 이부분은 우리 php랑 db에 맞게 수정해야함
        @Override
        protected String doInBackground(String... params) {

            String ID = (String) params[0];
            String Sale = (String) params[1];

            Name = Sale;

            String serverURL = "http://10.0.2.2/graduationproject/sale_update_Customer.php";
            String postParameters = "ID=" + ID + "&Sale=" + Sale;

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

    class Sale_Output extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Calculate.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            detailCount++;


            for(int n = 0; n < sbCount; n++) {
                if (sTable[n][1].equals("Charge")) {
                    int iSale = Integer.parseInt(sTable[n][2]);
                    sale_all = sale_all + iSale;
                }
                if(sTable[n][1].equals("Time")) {
                    int iTime = Integer.parseInt(sTable[n][2]);
                    time_all = time_all + iTime;
                }
                String a = sale_all + "원";

                txtSale.setText(a);
            }

            Price_Output price_output = new Price_Output();
            price_output.execute("");
        }

        @Override
        protected String doInBackground(String... params) {

            String Name = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/Sale_output_Customer.php";
            String postParameters = "Name=" + Name;

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

                    StringTokenizer tokens = new StringTokenizer(sb.toString());

                    for(int i = 0; i < sbCount; i++) {
                        for (int n = 0; n < 3; n++) {
                            sTable[i][n] = tokens.nextToken(",");
                        }
                    }

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

            progressDialog = ProgressDialog.show(Calculate.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            calculate(hour, min);
        }

        @Override
        protected String doInBackground(String... params) {

            String Name = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/price_output.php";
            String postParameters = "";

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
                    StringBuilder strB = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        strB.append(line);
                    }

                    bufferedReader.close();

                    StringTokenizer tokens = new StringTokenizer(strB.toString());

                    for(int i = 0; i < 2; i++) {
                        for (int n = 0; n < 3; n++) {
                            pTable[i][n] = tokens.nextToken(",");
                        }
                    }

                    return strB.toString();

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

    class Carout_Input extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Calculate.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {

            String CarNo = (String) params[0];
            String OutTime = (String) params[1];

            String serverURL = "http://10.0.2.2/graduationproject/Carout_insert.php";
            String postParameters = "CarNo=" + CarNo + "&OutTime=" + OutTime;

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

