package com.example.mingle.roleplapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText etName, etEmail, etPassword, etConfirmPassword;
    Button btRegister;

    private final String url = "http://10.0.2.2/roleapp-api/public/api/users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Registrarse");
        initializeComponents();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidations()) {
                    String jsonStr = null;
                    try {
                        jsonStr = buildJSON(
                                etName.getText().toString().trim(),
                                etEmail.getText().toString().trim(),
                                etPassword.getText().toString().trim()
                        );

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient http = new OkHttpClient();

                        RequestBody body = RequestBody.create(JSON, jsonStr);
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .build();
                        http.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("response failed", call.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseStr = response.body().string();
                                if (checkResponseNumeric(responseStr)) {
                                    Intent i = new Intent();
                                    i.putExtra("id", Integer.parseInt(responseStr));
                                    setResult(RESULT_OK, i);
                                    finish();
                                } else {
                                    SignUpActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            etName.setError("Email y/o nombre y existentes");
                                            etEmail.setError("Email y/o nombre y existentes");
                                        }
                                    });
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkResponseNumeric(String response) {
        char[] cad = response.toCharArray();
        boolean allOk = true;
        for (char c:
             cad) {
            if (!Character.isDigit(c)) {
                allOk = false;
            }
        }
        return allOk;
    }

    private String buildJSON(String name, String email, String pass) throws JSONException {
        return new JSONObject()
                .put("name", name)
                .put("email", email)
                .put("password", pass)
                .toString();
    }

    private boolean checkValidations() {
        boolean allOk = true;
        if (!isNameValid(etName.getText().toString().trim())) {
            allOk = false;
            etName.setError("Sólo se permiten caracteres alfanuméricos");
        }
        if (!isEmailValid(etEmail.getText().toString().trim())) {
            allOk = false;
            etEmail.setError("El email no es válido");
        }
        if (etName.getText().toString().trim().length() < 4
                || etName.getText().toString().trim().length() > 20) {
            allOk = false;
            etName.setError("Los nombres deben tener al menos 4 caracteres y un máximo de 20");
        }
        if (etPassword.getText().toString().trim().length() < 6) {
            allOk = false;
            etPassword.setError("La contraseña debe ser de al menos 6 caracteres");
        }
        if (!etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
            allOk = false;
            etConfirmPassword.setError("Las contraseñas no coinciden");
        }

        return allOk;
    }

    private boolean isNameValid(String name) {
        String expression = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void initializeComponents() {
        etName = findViewById(R.id.etName_signup);
        etPassword = findViewById(R.id.etPassword_signup);
        etEmail = findViewById(R.id.etEmail_signup);
        etConfirmPassword = findViewById(R.id.etConfirmPassword_signup);
        btRegister = findViewById(R.id.btRegister_signup);
    }
}
