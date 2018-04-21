package com.hentaiuncle.vision;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;
import static com.hentaiuncle.vision.Global.PERMISSIONS;

/**
 * Created by 饶翰新 on 2018/4/13.
 */

public class CommonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        makeRoot();
    }

    private void makeRoot() {
        String directoryPath = "";
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            directoryPath = this.getExternalFilesDir("vision").getAbsolutePath();
        } else {
            directoryPath = this.getFilesDir() + File.separator + "vision";
        }
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Global.root = file.getPath() + File.separator;
    }
}
