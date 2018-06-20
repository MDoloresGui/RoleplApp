package com.example.mingle.roleplapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoleLineVisualizerActivity extends AppCompatActivity {

    RoleLine roleLine;
    int userId;
    ArrayList<Integer> participantsIds = new ArrayList<>();
    ArrayList<CharacterClass> userCharacters = new ArrayList<>();
    ArrayList<String> charactersToShow = new ArrayList<>();
    ArrayList<Post> posts = new ArrayList<>();
    Spinner spChar;
    TextView tvCreate, tvUpdate, tvUniverse;
    ListView lv;
    Button bt;
    boolean participates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_line_visualizer);

        Bundle extras = getIntent().getExtras();
        roleLine = ((RoleLine) extras.get("roleline"));
        userId = extras.getInt("id");

        getSupportActionBar().setTitle(roleLine.getTitle());
        initialize();

        spChar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (participantsIds.contains(userCharacters.get(i).getId())) {
                    bt.setVisibility(View.VISIBLE);
                    participates = true;
                } else {
                    bt.setVisibility(View.GONE);
                    participates = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final PostsAdapter postsAdapter = new PostsAdapter(RoleLineVisualizerActivity.this, R.layout.list_view_posts, posts);
        lv.setAdapter(postsAdapter);

        final AsyncTask charactersTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/users/" + userId + "/characters")
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
                    String jsonToTreat = "{\"data\": " + o.toString() + " }";
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
                        userCharacters.add(cha);
                        if (participantsIds.contains(cha.getId()))
                            charactersToShow.add(cha.getName() + " #" + cha.getId() + " (participa)");
                        else
                            charactersToShow.add(cha.getName() + " #" + cha.getId());
                    }
                    spChar.setAdapter(new ArrayAdapter<>(RoleLineVisualizerActivity.this, R.layout.spinner_white_item, charactersToShow));
                    spChar.setSelection(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(o);
            }
        };
        AsyncTask participantsTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/rolelines/" + roleLine.getId() + "/participants")
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
                    String jsonToTreat = "{\"data\": " + o.toString() + " }";
                    JSONObject obj = new JSONObject(jsonToTreat);
                    JSONArray array = obj.getJSONArray("data");
                    participantsIds.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        participantsIds.add(temp.getInt("rol_cha_id_char"));
                    }
                    charactersTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(o);
            }
        };
        AsyncTask universeTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/universes/" + roleLine.getIdUniverse())
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
                super.onPostExecute(o);

                String json = o.toString().substring(1, o.toString().length() - 1);
                try {
                    JSONObject obj = new JSONObject(json);
                    tvUniverse.setText(obj.getString("uni_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        AsyncTask postsTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/rolelines/" + roleLine.getId() + "/posts")
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
                    String jsonToTreat = "{\"data\": " + o.toString() + " }";
                    JSONObject obj = new JSONObject(jsonToTreat);
                    JSONArray array = obj.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        Post postTemp = new Post(
                                temp.getInt("pos_id"),
                                temp.getString("pos_creation_date"),
                                temp.getString("pos_content"),
                                temp.getInt("pos_id_rol"),
                                temp.getInt("pos_id_char")
                        );
                        posts.add(postTemp);
                    }
                    postsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(o);
            }
        };

        participantsTask.execute();
        universeTask.execute();
        postsTask.execute();
        tvCreate.setText("Creado el: " + roleLine.getCreationDate());
        tvUpdate.setText("Actualizado el: " + roleLine.getLastUpdateDate());
    }


    private void initialize() {
        spChar = findViewById(R.id.spCharacters_visualizer);
        tvCreate = findViewById(R.id.tvCreateDate_visualizer);
        tvUpdate = findViewById(R.id.tvUpdatedDate_visualizer);
        tvUniverse = findViewById(R.id.tvUniverse_visualizer);
        lv = findViewById(R.id.lvPosts_visualizer);
        bt = findViewById(R.id.btAnswer_visualizer);
    }
}
