package com.example.mingle.roleplapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CharacterDetailedActivity extends AppCompatActivity {

    private CharacterClass character;
    private TextView tvBio, tvCard, tvName;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detailed);

        Bundle bundle = getIntent().getExtras();
        character = ((CharacterClass) bundle.get("character"));

        tvBio = findViewById(R.id.tvBio_characterDetailed);
        tvCard = findViewById(R.id.tvCard_characterDetailed);
        tvName = findViewById(R.id.tvName_characterDetailed);
        iv = findViewById(R.id.ivAvatar_characterDetailed);
        getSupportActionBar().setTitle("Detalles de " + character.getName());
        tvBio.setText(character.getBiography());
        tvName.setText(character.getName() + " #" + character.getId());
        tvCard.setText(character.getIndexCard());

        Picasso.get()
                .load(character.getAvatar())
                .placeholder(R.drawable.ic_face)
                .into(iv);
    }
}
