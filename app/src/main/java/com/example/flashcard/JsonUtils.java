package com.example.flashcard;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {
    private static final String TAG = "JsonUtils";

    public static String readJsonFromRaw(Context context, int rawResId) {
        try (InputStream inputStream = context.getResources().openRawResource(rawResId);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toString("UTF-8");

        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la lecture du fichier JSON", e);
            return null;
        }
    }
}
