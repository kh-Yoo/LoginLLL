package com.example.loginlll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MaterialEditText id, password;
    Button login, register, find_id, find_pw;
    CheckBox loginState;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        loginState = findViewById(R.id.checkbox);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        find_id = findViewById(R.id.find_id);
        find_pw = findViewById(R.id.find_pw);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String txtId = id.getText().toString();
                String txtPassword = password.getText().toString();
                if (TextUtils.isEmpty(txtId) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(MainActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else {
                    login(txtId, txtPassword);
                }
            }
        });

        String loginStatus = sharedPreferences.getString(getResources().getString(R.string.prefLoginState), "");
        if (loginStatus.equals("loggedin")) {
            startActivity(new Intent(MainActivity.this, AppStartActivity.class));
        }
        find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, FindActivity.class));
                finish();
            }
        });

        find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, FindActivity.class));
                finish();
            }
        });

    }

    private void login(final String userid, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Logining~");
        progressDialog.show();

        String uRl = "http://ykh3587.dothome.co.kr/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("Login Success")) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();/*
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (loginState.isChecked()) {
                        editor.putString(getResources().getString(R.string.prefLoginState), "loggedin");
                    }
                    else {
                        editor.putString(getResources().getString(R.string.prefLoginState), "loggedout");
                    }
                    editor.apply();
                    startActivity(new Intent(MainActivity.this, AppStartActivity.class));*/
                }

                else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("userid", userid);
                param.put("psw", password);

                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(MainActivity.this).addToRequestQueue(request);

    }

}
