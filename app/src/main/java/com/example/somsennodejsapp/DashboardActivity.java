package com.example.somsennodejsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.somsennodejsapp.Retrofit.INodeJS;
import com.example.somsennodejsapp.Retrofit.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity {

    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextView welcome_text;
    MaterialButton btn_logout;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Intialize API
        Retrofit retrofit = RetrofitClient.getInstance(); //Allows us to parse json
        loginAPI = retrofit.create(INodeJS.class);
        welcome_text = (TextView)findViewById(R.id.welcome_text_name);
        welcome_text.setText("Welcome" + " " + UserState.getInstance().name + "!");
        btn_logout = (MaterialButton)findViewById(R.id.logout_btn);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                finish();
            }
        });
    }



}