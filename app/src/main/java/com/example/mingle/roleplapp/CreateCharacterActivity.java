package com.example.mingle.roleplapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateCharacterActivity extends AppCompatActivity implements CustomDialogUniverse.OnUniverseAdded {
    private final String url = "http://10.0.2.2/roleapp-api/public/api/characters";
    private EditText etName, etBio, etCard;
    private Button btAvatar, btSend;
    private TextView tvCreateUni;
    private ImageView ivAvatar;
    private Spinner spUni;
    private int id;
    Uri pathSelect = null;
    ArrayList<String> universesToShow = new ArrayList<>();
    ArrayList<Integer> universesIndexes = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");

        setContentView(R.layout.activity_create_character);
        etBio = findViewById(R.id.etBiography_createCharacter);
        etCard = findViewById(R.id.etCard_createCharacter);
        etName = findViewById(R.id.etName_createCharacter);
        btAvatar = findViewById(R.id.btAvatar_createCharacter);
        btSend = findViewById(R.id.btSend_createCharacter);
        tvCreateUni = findViewById(R.id.tvCreateUniverse_createCharacter);
        ivAvatar = findViewById(R.id.ivAvatar_createCharacter);
        spUni = findViewById(R.id.spUniverses_createCharacter);

        etBio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        etCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        tvCreateUni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogUniverse dialogUniverse = new CustomDialogUniverse(CreateCharacterActivity.this, CreateCharacterActivity.this);
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    Cloudinary cloudinary = new Cloudinary(CloudinaryConfiguration.getConfig());
                    try {

                        String[] projection = { MediaStore.Images.Media.DATA };
                        Cursor cur = managedQuery(pathSelect, projection, null, null, null);
                        cur.moveToFirst();
                        String path = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));

                        File mediaFile = new File(path);
                        final FileInputStream is = new FileInputStream(mediaFile);
                        final Uploader uploader = cloudinary.uploader();
                        @SuppressLint("StaticFieldLeak") AsyncTask tpm = new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] objects) {
                                try {
                                    return uploader.upload(is, new HashMap());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                String name = etName.getText().toString().trim();
                                String bio = etBio.getText().toString().trim();
                                String card = etCard.getText().toString().trim();
                                int selectedUni = universesIndexes.get(spUni.getSelectedItemPosition());
                                String avatar = (String) ((Map) o).get("url");

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("name", name);
                                    jsonObject.put("avatar", avatar);
                                    jsonObject.put("biography", bio);
                                    jsonObject.put("index_card", card);
                                    jsonObject.put("id_universe", selectedUni);
                                    jsonObject.put("id_user", id);
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

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        Log.d("res", response.body().string());
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });

                            }
                        };
                        tpm.execute();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

        AsyncTask populateSpinner = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/universes").build();

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
                super.onPostExecute(o);

                String jsonToTreat = "{\"data\": " + o.toString() + " }";
                try {
                    JSONObject obj = new JSONObject(jsonToTreat);
                    JSONArray array = obj.getJSONArray("data");
                    universesToShow.clear();
                    universesIndexes.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        universesIndexes.add(temp.getInt("uni_id"));
                        universesToShow.add(temp.getString("uni_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                spUni.setAdapter(new ArrayAdapter<>(CreateCharacterActivity.this, R.layout.support_simple_spinner_dropdown_item, universesToShow));
            }
        };

        populateSpinner.execute();

        btAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
    }

    private void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione el selector de imágenes"), 10);
    }

    private boolean checkFields() {
        boolean res = true;

        if (etName.getText().toString().trim().isEmpty() || etName.getText().toString().trim().length() > 49) {
            etName.setError("Nombre inválido");
            res = false;
        }

        if (etBio.getText().toString().trim().isEmpty() || etBio.getText().toString().trim().length() > 254) {
            etBio.setError("Biografía inválida");
            res = false;
        }

        if (etCard.getText().toString().trim().isEmpty()) {
            etCard.setError("Ficha inválida");
            res = false;
        }

        if (spUni.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Seleccione un universo", Toast.LENGTH_LONG).show();
            res = false;
        }

        if (pathSelect == null) {
            Toast.makeText(this, "Seleccione un avatar", Toast.LENGTH_LONG).show();
            res = false;
        }

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10 && resultCode == RESULT_OK) {
            Uri path = data.getData();
            ivAvatar.setImageURI(path);
            pathSelect = path;
        }
    }

    @Override
    public void updateSpinner() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/universes").build();

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
                super.onPostExecute(o);

                String jsonToTreat = "{\"data\": " + o.toString() + " }";
                try {
                    JSONObject obj = new JSONObject(jsonToTreat);
                    JSONArray array = obj.getJSONArray("data");
                    universesToShow.clear();
                    universesIndexes.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        universesIndexes.add(temp.getInt("uni_id"));
                        universesToShow.add(temp.getString("uni_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                spUni.setAdapter(new ArrayAdapter<>(CreateCharacterActivity.this, R.layout.support_simple_spinner_dropdown_item, universesToShow));
                spUni.setSelection(0);
            }
        }.execute();
    }
}
