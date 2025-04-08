package com.example.mediaplayx.ui.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.data.repository.SongRepo;
import com.example.mediaplayx.ui.adapter.MediaAdapter;
import com.example.mediaplayx.ui.view.fragments.Artiist_AlbumFragment;
import com.example.mediaplayx.ui.view.fragments.Mediafragment;
import com.example.mediaplayx.ui.view.fragments.Playbarfragment;
import com.example.mediaplayx.ui.viewmodel.SongViewModel;
import com.example.mediaplayx.ui.viewmodel.ViewModelFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {
    private String[] menu = {"Songs", "Artist", "Album", "Favorite songs","Files"};
    private static final String TAG = MainActivity.class.getSimpleName();

    private Mediafragment mediaFragment = new Mediafragment();
    private Artiist_AlbumFragment artistFragment = new Artiist_AlbumFragment();

    private SongViewModel songViewModel; // Declare the ViewModel
    private MediaAdapter adapter;
    private List<Songmodel> songmodels;
    private MutableLiveData<Integer> liveData = new MutableLiveData<>();

    private SongRepo songRepo;
    private SearchView searchView;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private Playbarfragment playbarFragment = new Playbarfragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_main);

        // Initialize the Song Repository and ViewModel
        songRepo = SongRepo.getInstance(this);
        ViewModelFactory factory = new ViewModelFactory(songRepo);
        songViewModel = new ViewModelProvider(this, factory).get(SongViewModel.class);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.reclist_menu);
        searchView =findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // Handle search query submission
                Log.d(TAG, "Search query submitted: " + query);
                return false; // Return true if the query has been handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change in the search view
                songViewModel.searchsong(newText);
                return false; // Return true if the text change has been handled
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MediaAdapter(menu, this); // Pass the listener
        recyclerView.setAdapter(adapter);

        // Manage fragments
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: Initializing fragments");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.add(R.id.fragm2, playbarFragment); // Add Playbar fragment
            transaction.add(R.id.fragment, mediaFragment); // Add Media category fragment
            transaction.commit();
        }

        // Request permissions
        requestPermissions();
    }


    private void requestPermissions() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Permission granted");
                    } else {
                        Log.d(TAG, "Permission denied");
                    }
                }
        );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

    }

    @Override
    public void onItemClick(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        songViewModel.setcategory(position); // Set the selected category

//        if (mediaFragment != null) {
//            transaction.replace(R.id.fragment, mediaFragment); // Replace the fragment
//            transaction.addToBackStack(null); // Add to back stack
//            transaction.commit();// Update the selected item in the fragment
//        }
        if (position==4){

            transaction.replace(R.id.fragment, artistFragment); // Replace the fragment/ Add to back stack
            transaction.commit();// Update the selected item in the fragment
        }
        else {
            transaction.replace(R.id.fragment, mediaFragment); // Replace the fragment/ Add to back stack
            transaction.commit();
        }
    }

    public LiveData<Integer> getCurrentCategory() {
        return liveData;
    }



}