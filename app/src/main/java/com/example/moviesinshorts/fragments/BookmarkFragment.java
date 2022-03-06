package com.example.moviesinshorts.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviesinshorts.MainActivity;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentBookmarkBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.ui.RecyclerAdapter;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;
import com.example.moviesinshorts.viewmodel.MyViewModelFactory;

import java.util.List;

public class BookmarkFragment extends Fragment {

    private RecyclerAdapter recyclerAdapter;
    private FragmentBookmarkBinding fragmentBookmarkBinding;
    private MovieListViewModel movieListViewModel;

    public BookmarkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieListViewModel = new ViewModelProvider(this, new MyViewModelFactory(this.getActivity().getApplication())).get(MovieListViewModel.class);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentBookmarkBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_bookmark, container, false);
        View view = fragmentBookmarkBinding.getRoot();
        movieListViewModel.getBookMarkedMovies().observe(getViewLifecycleOwner(), new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModelList) {
                recyclerAdapter.setMovieModels(movieModelList);
            }
        });
        setUpRecyclerAdapter();
        return view;
    }
    private void setUpRecyclerAdapter() {

        OnMovieOnClick onMovieOnClick = new OnMovieOnClick() {
            @Override
            public void onMovieOnClick(View view, int position) {
                ((MainActivity) getActivity()).navigateFromBookmarkToDetail(recyclerAdapter.getMovieAtPosition(position));
            }
        };

        RecyclerView recyclerView = fragmentBookmarkBinding.recyclerViewBookmark;
        recyclerAdapter = new RecyclerAdapter(recyclerView, onMovieOnClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
    }
}