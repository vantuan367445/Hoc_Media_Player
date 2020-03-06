package com.example.tuanvatvo.hoc_media_player.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tuanvatvo.hoc_media_player.R;
import com.example.tuanvatvo.hoc_media_player.model.ModelSong;

import java.util.ArrayList;

public class AdapterSong extends ArrayAdapter<ModelSong> {
    Activity context;
    int resource;
    @NonNull ArrayList<ModelSong> array;

    public AdapterSong(@NonNull Activity context, int resource, @NonNull ArrayList<ModelSong> array) {
        super(context, resource, array);
        this.array = array;
        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View view = inflater.inflate(this.resource,null);

        TextView txt_titleSong = view.findViewById(R.id.txt_titleSong);
        TextView txt_artistSong = view.findViewById(R.id.txt_artistSong);
        ModelSong modelSong = array.get(position);

        txt_titleSong.setText(modelSong.getTitle());;
        txt_artistSong.setText(modelSong.getArtist());


        return view;
    }
}
