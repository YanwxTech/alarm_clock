package com.yvan.alarmclock.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Yvan on 2015/7/27.
 */
public class UriUtil {
    public static String uriToName(Context context, Uri uri) {
        String title = "";
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Audio.Media.TITLE}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            title = cursor.getString(0);
        }
        if (title == null || title.equals("")) {
            if (uri.toString().contains("file")) {
                String url = uri.toString();
                title = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
            }
        }

        if (title == null) {
            title = "";
        }

        return title;
    }

}
