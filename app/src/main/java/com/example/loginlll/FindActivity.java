package com.example.loginlll;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class FindActivity extends AppCompatActivity {
    MaterialEditText code, email;
    Button transfer, confirm;
    TextView youremail;
    Random rand = new Random();
    int emailcode = 0;
    ArrayList<HashMap<String, String>> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        email = findViewById(R.id.email);
        code = findViewById(R.id.code);
        transfer = findViewById(R.id.transfer);
        confirm = findViewById(R.id.confirm);
        youremail = findViewById(R.id.youremail);


        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailcode = 0;
                String txtEmail = email.getText().toString();
                if (android.os.Build.VERSION.SDK_INT > 9) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                }
                if (isEmail(txtEmail) == false) {
                    Toast.makeText(FindActivity.this, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                } else {
                    find(txtEmail);

                }
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailcode == Integer.parseInt(code.getText().toString())) {
                    showme();
                    youremail.setVisibility(View.VISIBLE);
                    Toast.makeText(FindActivity.this, "인증번호가 맞습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    youremail.setVisibility((View.INVISIBLE));
                    Toast.makeText(FindActivity.this, "잘못된 인증번호입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static boolean isEmail(String email) {
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            returnValue = true;
        }
        return returnValue;
    }

    private void find(final String Useremail) {
        final ProgressDialog progressDialog = new ProgressDialog(FindActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Sending Email~");
        progressDialog.show();

        String uRl = "http://ykh3587.dothome.co.kr/find.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("right email")) {
                    MailSender Sender = new MailSender("ykhykh3587@gmail.com", "qexjxbdiizsoqwyj");
                    Toast.makeText(FindActivity.this, response, Toast.LENGTH_SHORT).show();
                    confirm.setVisibility(View.VISIBLE);
                    code.setVisibility(View.VISIBLE);
                    try {
                        int[] value = new int[4];
                        int im = 0;
                        int wait = 1000;
                        for (int a = 0; a < 4; a++) {
                            for (int i = 0; i < 100; i++) {
                                value[a] = rand.nextInt(10);
                                if (value[0] == 0)
                                    value[0]++;
                            }
                        }
                        for (int b = 0; b < 4; b++) {
                            im = value[b] * wait;
                            wait = wait / 10;
                            emailcode += im;
                        }
                        Sender.sendMail(
                                "[ Oishi 인증번호 ]",
                                "\n\n 안녕하세요! Oishi입니다.\n 당신의 인증번호는 [" + emailcode + "] 입니다.",
                                "ykhykh3587@gamil.com",
                                "" + email.getText().toString() + ""
                        );
                        progressDialog.dismiss();
                        Toast.makeText(FindActivity.this, "인증번호를 전송했습니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(FindActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FindActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("email", Useremail);
                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(FindActivity.this).addToRequestQueue(request);

    }

    private void showme() {

        String uRl = "http://ykh3587.dothome.co.kr/showme.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    try {
                        //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
                        JSONObject jsonObject = new JSONObject(response);


                        //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
                        JSONArray jsonArray = jsonObject.getJSONArray("response");
                        String txtEmail = email.getText().toString();
                        String userid, email;
                        int count = 0;
                        while (count < jsonArray.length()) {
                            JSONObject object = jsonArray.getJSONObject(count);

                            //count는 배열의 인덱스를 의미
                            userid = object.getString("userid");//여기서 ID가 대문자임을 유의
                            email = object.getString("email");
                            if(txtEmail == email)
                            {
                                youremail.setText("D");
                            }
                            count++;
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FindActivity.this, response, Toast.LENGTH_SHORT).show();
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FindActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(FindActivity.this).addToRequestQueue(request);

    }


    //모든회원에 대한 정보를 가져오기 위한 쓰레드
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://ykh3587.dothome.co.kr/showme.php";
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(target);//URL 객체 생성

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();

                //한줄씩 읽어서 stringBuilder에 저장함
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
                JSONObject jsonObject = new JSONObject(result);


                //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                String txtEmail = email.getText().toString();
                String userid, email;
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    //count는 배열의 인덱스를 의미
                    userid = object.getString("userid");//여기서 ID가 대문자임을 유의
                    email = object.getString("email");
                    if(txtEmail == email)
                    {
                        youremail.setText("D");
                    }
                    count++;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

}
