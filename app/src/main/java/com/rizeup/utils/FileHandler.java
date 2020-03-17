package com.rizeup.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class FileHandler {

    public static String getFileExtension(ContentResolver cR, Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cR.getType(uri));
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = Calendar.getInstance().getTime().toString().replace(" ", "_");
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


}
