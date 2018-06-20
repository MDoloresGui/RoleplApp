package com.example.mingle.roleplapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyRoleLinesActivity extends AppCompatActivity {

    private int id;
    private ArrayList<RoleLine> roles = new ArrayList<>();
    private ListView lv;
    RolesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_role_lines);
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        lv = findViewById(R.id.lvRoles_myRoleLines);

        adapter = new RolesListAdapter(MyRoleLinesActivity.this, R.layout.list_view_role_lines, roles);
        lv.setAdapter(adapter);

        loadRoleLines();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyRoleLinesActivity.this, RoleLineVisualizerActivity.class);
                intent.putExtra("roleline", roles.get(i));
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    private void loadRoleLines() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/users/" + id + "/rolelines")
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
                    roles.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject temp = array.getJSONObject(i);
                        RoleLine tempRoleLine = new RoleLine(
                                temp.getInt("rol_id"),
                                temp.getString("rol_title"),
                                temp.getString("rol_creation_date"),
                                temp.getString("rol_last_update_date"),
                                temp.getInt("rol_id_master"),
                                temp.getInt("rol_id_universe"),
                                temp.getString("rol_description")
                        );
                        roles.add(tempRoleLine);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(o);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_character_detailed, menu);
        getSupportActionBar().setTitle("Mis roles activos");
        menu.findItem(R.id.my_roles_opt).setVisible(false);
        return true;
    }
}
