package com.example.mediaplayx.ui.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.data.repository.SongRepo;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private  SongRepo songRepository;

    public ViewModelFactory(SongRepo repo) {
        this.songRepository = repo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SongViewModel.class)) {
            songRepository=SongRepo.getInstance(songRepository.context);
            return (T) new SongViewModel(songRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}