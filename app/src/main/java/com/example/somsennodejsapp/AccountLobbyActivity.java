package com.example.somsennodejsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.somsennodejsapp.Retrofit.INodeJS;
import com.example.somsennodejsapp.Retrofit.RetrofitClient;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AccountLobbyActivity extends AppCompatActivity {

    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        setContentView(R.layout.activity_account_lobby);
        //Intialize API
        Retrofit retrofit = RetrofitClient.getInstance(); //Allows us to parse json
        loginAPI = retrofit.create(INodeJS.class);
        //Check if the user has an account associated
        getAccount();
        finish();

    }

    //Send info to API and wait for response
    private void getAccount(){
        compositeDisposable.add(loginAPI.getUserInfo("Bearer " + UserState.getInstance().token,UserState.getInstance().unique_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //If response contains some_field then it came back true
                        if(s.contains("unique_id")) {
                            JSONObject response = new JSONObject(s);
                            UserState.getInstance().name = response.getString("first_name");
                            UserState.getInstance().state = response.getString("state");
                            UserState.getInstance().city = response.getString("city");
                            startActivity(new Intent(AccountLobbyActivity.this, DashboardActivity.class));
                        }
                        else
                        {
                            startActivity(new Intent(AccountLobbyActivity.this, AccountCreationActivity.class));
                            //txt_welcome.setText("Welcome! we do not have your profile information. Please create an account.");
                            //Toast.makeText(AccountLobby.this,""+s, Toast.LENGTH_SHORT).show(); //show error from API
                        }
                    }
                })
        );
    }
}
