package com.example.mingle.roleplapp;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
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

public class PostDialog {
    public interface OnDialogPosted {
        void dialogPosted();
    }

    public PostDialog(final Context context, final OnDialogPosted onDialogPosted, final int rolId, final int charId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.post_dialog);

        final EditText et = dialog.findViewById(R.id.etPost_dialogPost);
        Button btCancel = dialog.findViewById(R.id.btCancel_dialogPost);
        Button btSend = dialog.findViewById(R.id.btSend_dialogPost);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et.getText().toString().trim().isEmpty()) {
                    et.setError("El contenido de un post no puede estar vacío");
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("content", et.getText().toString().trim());
                        jsonObject.put("id_char", charId);
                        jsonObject.put("id_rol", rolId);

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient http = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                        Request request = new Request.Builder()
                                .url("http://10.0.2.2/roleapp-api/public/api/posts")
                                .post(body)
                                .addHeader("content-type", "application/json; charset=utf-8")
                                .build();
                        http.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(context, "Fallo al postear", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            @Override
                            public void onResponse(Call call, Response response) {
                                onDialogPosted.dialogPosted();
                                dialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dialog.show();
    }
}
