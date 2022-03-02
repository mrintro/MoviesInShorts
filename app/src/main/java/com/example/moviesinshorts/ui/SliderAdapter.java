package com.example.moviesinshorts.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.fragments.MovieDetailFragment;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.repository.MovieListRepository;
import com.example.moviesinshorts.utils.NetworkHelper;
import com.example.moviesinshorts.utils.OnBookmarkClickListener;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<MovieModel> movieModels;
    private final ViewPager2 viewPager2;
    private final OnMovieOnClick onMovieOnClick;
    private final Application application;
    private final MovieListRepository movieListRepository;

    private final OnBookmarkClickListener onBookmarkClickListener = new OnBookmarkClickListener() {
        @Override
        public void onBookmarkClickListener(View view, int position) {
            Log.d("Check", "Bookmark Clicked");

            if(movieModels.get(position).isBookmark()) {
//                movieListRepository.bookMarkMovie(movieModels.get(position).getId(), false);
//                view.setBackgroundResource(R.drawable.bookmark_white);
//                Glide.with(view).load(R.drawable.bookmark_white).into((ImageView) view);
//                ImageView v = (ImageView) view;
//                v.setImageResource(R.drawable.bookmark_white);
            }else{
//                movieListRepository.bookMarkMovie(movieModels.get(position).getId(), true);
//                Glide.with(view).load(R.drawable.bookmark_red).into((ImageView) view);
//                view.setBackgroundResource(R.drawable.bookmark_red);
//                ImageView v = (ImageView) view;
//                v.setImageResource(R.drawable.bookmark_red);
            }




        }
    };

    public MovieModel getMovieModel(int position){
        if(movieModels.size()<position) return null;
        return movieModels.get(position);
    }

    public void setMovieModels(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        notifyDataSetChanged();
    }

    public SliderAdapter(ViewPager2 viewPager2, OnMovieOnClick onMovieOnClick, Application application) {
        this.viewPager2 = viewPager2;
        this.onMovieOnClick = onMovieOnClick;
        this.application = application;
        this.movieListRepository = MovieListRepository.getMovieListRepositoryInstance(application);
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
        public RoundedImageView bookmarkImage;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            bookmarkImage = itemView.findViewById(R.id.bookmark_image);
            imageView.setOnClickListener(this);
            bookmarkImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("Check ids", String.valueOf(v.getId())+" " +R.id.imageSlide);
            if(v.getId() == R.id.imageSlide) {
//                onMovieOnClick.onMovieOnClick(v, getBindingAdapterPosition());
            } else{
                onBookmarkClickListener.onBookmarkClickListener(v,getBindingAdapterPosition());
            }
        }
    }

}
