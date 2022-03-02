package com.example.moviesinshorts.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.fragments.MovieDetailFragment;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.utils.NetworkHelper;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<MovieModel> movieModels;
    private final ViewPager2 viewPager2;
    private final OnMovieOnClick onMovieOnClick;

    public MovieModel getMovieModel(int position){
        if(movieModels.size()<position) return null;
        return movieModels.get(position);
    }

    public void setMovieModels(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        notifyDataSetChanged();
    }

    public SliderAdapter(ViewPager2 viewPager2, OnMovieOnClick onMovieOnClick) {
        this.viewPager2 = viewPager2;
        this.onMovieOnClick = onMovieOnClick;
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
    public void onBindViewHolder(@NonNull SliderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(NetworkHelper.checkNetwork(holder.itemView.getContext())) {
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + movieModels.get(position).getPoster_path())
                    .into((holder).imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_launcher_foreground)
                    .into(holder.imageView);
        }

//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
//                binding.mainContainer.setVisibility(View.GONE);
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(binding.mainContainer.getId(), movieDetailFragment);
//                fragmentTransaction.addToBackStack("DetailsTransaction");
//                fragmentTransaction.commit();
//            }
//        });

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

    class SliderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public RoundedImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMovieOnClick.onMovieOnClick(v,getBindingAdapterPosition());
        }
    }

}
