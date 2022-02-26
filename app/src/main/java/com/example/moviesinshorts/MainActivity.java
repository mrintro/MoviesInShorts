package com.example.moviesinshorts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.moviesinshorts.databinding.ActivityMainBinding;
import com.example.moviesinshorts.databinding.ActivityMainBindingImpl;
import com.example.moviesinshorts.movieFragment.MovieFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        initMovieFragment(fragmentManager);
    }

    private void initMovieFragment(FragmentManager fragmentManager) {
        MovieFragment movieFragment = new MovieFragment();
        FragmentTransaction fragmentTransactaction = fragmentManager.beginTransaction();
        fragmentTransactaction.replace(R.id.movie_fragment, movieFragment);
        fragmentTransactaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransactaction.commit();
    }
}