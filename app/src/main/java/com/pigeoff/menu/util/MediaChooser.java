package com.pigeoff.menu.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaChooser {

    private final Context context;
    private final ActivityResultLauncher<PickVisualMediaRequest> launcher;
    private OnMediaChoose listener;

    public MediaChooser(Fragment fragment) {
        this.context = fragment.requireContext();
        this.launcher = fragment.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (listener == null || uri == null) return;

            try {
                new BackgroundTask<>(fragment.requireActivity(), () -> {
                    return saveFileInCache(uri);
                })
                .addSuccessListener(path -> {
                    if (listener != null) listener.onMediaChoose(path);
                }).start();
            } catch (Exception e) {
                System.out.println("Error saving new photo");
            }
        });
    }

    public MediaChooser addOnMediaChooseListener(OnMediaChoose listener) {
        this.listener = listener;
        return this;
    }

    public void pick() {
        launcher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private String saveFileInCache(Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        File dir = context.getFilesDir();
        String newFileName = String.format("%s.jpg", RandomStringUtils.randomAlphanumeric(8));
        File destination = new File(dir, newFileName);
        FileOutputStream outputStream = new FileOutputStream(destination);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        return destination.getPath();
    }

    public static void deleteOldFile(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists()) {
            try {
                boolean works = file.delete();
                if (works) System.out.println("File deleted");
            } catch (Exception e) {
                System.out.println("File not deleted");
            }
        }
    }


    public static String base64ToNewFilePath(Context context, String raw) {
        if (raw == null) return "";
        try {
            File dir = context.getFilesDir();
            String newFileName = String.format("%s.jpg", RandomStringUtils.randomAlphanumeric(8));
            File destination = new File(dir, newFileName);
            byte[] bytes = Base64.decode(raw, Base64.DEFAULT);
            FileUtils.writeByteArrayToFile(destination, bytes);
            return destination.getPath();
        } catch (Exception e) {
            return "";
        }
    }
    public static String addBitmapToFiles(Context context, Bitmap bitmap) {
        if (bitmap == null) return "";
        try {
            File dir = context.getFilesDir();
            String newFileName = String.format("%s.jpg", RandomStringUtils.randomAlphanumeric(8));
            File destination = new File(dir, newFileName);
            FileOutputStream outputStream = new FileOutputStream(destination);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            return destination.getPath();
        } catch (Exception e) {
            return "";
        }
    }

    public interface OnMediaChoose {
        void onMediaChoose(String uri);
    }
}
