package com.example.mingle.roleplapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RolesListAdapter extends ArrayAdapter<RoleLine> {
    private Context context;
    private int resource;
    private List<RoleLine> roles;

    public RolesListAdapter(@NonNull Context context, int resource, @NonNull List<RoleLine> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.roles = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resource, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle_roleList);
        TextView tvDate = view.findViewById(R.id.tvDate_roleList);
        TextView tvDesc = view.findViewById(R.id.tvDescription_roleList);
        final TextView tvMaster = view.findViewById(R.id.tvMaster_roleList);
        final TextView tvUniverse = view.findViewById(R.id.tvUniverse_roleList);

        tvTitle.setText(roles.get(position).getTitle());
        tvDate.setText(roles.get(position).getLastUpdateDate());
        tvDesc.setText(roles.get(position).getDescription());

        final int masterId = roles.get(position).getMasterId();
        final int universeId = roles.get(position).getIdUniverse();

        AsyncTask masterTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/characters/" + masterId)
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
                    tvMaster.setText(obj.getString("cha_name") + " #" + obj.getInt("cha_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        AsyncTask universeTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/universes/" + universeId)
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

        masterTask.execute();
        universeTask.execute();

        return view;
    }
}
