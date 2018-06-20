package com.example.mingle.roleplapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateRolLineActivity extends AppCompatActivity {

    EditText etTitle, etDesc;
    ListView lvPart;
    Button btSend;
    Spinner spUni, spMaster;
    ArrayList<String> universesToShow = new ArrayList<>();
    ArrayList<Integer> universesIndexes = new ArrayList<>();
    ArrayList<CharacterClass> charactersBack = new ArrayList<>();
    ArrayList<CharacterClass> charactersToManage = new ArrayList<>();
    ArrayList<String> charactersToShow = new ArrayList<>();
    ArrayList<CharacterClass> charactersPart = new ArrayList<>();

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rol_line);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        id = extras.getInt("id");

        getSupportActionBar().setTitle("Crear línea de rol");
        initializeComponents();
        populateUniverses();
        populateCharacters();
        populateCharactersToManage();

        lvPart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        etDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lvPart.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvPart.setItemsCanFocus(false);
        lvPart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (charactersPart.contains(charactersToManage.get(i))) {
                    lvPart.setItemChecked(i, false);
                    charactersPart.remove(charactersToManage.get(i));
                    Log.d("array length", charactersPart.size() + "");
                } else {
                    lvPart.setItemChecked(i, true);
                    charactersPart.add(charactersToManage.get(i));
                    Log.d("array length", charactersPart.size() + "");
                }
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    if (!charactersPart.contains(charactersBack.get(spMaster.getSelectedItemPosition()))) {
                        charactersPart.add(charactersBack.get(spMaster.getSelectedItemPosition()));
                    }

                    postRoleLine();
                }
            }
        });
    }

    private void postRoleLine() {
        String jsonStr = null;
        try {
            jsonStr = new JSONObject()
                    .put("title", etTitle.getText().toString().trim())
                    .put("master_id", charactersBack.get(spMaster.getSelectedItemPosition()).getId())
                    .put("universe_id", universesIndexes.get(spUni.getSelectedItemPosition()))
                    .put("rol_desc", etDesc.getText().toString().trim())
                    .toString();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient http = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url("http://10.0.2.2/roleapp-api/public/api/rolelines")
                    .post(body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();

            http.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseStr = response.body().string();
                    if (checkResponseNumeric(responseStr)) {
                        int rolId = Integer.parseInt(responseStr);
                        for (CharacterClass cha:
                             charactersPart) {
                            postChaRole(cha.getId(), rolId);
                        }
                        finish();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void postChaRole(int id, int rolId) {
        String jsonStr = null;

        try {
            jsonStr = new JSONObject()
                    .put("id_rol", rolId)
                    .put("id_char", id)
            .toString();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient http = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url("http://10.0.2.2/roleapp-api/public/api/rolechars")
                    .post(body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();

            http.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("res", response.body().string() + " added");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void populateCharactersToManage() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();
                Log.d("async2", "");
                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/characters").build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String str = response.body().string();
                    Log.d("res", str);
                    return str;
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
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        CharacterClass cha = new CharacterClass(
                                temp.getInt("cha_id"),
                                temp.getString("cha_name"),
                                temp.getString("cha_avatar"),
                                temp.getString("cha_biography"),
                                temp.getString("cha_index_card"),
                                temp.getInt("cha_id_user"),
                                temp.getInt("cha_id_universe")
                        );
                        charactersToManage.add(cha);
                    }
                    Log.d("eh", jsonToTreat);
                    CharactersListAdapter adapter = new CharactersListAdapter(CreateRolLineActivity.this, R.layout.list_view_characters, charactersToManage);
                    lvPart.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void populateCharacters() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("async2", "");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/users/" + id + "/characters").build();

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
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        CharacterClass cha = new CharacterClass(
                                temp.getInt("cha_id"),
                                temp.getString("cha_name"),
                                temp.getString("cha_avatar"),
                                temp.getString("cha_biography"),
                                temp.getString("cha_index_card"),
                                temp.getInt("cha_id_user"),
                                temp.getInt("cha_id_universe")
                        );
                        charactersBack.add(cha);
                        charactersToShow.add(cha.getName() + " #" + cha.getId() + " (" + universesToShow.get(universesIndexes.indexOf(cha.getUniverseId())) + ")");
                    }
                    spMaster.setAdapter(new ArrayAdapter<>(CreateRolLineActivity.this, R.layout.support_simple_spinner_dropdown_item, charactersToShow));
                    spMaster.setSelection(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    private void populateUniverses() {
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
                spUni.setAdapter(new ArrayAdapter<>(CreateRolLineActivity.this, R.layout.support_simple_spinner_dropdown_item, universesToShow));
                spUni.setSelection(0);
            }
        }.execute();
    }

    private void initializeComponents() {
        etTitle = findViewById(R.id.etTitle_createRole);
        etDesc = findViewById(R.id.etDesc_createRoles);
        lvPart = findViewById(R.id.lvPart_createRole);
        btSend = findViewById(R.id.btCreate_createRole);
        spMaster = findViewById(R.id.spMaster_createRole);
        spUni = findViewById(R.id.spUniverses_createRole);
    }

    private boolean checkFields() {
        boolean res = true;

        if (etTitle.getText().toString().trim().isEmpty()) {
            res = false;
            etTitle.setError("El título no puede estar vacío");
        }

        if (etDesc.getText().toString().trim().isEmpty()) {
            res = false;
            etDesc.setError("La descripción no puede quedar vacía");
        }

        if (charactersBack.get(spMaster.getSelectedItemPosition()).getUniverseId() != universesIndexes.get(spUni.getSelectedItemPosition())) {
            res = false;
            Toast.makeText(this, "El universo del master debe coincidir con el del rol", Toast.LENGTH_LONG).show();
        }

        if (charactersPart.isEmpty()) {
            res = false;
            Toast.makeText(this, "Debe haber al menos un participante", Toast.LENGTH_SHORT).show();
        }

        return res;
    }
}
