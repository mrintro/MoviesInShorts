package com.example.moviesinshorts.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.moviesinshorts.MainActivity;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentSearchBinding;
import com.example.moviesinshorts.ui.RecyclerAdapter;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;
import com.example.moviesinshorts.viewmodel.MyViewModelFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
public class SearchFragment extends Fragment {

    FragmentSearchBinding fragmentSearchBinding;
    private MovieListViewModel movieListViewModel;
    private RecyclerAdapter recyclerAdapter;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public SearchFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieListViewModel = new ViewModelProvider(this, new MyViewModelFactory(Objects.requireNonNull(this.getActivity()).getApplication())).get(MovieListViewModel.class);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        View view = fragmentSearchBinding.getRoot();
        view.setY(0);
        view.setX(0);
        initHandleUserText();
        setUpRecyclerAdapter();
        return view;
    }



    private void setUpRecyclerAdapter() {

        OnMovieOnClick onMovieOnClick = (view, position) -> ((MainActivity) Objects.requireNonNull(getActivity())).navigateToDetails(recyclerAdapter.getMovieAtPosition(position));

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
        if(Objects.requireNonNull(getActivity()).getCurrentFocus()!=null) {
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

    @Override
    public void onDestroy(){
        disposable.clear();
        super.onDestroy();
    }

    private void initHandleUserText() {

        Observable<String> observableStringQuery = Observable.create(
                (ObservableOnSubscribe<String>) emitter -> fragmentSearchBinding.searchField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("Search for string ", "After Text Change called " + s.toString() );
                        emitter.onNext(fragmentSearchBinding.searchField.getText().toString());
                    }
                })
        ).debounce(500, TimeUnit.MILLISECONDS)
                .filter(s -> !s.isEmpty())
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observableStringQuery.subscribe(new io.reactivex.Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d("Search for String :", s);
                movieListViewModel.getSearchMovie(fragmentSearchBinding.searchField.getText().toString()).observe(getViewLifecycleOwner(), movieModels -> {
                    Log.d("New data here", "check data");
                    recyclerAdapter.setMovieModels(movieModels);
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}