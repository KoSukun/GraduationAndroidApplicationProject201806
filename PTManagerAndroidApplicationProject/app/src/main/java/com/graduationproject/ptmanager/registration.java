package com.graduationproject.ptmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sukun on 2018-03-26.
 */

public class registration extends AppCompatActivity {
    Button chkBtn, insBtn;
    EditText idText, pwText, carNoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        chkBtn = (Button) findViewById(R.id.chkBtn);
        insBtn = (Button) findViewById(R.id.insBtn);
        idText = (EditText) findViewById(R.id.idText);
        pwText = (EditText) findViewById(R.id.pwText);
        carNoText = (EditText) findViewById(R.id.carNoText);

        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*try{
                    PHPRequest request = new PHPRequest("http://127.0.0.1/");
                    String result = request.PhPtest(String.valueOf(idText.getText());

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }*/
            }
        });
    }
}