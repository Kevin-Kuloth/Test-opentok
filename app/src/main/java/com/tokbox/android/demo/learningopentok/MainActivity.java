package com.tokbox.android.demo.learningopentok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kevin on 11/30/2016.
 */

public class MainActivity extends Activity {

    LinearLayout calllay;
    ImageView adduser;
    EditText username;
    public static final String BASE_URL = " http://114.69.235.57:7058/";
    Retrofit retrofit;
    MyApiEndpointInterface apiService;
    UserCallrequest userCallrequest;
    Usercreate usercreate;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        token = FirebaseInstanceId.getInstance().getToken();
        // Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
        Log.d("Token", "Refreshed token: " + token);

        calllay = (LinearLayout) findViewById(R.id.calllay);
        adduser = (ImageView) findViewById(R.id.adduser);
        username = (EditText) findViewById(R.id.username);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(MyApiEndpointInterface.class);

        calllay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inten = new Intent(MainActivity.this,ChatActivity.class);
                startActivity(inten);

                List<String> users = new ArrayList<String>();

                users.add("583fb7c7268b8c4c376d9c48");

                userCallrequest = new UserCallrequest();

                userCallrequest.setUsers(users);

                callAPi();

            }
        });

        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usercreate = new Usercreate();
                usercreate.setName( username.getText().toString().trim());
                usercreate.setDeviceToken(token);
                usercreate.setPlatform("android");
                createuserAPI();
            }
        });

    }

    void callAPi(){

        Call<User> call = apiService.call(userCallrequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Toast.makeText(MainActivity.this,"retrofit success", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Toast.makeText(MainActivity.this,"retrofit error"+t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void createuserAPI(){

        Call<Usercreated> usercreates = apiService.usercreate(usercreate);
        usercreates.enqueue(new Callback<Usercreated>() {
            @Override
            public void onResponse(Call<Usercreated> call, Response<Usercreated> response) {

                Toast.makeText(MainActivity.this,"retrofit success"+response.body().getName(), Toast.LENGTH_LONG).show();


            }
            @Override
            public void onFailure(Call<Usercreated> call, Throwable t) {

                Toast.makeText(MainActivity.this,"retrofit error"+t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void getuser(){

        Call<User> call = apiService.call(userCallrequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Toast.makeText(MainActivity.this,"retrofit success", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Toast.makeText(MainActivity.this,"retrofit error"+t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
