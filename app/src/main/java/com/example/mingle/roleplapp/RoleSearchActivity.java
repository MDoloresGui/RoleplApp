package com.example.mingle.roleplapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoleSearchActivity extends AppCompatActivity {

    EditText etSearch;
    ImageButton ibSend;
    ListView lvResults;
    ArrayList<RoleLine> roleLines = new ArrayList<>();
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_search);
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        initializeComponents();

        final RolesListAdapter adapter = new RolesListAdapter(RoleSearchActivity.this, R.layout.list_view_role_lines, roleLines);
        lvResults.setAdapter(adapter);

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strToSearch = etSearch.getText().toString().trim();
                final String finalStrToSearch = strToSearch;
                new AsyncTask() {

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("http://10.0.2.2/roleapp-api/public/api/rolelines/byregexp/" + finalStrToSearch)
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
                            roleLines.clear();
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
                                roleLines.add(tempRoleLine);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        super.onPostExecute(o);
                    }
                }.execute();
            }
        });

        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RoleSearchActivity.this, RoleLineVisualizerActivity.class);
                intent.putExtra("roleline", roleLines.get(i));
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    private void initializeComponents() {
        etSearch = findViewById(R.id.etSearch_roleSearch);
        ibSend = findViewById(R.id.ibSend_roleSearch);
        lvResults = findViewById(R.id.lvRoles_roleSearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_character_detailed, menu);
        getSupportActionBar().setTitle("Búsqueda de roles");
        menu.findItem(R.id.role_search_opt).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.role_search_opt:
                i = new Intent(RoleSearchActivity.this, RoleSearchActivity.class);
                i.putExtra("id", id);
                startActivity(i);
                break;
            case R.id.create_role_opt:
                i = new Intent(RoleSearchActivity.this, CreateRolLineActivity.class);
                i.putExtra("id", id);
                startActivity(i);
                break;
            case R.id.my_roles_opt:
                i = new Intent(RoleSearchActivity.this, MyRoleLinesActivity.class);
                i.putExtra("id", id);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
