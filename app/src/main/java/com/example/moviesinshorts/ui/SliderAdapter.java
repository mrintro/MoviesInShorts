package com.example.moviesinshorts.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentMovieBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.repository.MovieListRepository;
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
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final FragmentMovieBinding fragmentMovieBinding;
    private final MovieListViewModel movieListViewModel;



    private final OnBookmarkClickListener onBookmarkClickListener = new OnBookmarkClickListener() {
        @Override
        public void onBookmarkClickListener(View view, int position) {
            boolean currStatus = sharedPreferences.getBoolean(String.valueOf(movieModels.get(position).getId()),false);
            editor.putBoolean(String.valueOf(movieModels.get(position).getId()), !currStatus);
            editor.commit();
            movieListViewModel.bookMarkMovie(movieModels.get(position).getId(), !currStatus);
        }
    };

    public MovieModel getMovieModel(int position){
        if(movieModels.size()<position) return null;
        return movieModels.get(position);
    }

    public void setMovieModels(List<MovieModel> movieModels) {

        if(this.movieModels==null) {
            this.movieModels = movieModels;
            notifyDataSetChanged();
        }
        else{
            setSmartData(movieModels);
        }
    }

    private void setSmartData(List<MovieModel> movieModels) {
        for(int i=0;i<this.movieModels.size();i++){
            if(this.movieModels.get(i).getId()!=movieModels.get(i).getId()){
                notifyDataSetChanged();
            }else{
                if(this.movieModels.get(i).isBookmark()!=movieModels.get(i).isBookmark()){
                    this.movieModels.set(i, movieModels.get(i));
                    notifyItemChanged(i);
                }
            }
        }

    }

    public SliderAdapter(ViewPager2 viewPager2, OnMovieOnClick onMovieOnClick, Application application, FragmentMovieBinding fragmentMovieBinding, MovieListViewModel movieListViewModel) {
        this.viewPager2 = viewPager2;
        this.onMovieOnClick = onMovieOnClick;
        this.application = application;
        this.movieListRepository = MovieListRepository.getMovieListRepositoryInstance(application);
        this.sharedPreferences = application.getSharedPreferences("Bookmark",Context.MODE_PRIVATE);
        this.movieListViewModel = movieListViewModel;
        this.editor = sharedPreferences.edit();
        this.fragmentMovieBinding = fragmentMovieBinding;
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


            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + movieModels.get(position).getPoster_path())
                    .error(R.drawable.image_not_found)
                    .into((holder).imageView);
        if(movieModels.get(position).isBookmark()){
            holder.bookmarkImage.setImageResource(R.drawable.bookmark_red);
        }else {
            holder.bookmarkImage.setImageResource(R.drawable.bookmark_white);
        }



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
            if(v.getId() == R.id.imageSlide) {
                onMovieOnClick.onMovieOnClick(v, getBindingAdapterPosition());
            } else{
                onBookmarkClickListener.onBookmarkClickListener(v,getBindingAdapterPosition());
            }
        }
    }

}
