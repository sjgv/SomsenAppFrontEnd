package com.example.somsennodejsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.somsennodejsapp.Retrofit.INodeJS;
import com.example.somsennodejsapp.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AccountActivity extends AppCompatActivity {

    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextView welcome_text;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Intialize API
        Retrofit retrofit = RetrofitClient.getInstance(); //Allows us to parse json
        loginAPI = retrofit.create(INodeJS.class);
        welcome_text = (TextView)findViewById(R.id.welcome_text_name);
        welcome_text.setText("Welcome" + " " + UserState.getInstance().name + "!");
    }

}