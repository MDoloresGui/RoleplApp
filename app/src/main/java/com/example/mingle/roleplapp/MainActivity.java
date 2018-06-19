package com.example.mingle.roleplapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btLogin;
    private TextView tvSignUp;
    public final static int SIGNUP_REQ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Inicio de sesión");

        initializeComponents();
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();
                final String pass = etPassword.getText().toString();

                @SuppressLint("StaticFieldLeak") AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("http://10.0.2.2/roleapp-api/public/api/users/" + email)
                                .build();

                        Response response = null;

                        try {
                            response = client.newCall(request).execute();
                            return response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        try {
                            String json = o.toString().substring(1, o.toString().length() - 1);

                            Log.d("json: ", o.toString());
                            JSONObject obj = new JSONObject(json);
                            User user = new User(obj.getInt("use_id"),
                                    obj.getString("use_name"),
                                    obj.getString("use_email"),
                                    obj.getString("use_password"));
                            if (AeSimpleSHA1.SHA1(pass).equals(user.getUse_password())) {
                                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                                intent.putExtra("USER_ID", user.getUse_id());
                                startActivity(intent);
                            } else {
                                etPassword.setError("Contraseña incorrecta");
                            }
                            Log.d("result: ", user.toString());

                        } catch (JSONException e) {
                            Log.d("error", e.getLocalizedMessage());
                            etEmail.setError("Email incorrecto");

                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                };
                if (isEmailValid(email)) {
                    asyncTask.execute();
                } else {
                    etEmail.setError("Email no válido");
                }

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivityForResult(intent, SIGNUP_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SIGNUP_REQ && resultCode == RESULT_OK) {
            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            intent.putExtra("USER_ID", data.getExtras().getInt("id"));
            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeComponents() {
        etEmail = findViewById(R.id.etEmail_login);
        etPassword = findViewById(R.id.etPassword_login);
        btLogin = findViewById(R.id.btLogin_login);
        tvSignUp = findViewById(R.id.tvSignup_login);
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
