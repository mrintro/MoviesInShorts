package com.example.moviesinshorts.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Arrays;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private List<MovieModel> movieModels;
    private RecyclerView recyclerView;
    private final OnMovieOnClick onMovieOnClick;

    public RecyclerAdapter(RecyclerView recyclerView, OnMovieOnClick onMovieOnClick) {
        this.recyclerView = recyclerView;
        this.onMovieOnClick = onMovieOnClick;
    }

    public MovieModel getMovieModel(int position){
        if(movieModels.size()<position) return null;
        return movieModels.get(position);
    }

    public void setMovieModels(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/"+movieModels.get(position).getPoster_path())
                .into((holder).imageView);
        (holder).title.setText(movieModels.get(position).getTitle());
        (holder).detail.setText(movieModels.get(position).getOverview());
        holder.language.setText(movieModels.get(position).getOriginal_language());
        holder.rating.setText(String.valueOf(movieModels.get(position).getVote_average()));

    }

    @Override
    public int getItemCount() {
        if(movieModels!=null){
            return movieModels.size();
        } return 0;
    }

    public MovieModel getMovieAtPosition(int position) {
        return movieModels.get(position);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public RoundedImageView imageView;
        public TextView title, rating, language, detail;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.search_title);
            rating = itemView.findViewById(R.id.search_rating);
            language = itemView.findViewById(R.id.search_language);
            imageView = itemView.findViewById(R.id.search_image);
            detail = itemView.findViewById(R.id.description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMovieOnClick.onMovieOnClick(v,getBindingAdapterPosition());
        }
    }

}
