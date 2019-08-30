package com.omarea.krscript.downloader;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;
import com.omarea.common.shared.FilePathResolver;
import com.omarea.common.ui.DialogHelper;
import com.omarea.krscript.R;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    private static DownloadCompleteReceiver downloadCompleteReceiver;

    public static void autoRegister(Context context) {
        if (downloadCompleteReceiver == null) {
            downloadCompleteReceiver = new DownloadCompleteReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(downloadCompleteReceiver, intentFilter);
        }
    }
    public static void autoUnRegister(Context context) {
        if (downloadCompleteReceiver != null) {
            context.unregisterReceiver(downloadCompleteReceiver);
            downloadCompleteReceiver = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                try {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                    if (TextUtils.isEmpty(type)) {
                        type = "*/*";
                    }
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    /*
                    if (uri != null) {
                        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                        handlerIntent.setDataAndType(uri, type);
                        context.startActivity(handlerIntent);
                    }
                    */
                    String path = new FilePathResolver().getPath(context, uri);
                    if (path != null && !path.isEmpty()) {
                        new Downloader(context, null).saveTaskCompleted(downloadId, path);
                        try {
                            DialogHelper.Companion.helpInfo(context, context.getString(R.string.kr_download_completed), "" + path);
                        } catch (Exception ex) {
                            Toast.makeText(context, context.getString(R.string.kr_download_completed) + "\n" + path, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
}
