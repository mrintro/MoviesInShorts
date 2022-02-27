package com.example.moviesinshorts.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.model.MovieModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<MovieModel> movieModels;
    private ViewPager2 viewPager2;

    public MovieModel getMovieModel(int position){
        if(movieModels.size()<position) return null;
        return movieModels.get(position);
    }

    public void setMovieModels(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        notifyDataSetChanged();
    }

    public SliderAdapter(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }


    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slider_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {



        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/"+movieModels.get(position).getPoster_path())
                .into((holder).imageView);

    }

    @Override
    public int getItemCount() {
        if(movieModels!=null){
            return movieModels.size();
        } return 0;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }

}
