package com.example.somsennodejsapp;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.somsennodejsapp.Retrofit.INodeJS;
import com.example.somsennodejsapp.Retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.button.MaterialButton;
import com.rengwuxian.materialedittext.MaterialEditText;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    MaterialEditText edit_email, edit_password;
    MaterialButton btn_register, btn_login;

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
        setContentView(R.layout.activity_main);

        //Intialize API
        Retrofit retrofit = RetrofitClient.getInstance(); //Allows us to parse json
        loginAPI = retrofit.create(INodeJS.class);

        //View
        btn_register = (MaterialButton) findViewById(R.id.register_btn);
        btn_login = (MaterialButton)findViewById(R.id.login_btn);
        edit_email = (MaterialEditText) findViewById(R.id.edit_email);
        edit_password = (MaterialEditText) findViewById(R.id.edit_password);

        //Event
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginUser(edit_email.getText().toString(), edit_password.getText().toString());
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerUser(edit_email.getText().toString(), edit_password.getText().toString());
            }
        });
    }


    private void registerUser(final String email, final String password) {
        final View enter_name_view = LayoutInflater.from(this).inflate(R.layout.enter_name_layout, null);

        new MaterialStyledDialog.Builder(this)
                .setTitle("What should we call you?")
                .setDescription("Enter Name")
                .setCustomView(enter_name_view)
                .setIcon(R.drawable.user_icon)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Register")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        MaterialEditText edit_name =(MaterialEditText)enter_name_view.findViewById(R.id.edit_name);

                        compositeDisposable.add(loginAPI.registerUser(email,edit_name.getText().toString(),password)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        Toast.makeText(MainActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        );
                    }
                }).show();
    }


    private void loginUser(String email, String password){
        compositeDisposable.add(loginAPI.loginUser(email, password)
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.contains("encrypted_password"))
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this,""+s, Toast.LENGTH_SHORT).show(); //show error from API
                    }
                })
        );
    }



}
