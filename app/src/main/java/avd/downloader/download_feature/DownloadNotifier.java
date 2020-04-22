

package avd.downloader.download_feature;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.File;

import avd.downloader.LMvdApp;
import avd.downloader.R;

public class DownloadNotifier {
    private final int ID = 77777;
    private Intent downloadServiceIntent;
    private Handler handler;
    private NotificationManager notificationManager;
    private DownloadingRunnable downloadingRunnable;

    private class DownloadingRunnable implements Runnable {
        @Override
        public void run() {
            String filename = downloadServiceIntent.getStringExtra("name") + "." +
                    downloadServiceIntent.getStringExtra("type");
            Notification.Builder NB;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NB = new Notification.Builder(LMvdApp.getInstance().getApplicationContext(), "download_01")
                        .setStyle(new Notification.BigTextStyle());
            } else {
                NB = new Notification.Builder(LMvdApp.getInstance().getApplicationContext());
            }
            NB.setContentTitle("Downloading " + filename)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(LMvdApp.getInstance()
                            .getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                    .setOngoing(true);
            if (downloadServiceIntent.getBooleanExtra("chunked", false)) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS), filename);
                String downloaded;
                if (file.exists()) {
                    downloaded = android.text.format.Formatter.formatFileSize(LMvdApp.getInstance
                            ().getApplicationContext(), file.length());
                } else {
                    downloaded = "0KB";
                }
                NB.setProgress(100, 0, true)
                        .setContentText(downloaded);
                notificationManager.notify(ID, NB.build());
                handler.postDelayed(this, 1000);
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS), filename);
                String sizeString = downloadServiceIntent.getStringExtra("size");
                int progress = (int) Math.ceil(((double) file.length() / (double) Long.parseLong
                        (sizeString)) * 100);
                progress = progress >= 100 ? 100 : progress;
                String downloaded = android.text.format.Formatter.formatFileSize(LMvdApp
                        .getInstance().getApplicationContext(), file.length());
                String total = android.text.format.Formatter.formatFileSize(LMvdApp.getInstance()
                        .getApplicationContext(), Long.parseLong
                        (sizeString));
                NB.setProgress(100, progress, false)
                        .setContentText(downloaded + "/" + total + "   " + progress + "%");
                notificationManager.notify(ID, NB.build());
                handler.postDelayed(this, 1000);
            }
        }
    }

    DownloadNotifier(Intent downloadServiceIntent) {
        this.downloadServiceIntent = downloadServiceIntent;
        notificationManager = (NotificationManager) LMvdApp.getInstance().getApplicationContext().getSystemService
                (Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("download_01",
                    "Download Notification", NotificationManager.IMPORTANCE_DEFAULT));
            notificationManager.createNotificationChannel(new NotificationChannel("download_02",
                    "Download Notification", NotificationManager.IMPORTANCE_HIGH));
        }
        HandlerThread thread = new HandlerThread("downloadNotificationThread");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    void notifyDownloading() {
        downloadingRunnable = new DownloadingRunnable();
        downloadingRunnable.run();
    }

    void notifyDownloadFinished() {
        handler.removeCallbacks(downloadingRunnable);
        notificationManager.cancel(ID);

        String filename = downloadServiceIntent.getStringExtra("name") + "." +
                downloadServiceIntent.getStringExtra("type");
        Notification.Builder NB;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NB = new Notification.Builder(LMvdApp.getInstance().getApplicationContext(), "download_02")
                    .setTimeoutAfter(1500)
                    .setContentTitle("Download Finished")
                    .setContentText(filename)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(LMvdApp.getInstance().getApplicationContext().getResources(),
                            R.mipmap.ic_launcher_round));
            notificationManager.notify(8888, NB.build());
        } else {
            NB = new Notification.Builder(LMvdApp.getInstance().getApplicationContext())
                    .setTicker("Download Finished")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(LMvdApp.getInstance().getApplicationContext().getResources(),
                            R.mipmap.ic_launcher_round));
            notificationManager.notify(8888, NB.build());
            notificationManager.cancel(8888);
        }
    }

    void cancel() {
        if (downloadingRunnable != null) {
            handler.removeCallbacks(downloadingRunnable);
        }
        notificationManager.cancel(ID);
    }
}
