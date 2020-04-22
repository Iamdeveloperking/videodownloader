

package avd.downloader;

import android.app.Application;
import android.content.Intent;

import avd.downloader.download_feature.DownloadManager;

public class LMvdApp extends Application {
    private static LMvdApp instance;
    private Intent downloadService;
    private LMvdActivity.OnBackPressedListener onBackPressedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        downloadService = new Intent(getApplicationContext(), DownloadManager.class);
    }

    public Intent getDownloadService() {
        return downloadService;
    }

    public static LMvdApp getInstance() {
        return instance;
    }

    public LMvdActivity.OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }

    public void setOnBackPressedListener(LMvdActivity.OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
}
