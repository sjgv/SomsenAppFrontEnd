package com.example.somsennodejsapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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

public class AccountCreationActivity extends AppCompatActivity {

    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    TextView txt_welcome;
    MaterialButton btn_create_account;


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
        setContentView(R.layout.activity_account_creation);
        txt_welcome = (TextView)findViewById(R.id.welcome_text);
        btn_create_account = (MaterialButton)findViewById(R.id.create_account_button);

        //Intialize API
        Retrofit retrofit = RetrofitClient.getInstance(); //Allows us to parse json
        loginAPI = retrofit.create(INodeJS.class);

        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        final View enter_account_info = LayoutInflater.from(this).inflate(R.layout.enter_account_information, null);

        new MaterialStyledDialog.Builder(this)
                .setTitle("Account Information")
                .setCustomView(enter_account_info)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Done")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        final MaterialEditText edit_name =(MaterialEditText)enter_account_info.findViewById(R.id.edit_name);
                        final MaterialEditText edit_last_name = (MaterialEditText)enter_account_info.findViewById(R.id.edit_lastname);
                        final MaterialEditText edit_state = (MaterialEditText)enter_account_info.findViewById(R.id.edit_state);
                        final MaterialEditText edit_city = (MaterialEditText)enter_account_info.findViewById(R.id.edit_city);

                        compositeDisposable.add(loginAPI.setAccount(UserState.getInstance().unique_id,
                                edit_name.getText().toString(),edit_last_name.getText().toString(), edit_state.getText().toString(),edit_city.getText().toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        UserState.getInstance().name = edit_name.getText().toString();
                                        UserState.getInstance().lastname = edit_last_name.getText().toString();
                                        UserState.getInstance().state = edit_state.getText().toString();
                                        UserState.getInstance().city = edit_city.getText().toString();

                                        Toast.makeText(AccountCreationActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        );
                    }
                }).show();

    }

}
