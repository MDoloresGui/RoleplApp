package com.example.mingle.roleplapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainMenuActivity extends AppCompatActivity {

    private ListView lv;
    private Button btAdd;
    public final int CREATE_REQ_CODE = 2;
    private AsyncTask task;
    final ArrayList<CharacterClass> characters = new ArrayList<>();
    int actid;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_character_detailed, menu);
        getSupportActionBar().setTitle("Menú principal");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.role_search_opt:
                i = new Intent(MainMenuActivity.this, RoleSearchActivity.class);
                i.putExtra("id", actid);
                startActivity(i);
                break;
            case R.id.create_role_opt:
                i = new Intent(MainMenuActivity.this, CreateRolLineActivity.class);
                i.putExtra("id", actid);
                startActivity(i);
                break;
            case R.id.my_roles_opt:
                i = new Intent(MainMenuActivity.this, MyRoleLinesActivity.class);
                i.putExtra("id", actid);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle bundle = getIntent().getExtras();
        final int id = bundle.getInt("USER_ID");
        actid = id;

        lv = findViewById(R.id.lvCharacters);
        btAdd = findViewById(R.id.btAddCharacter_mainMenu);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainMenuActivity.this, CreateCharacterActivity.class);
                i.putExtra("id", id);
                startActivityForResult(i, CREATE_REQ_CODE);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainMenuActivity.this, CharacterDetailedActivity.class);
                intent.putExtra("character", characters.get(i));
                startActivity(intent);
            }
        });



        task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/users/" + id + "/characters")
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
                Log.d("json", o.toString());
                try {
                    String jsonToTreat = "{\"data\": " + o.toString() + " }";
                    JSONObject obj = new JSONObject(jsonToTreat);
                    JSONArray array = obj.getJSONArray("data");
                    characters.clear();
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
                        characters.add(cha);
                    }
                    CharactersListAdapter adapter = new CharactersListAdapter(MainMenuActivity.this, R.layout.list_view_characters, characters);
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(o);
            }
        };

        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_REQ_CODE && resultCode == RESULT_OK) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("http://10.0.2.2/roleapp-api/public/api/users/" + actid + "/characters")
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
                    Log.d("json", o.toString());
                    try {
                        String jsonToTreat = "{\"data\": " + o.toString() + " }";
                        JSONObject obj = new JSONObject(jsonToTreat);
                        JSONArray array = obj.getJSONArray("data");
                        characters.clear();
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
                            characters.add(cha);
                        }
                        CharactersListAdapter adapter = new CharactersListAdapter(MainMenuActivity.this, R.layout.list_view_characters, characters);
                        lv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    super.onPostExecute(o);
                }
            }.execute();
        }
    }
}
