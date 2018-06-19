package com.example.mingle.roleplapp;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomDialogUniverse {
    final String url = "http://10.0.2.2/roleapp-api/public/api/universes";

    public interface OnUniverseAdded {
        void updateSpinner();
    }

    public CustomDialogUniverse(final Context context, final OnUniverseAdded onUniverseAdded) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.universe_dialog);

        final EditText etName = dialog.findViewById(R.id.etName_dialog);
        Button btOK = dialog.findViewById(R.id.btOK_dialog), btCancel = dialog.findViewById(R.id.btCancel_dialog);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    etName.setError("El campo no puede estar vacío");
                } else {
                    new AsyncTask() {

                        @Override
                        protected Object doInBackground(Object[] objects) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("name", name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            OkHttpClient http = new OkHttpClient();
                            Log.d("json to post", jsonObject.toString());
                            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .addHeader("content-type", "application/json; charset=utf-8")
                                    .build();

                            http.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Toast.makeText(context, "Fallo al postear...", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    onUniverseAdded.updateSpinner();
                                    dialog.dismiss();
                                }
                            });
                            return null;
                        }
                    }.execute();
                }
            }
        });

        dialog.show();
    }
}
