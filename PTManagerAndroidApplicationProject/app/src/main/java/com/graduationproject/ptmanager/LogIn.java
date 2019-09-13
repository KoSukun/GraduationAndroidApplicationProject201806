package com.graduationproject.ptmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.net.MalformedURLException;

import static android.content.ContentValues.TAG;



/**
 * Created by crystal & sukun on 2018-03-28.
 */

public class LogIn extends AppCompatActivity {

    final int RC_SIGN_IN = 1001; // 로그인 확인여부 코드
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient; //API 클라이언트

    private Button btn_send;

    Button  btnGoogle;
    EditText id, password;

    boolean isCar = false, isLog = false;
    //
    //private EditText mEditTextID;
    //private EditText mEditTextCarNo;
    //

    String mEditTextCarNo;
    String mEditTextID;
    String getID;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Login fail");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        //id = (EditText) findViewById(R.id.id);
        //password = (EditText) findViewById(R.id.password);


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();

                    Log.d(TAG, "이름 =" + account.getDisplayName());
                    Log.d(TAG, "이메일=" + account.getEmail());
                    Log.d(TAG, "getId()=" + account.getId());
                    Log.d(TAG, "getAccount()=" + account.getAccount());
                    Log.d(TAG, "getIdToken()=" + account.getIdToken());
                    getID = account.getId();


                    AlertDialog.Builder alert = new AlertDialog.Builder(LogIn.this);
                    alert.setTitle("자동차 번호");
                    alert.setMessage("등록할 자동차 번호를 입력하세요.");
                    final EditText CarNo = new EditText(LogIn.this);
                    mEditTextID = account.getId();


                    alert.setView(CarNo);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //int carNo = Integer.parseInt(CarNo.getText().toString());
                            // DB 저장 부분
                            mEditTextCarNo = CarNo.getText().toString();



                            String ID = mEditTextID;
                            String CarNo = mEditTextCarNo;

                            Toast.makeText(getApplicationContext(), CarNo, Toast.LENGTH_SHORT).show();

                            InsertData task = new InsertData();
                            task.execute(ID, CarNo);

                        /*
                        try {
                            URLConnector request = new URLConnector("http://127.0.0.1/graduationproject/Data" +
                                    "_insert.php");
                            String result = request.PhPtest(String.valueOf(ID), String.valueOf(CarNo)); //.getText();
                            if(result.equals("1")){
                                Toast.makeText(getApplication(),"들어감",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplication(),"안 들어감",Toast.LENGTH_SHORT).show();
                            }
                        }catch (MalformedURLException e){
                            e.printStackTrace();
                        }*/
                            Toast.makeText(LogIn.this, "Success!", Toast.LENGTH_SHORT).show();
                            isCar = true;
                            isLog = true;
                        }
                    });
                if(!isLog) { alert.show(); }
                if (isCar == true) {
                    firebaseAuthWithGoogle(account);
                }
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    public void onStart() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //getID = currentUser.getUid();
        if (currentUser != null) { // 만약 로그인이 되어있으면 다음 액티비티 실행
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("ID", getID);
            startActivity(intent);
            finish();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            if(getID.equals("105205044512464203733")) {
                                Intent intent = new Intent(getApplicationContext(), AdminPage.class);
                                intent.putExtra("ID", getID);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("ID", getID);
                                startActivity(intent);
                                finish();
                            }
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LogIn.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String ID = (String) params[0];
            String CarNo = (String) params[1];

            String serverURL = "http://10.0.2.2/graduationproject/Data_insert.php";
            String postParameters = "ID=" + ID + "&CarNo=" + CarNo + "&Sale=" + "";

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