package com.example.somsennodejsapp;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    MaterialEditText edit_email, edit_password, edit_phone;
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
                //loginUser(edit_email.getText().toString(), edit_password.getText().toString());
                String pass = edit_password.getText().toString();
                String email = edit_email.getText().toString();
                //Check if empty
                boolean validEmail = emailValidator(email);
                boolean validPass = passwordValidator(pass);
                if (validEmail && validPass)
                    loginUser(email, pass);
                else
                {
                    if (!validEmail)
                        Toast.makeText(MainActivity.this, "Invalid email, must be a valid address.", Toast.LENGTH_SHORT).show();

                    else if (!validPass)
                        Toast.makeText(MainActivity.this, "Invalid Password, please include a number, a special character and a capital letter. No spaces allowed. 6 char minimum.", Toast.LENGTH_LONG).show();


                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //registerUser(edit_email.getText().toString(), edit_password.getText().toString());
                String pass = edit_password.getText().toString();
                String email = edit_email.getText().toString();

                //Check if empty
                boolean validEmail = emailValidator(email);
                boolean validPass = passwordValidator(pass);

                if (validEmail && validPass)
                    registerUser(email, pass);
                else
                {
                    if (!validEmail)
                        Toast.makeText(MainActivity.this, "Invalid email, must be a valid address.", Toast.LENGTH_SHORT).show();

                    else if (!validPass)
                        Toast.makeText(MainActivity.this, "Invalid Password, please include a number, a special character and a capital letter. No spaces allowed. 6 char minimum.", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void registerUser(final String email, final String password) {
        final View enter_phone_number_view = LayoutInflater.from(this).inflate(R.layout.enter_phone_number_layout, null);

        new MaterialStyledDialog.Builder(this)
                .setTitle("Please Enter Your Phone Number")
                .setDescription("Enter Mobile Number")
                .setCustomView(enter_phone_number_view)
                .setIcon(R.drawable.phone_iconpng)
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

                        //MaterialEditText edit_phone =(MaterialEditText)enter_phone_number_view.findViewById(R.id.edit_phone_number);
                        edit_phone = (MaterialEditText) enter_phone_number_view.findViewById(R.id.edit_phone_number);
                        String phone = edit_phone.getText().toString();

                        if(phoneValidator(phone)) {
                            compositeDisposable.add(loginAPI.registerUser(email.toLowerCase(), phone, password)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            //Populate UserState.getInstance() here!
                                            if (s.contains("Error"))
                                                Toast.makeText(MainActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                                            else{
                                                JSONObject response = new JSONObject(s);
                                                UserState.getInstance().token = response.getString("token");
                                                UserState.getInstance().unique_id = response.getJSONObject("user").getString("uid");
                                                startActivity(new Intent(MainActivity.this, AccountLobbyActivity.class));
                                                finish();
                                            }
                                        }
                                    })
                            );
                        }
                        else
                            Toast.makeText(MainActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();

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
                        JSONObject response = new JSONObject(s);
                        String token = "";
                        String uid = "";
                        try {
                            //Get token
                            token = response.getString("token");
                            uid = response.getJSONObject("user").getString("uid");
                        }catch(Exception e){
                            Log.d("LOGIN", e.toString());
                        }

                        if(token != null && token.length() > 0){
                            //Populate UserState.getInstance() here!
                            // We keep the unique_id field for persistence (other tables will have this as key)
                            //UserState.getInstance().unique_id = s.substring(sub_idx_start, sub_idx_end-2);
                            UserState.getInstance().token = token;
                            UserState.getInstance().unique_id = uid;
                            startActivity(new Intent(MainActivity.this, AccountLobbyActivity.class));
                            finish();
                            //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(MainActivity.this,""+response.getString("message"), Toast.LENGTH_SHORT).show(); //show error from API
                    }
                })
        );
    }

    //HELPER METHODS
    private boolean phoneValidator(String phone){
        if (phone.length() > 10)
            return false;
        else if (phone.length() < 10)
            return false;
        //I'm pretty sure they can't input anything that is not numbers thats why we don't check [MAKE SURE]
        else
            return true;
    }

    private boolean emailValidator(String email) {
        boolean length = false;
        boolean at = false;
        boolean dot = false;
        if (email.length() > 100)
            length = true;

        for (char c : email.toCharArray()){
            if (c == '@')
                at = true;
            if (c == '.')
                dot = true;
        }

        if(at && dot && !length)
            return true;
        else
            return false;

    }

    private boolean passwordValidator(String pass) {
        boolean toolong = false;
        boolean minimumlength = false;
        boolean number = false;
        boolean capital = false;
        boolean special = false;
        boolean whitespace = false;
        if (pass.length() > 50)
            toolong = true;
        if (pass.length() > 5)
            minimumlength = true;

        for (char c : pass.toCharArray())
        {
            if(Character.isDigit(c))
            {
                number = true;
            }
            else if(Character.isUpperCase(c))
            {
                capital = true;
            }
            else if(!Character.isLetter(c))
            {
                if(!Character.isDigit(c))
                {
                    if (!Character.isSpaceChar(c))
                        special = true;
                    else if (Character.isSpaceChar(c))
                        whitespace = true;

                }
            }
        }
        if (number && capital && special && minimumlength && !toolong && !whitespace)
            return true;
        else
            return false;
    }




}
