package com.example.moviesinshorts.movieFragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviesinshorts.R;
import com.example.moviesinshorts.adapter.MovieListAdapter;
import com.example.moviesinshorts.databinding.FragmentMovieBinding;

public class MovieFragment extends Fragment {

    private FragmentMovieBinding fragmentMovieBinding;

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
        MovieListAdapter adapter = new MovieListAdapter();
        recyclerView.setAdapter(adapter);

        return movieFragmentView;
    }
}