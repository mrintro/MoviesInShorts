package com.example.moviesinshorts.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.moviesinshorts.MainActivity;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentSearchBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.repository.MovieListRepository;
import com.example.moviesinshorts.ui.RecyclerAdapter;
import com.example.moviesinshorts.ui.SliderAdapter;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchFragment extends Fragment {

    @NotNull
    private Handler handler;
    FragmentSearchBinding fragmentSearchBinding;
    private MovieListViewModel movieListViewModel;
    private RecyclerAdapter recyclerAdapter;
    private long lastEditTime;
    private final long delay = 500;

    public SearchFragment() {
    }

    private final Runnable handleUserType = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis() > lastEditTime+delay){
                movieListViewModel.getSearchMovie(fragmentSearchBinding.searchField.getText().toString()).observe(getViewLifecycleOwner(), new Observer<List<MovieModel>>() {
                    @Override
                    public void onChanged(List<MovieModel> movieModels) {
                        Log.d("New data here", "check data");
                        recyclerAdapter.setMovieModels(movieModels);
                    }
                });


            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        View view = fragmentSearchBinding.getRoot();
        handler = new Handler();
        initHandleUserText();
        setUpRecyclerAdapter(view);
        return view;
    }



    private void setUpRecyclerAdapter(View view) {

        OnMovieOnClick onMovieOnClick = new OnMovieOnClick() {
            @Override
            public void onMovieOnClick(View view, int position) {
                MovieDetailFragment movieDetailFragment = new MovieDetailFragment(recyclerAdapter.getMovieAtPosition(position));
//                ((MainActivity) getActivity()).changeToDetailFragment(movieDetailFragment);
                Log.d("Listener check", "listening to " + String.valueOf(position));

            }
        };

        RecyclerView recyclerView = fragmentSearchBinding.searchList;
        recyclerAdapter = new RecyclerAdapter(recyclerView, onMovieOnClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFocus();
    }

    private void initFocus() {
        Log.d("Focus Check","checking textview focus");
        fragmentSearchBinding.searchField.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(fragmentSearchBinding.searchField.getId(), InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onPause() {
        if(getActivity().getCurrentFocus()!=null) {
            final InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        initFocus();
        super.onResume();
    }

    private void initHandleUserText() {
        fragmentSearchBinding.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(handleUserType);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    lastEditTime = System.currentTimeMillis();
                    handler.postDelayed(handleUserType, delay);
                }
            }
        });
    }

}