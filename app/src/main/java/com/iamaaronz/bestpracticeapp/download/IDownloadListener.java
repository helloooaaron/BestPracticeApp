package com.iamaaronz.bestpracticeapp.download;

public interface IDownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailure();

    void onPause();

    void onCancel();
}
