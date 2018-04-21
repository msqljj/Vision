package com.hentaiuncle.vision;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.hentaiuncle.vision.model.operator.Binaryzation;
import com.hentaiuncle.vision.model.operator.BinaryzationRGB;
import com.hentaiuncle.vision.model.operator.Blur;
import com.hentaiuncle.vision.model.operator.Cameo;
import com.hentaiuncle.vision.model.operator.Edge3x3;
import com.hentaiuncle.vision.model.operator.Edge5x5;
import com.hentaiuncle.vision.model.operator.Grey;
import com.hentaiuncle.vision.model.operator.GreyEx;
import com.hentaiuncle.vision.model.operator.Inverse;
import com.hentaiuncle.vision.model.operator.Laplacian;
import com.hentaiuncle.vision.model.operator.Light;
import com.hentaiuncle.vision.model.operator.LowPoly;
import com.hentaiuncle.vision.model.operator.Sharp5x5;
import com.hentaiuncle.vision.model.operator.Sobel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Hansin on 2018/4/13.
 */

public class Global {
    public static Class<?>[] OPERATORS = {
            Blur.class,
            Grey.class,
            GreyEx.class,
            Inverse.class,
            Light.class,
            LowPoly.class,
            null,
            Binaryzation.class,
            BinaryzationRGB.class,
            null,
            Sobel.class,
            Edge3x3.class,
            Edge5x5.class,
            Sharp5x5.class,
            Laplacian.class,
            Cameo.class
    };

    public static String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.INTERNET"};

    public static String root;

    public static boolean getPermissions(Activity activity) {
        for (String p : Global.PERMISSIONS) {
            int permission = ContextCompat.checkSelfPermission(activity, p);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
                return false;
            }
        }
        return true;
    }

    public static void Unzip(String zipFile, String targetDir) {
        new File(targetDir).mkdirs();
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称
        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    Log.i("Unzip: ","="+ entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();
                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }
}
