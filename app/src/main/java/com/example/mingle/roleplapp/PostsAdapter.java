package com.example.mingle.roleplapp;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostsAdapter extends ArrayAdapter<Post> {

    private Context context;
    private int resource;
    private List<Post> posts;
    private CharacterClass character;

    public PostsAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.posts = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(resource, null);

        final ImageView iv = v.findViewById(R.id.ivAvatar_post);
        final TextView tvName = v.findViewById(R.id.tvName_post),
                tvDate = v.findViewById(R.id.tvDate_post),
                tvContent = v.findViewById(R.id.tvContent_post);

        tvDate.setText(posts.get(position).getCreationDate());
        tvContent.setText(posts.get(position).getContent());

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2/roleapp-api/public/api/characters/" + posts.get(position).getIdChar())
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
                    character = new CharacterClass(
                            obj.getInt("cha_id"),
                            obj.getString("cha_name"),
                            obj.getString("cha_avatar"),
                            obj.getString("cha_biography"),
                            obj.getString("cha_index_card"),
                            obj.getInt("cha_id_universe"),
                            obj.getInt("cha_id_user")
                    );
                    tvName.setText(character.getName() + " #" + character.getId());
                    Picasso.get()
                            .load(character.getAvatar())
                            .placeholder(R.drawable.ic_face)
                            .into(iv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CharacterDetailedActivity.class);
                intent.putExtra("character", character);
                context.startActivity(intent);
            }
        });

        return v;
    }
}
