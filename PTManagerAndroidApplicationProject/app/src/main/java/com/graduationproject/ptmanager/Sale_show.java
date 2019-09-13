package com.graduationproject.ptmanager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;

/**
 * Created by Jaeho on 2018-05-16.
 */

public class Sale_show extends AppCompatActivity{
    TableLayout tableLayout;
    TableRow row[] = new TableRow[20];
    TextView tableText[][] = new TextView[20][3];

    StringBuilder sb = new StringBuilder();
    int tr = 0;
    boolean isDB = false;
    String[][] sTable = new String[20][3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_show);

        tableLayout = (TableLayout) findViewById(R.id.tLayout);

        String any = null;

        DB_Output task = new DB_Output();
        task.execute(any);


    }

    class DB_Output extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Sale_show.this,
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
                    row[n] = new TableRow(Sale_show.this);

                    for(int m = 0; m < 3; m++) {
                        tableText[n][m] = new TextView(Sale_show.this);
                        tableText[n][m].setText(sTable[n][m]);
                        tableText[n][m].setTextSize(15);
                        tableText[n][m].setGravity(Gravity.CENTER);
                        tableText[n][m].setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                        row[n].addView(tableText[n][m]);
                    }
                    if(sTable[n][1].equals("Time")) {
                        tableText[n][1].setText("시간 차감");
                        String a = sTable[n][2] + "분";
                        tableText[n][2].setText(a);
                    }
                    else if(sTable[n][1].equals("Charge")){
                        tableText[n][1].setText("요금 할인");
                        String a = sTable[n][2] + "원";
                        tableText[n][2].setText(a);
                    }

                    tableLayout.addView(row[n], rowLayout);
                }
                //Toast.makeText(getApplicationContext(), Integer.toString(tr), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String any = (String) params[0];

            String serverURL = "http://10.0.2.2/graduationproject/sale_output.php";
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
