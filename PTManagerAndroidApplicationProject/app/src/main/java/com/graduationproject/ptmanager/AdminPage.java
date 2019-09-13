package com.graduationproject.ptmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by sukun on 2018-05-08.
 */

public class AdminPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        Toast.makeText(getApplicationContext(), "관리자님 환영합니다.", Toast.LENGTH_SHORT).show();
        //요금 등록
        findViewById(R.id.priceinsertbtn).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent_act = new Intent(getApplicationContext(), PerPrice.class);
                        startActivity(intent_act);
                    }
                }
        );
        //할인 등록
        findViewById(R.id.saleinsertbtn).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent_act = new Intent(getApplicationContext(), sale_register.class);
                        startActivity(intent_act);
                    }
                }
        );
        //주차 차량 확인
        findViewById(R.id.carinshowbtn).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent_act = new Intent(getApplicationContext(), CarIn_show.class);
                        startActivity(intent_act);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logOut:
                //로그아웃 기능

                return true;
        }
        return true;
    }



}
