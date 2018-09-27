package com.example.lqs2.courseapp.listener;

public interface DownloadListener {

    void onProgress (int progress);

    void onSuccess ();

    void onFailed ();

    void onPaused ();

    void onCanceled ();
}