package com.example.moviesinshorts.movieFragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviesinshorts.R;
import com.example.moviesinshorts.adapter.MovieListAdapter;
import com.example.moviesinshorts.databinding.FragmentMovieBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;

import java.util.List;

public class MovieFragment extends Fragment {

    private FragmentMovieBinding fragmentMovieBinding;

    private List<MovieModel> movieList;
    private MovieListAdapter adapter;
    private MovieListViewModel viewModel;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View movieFragmentView = inflater.inflate(R.layout.fragment_movie, container, false);

        RecyclerView recyclerView = movieFragmentView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new GridLayoutManager(this.getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MovieListAdapter(getContext(),movieList);
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(getActivity()).get(MovieListViewModel.class);
        viewModel.getNowPlayingLiveData().observe(getActivity(), new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                if(movieModels!=null){
                    movieList = movieModels;
                }
            }
        });

        return movieFragmentView;
    }
}