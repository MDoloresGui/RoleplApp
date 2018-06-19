package com.example.mingle.roleplapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CharactersListAdapter extends ArrayAdapter<CharacterClass> {
    private Context context;
    private int resource;
    private List<CharacterClass> characterList;
    private Application application;

    public CharactersListAdapter(@NonNull Context context, int resource, @NonNull List<CharacterClass> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        characterList = objects;
        this.application = application;
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(resource, null);

        TextView tvName = v.findViewById(R.id.tvName_characterList);
        final TextView tvUniverse = v.findViewById(R.id.tvUniverse_characterList);
        ImageView ivAvatar = v.findViewById(R.id.ivAvatar_characterList);

        tvName.setText(characterList.get(position).getName() + " #" + characterList.get(position).getId());
        tvUniverse.setText(characterList.get(position).getUniverseId() + "");

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/universes/" + characterList.get(position).getUniverseId())
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

        task.execute();

        Picasso.get()
                .load(characterList.get(position).getAvatar())
                .placeholder(R.drawable.ic_face)
                .into(ivAvatar);

        return v;
    }
}
