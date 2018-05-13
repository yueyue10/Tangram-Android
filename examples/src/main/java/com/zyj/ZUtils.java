package com.zyj;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.socks.library.KLog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhaoyuejun on 2018/5/12.
 */

public class ZUtils {

    public static byte[] getAssertsFile(Context context, String fileName) {
        InputStream inputStream = null;
        AssetManager assetManager = context.getAssets();
        try {
            inputStream = assetManager.open(fileName);
            if (inputStream == null) {
                return null;
            }
            BufferedInputStream bis = null;
            int length;
            try {
                bis = new BufferedInputStream(inputStream);
                length = bis.available();
                byte[] data = new byte[length];
                bis.read(data);

                return data;
            } catch (IOException e) {
                KLog.d(Log.getStackTraceString(e));
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception e) {

                    }
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
