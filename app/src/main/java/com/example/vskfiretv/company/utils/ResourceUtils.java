package com.example.vskfiretv.company.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Util class to read app resources declared in the resource folder
 */
public class ResourceUtils {
    private static final String TAG = ResourceUtils.class.getSimpleName();

    /**
     * Attempts to open the resource given by the resourceId as a raw resource and return its contents
     * as a string.
     *
     * @param context context used to load the resource
     * @param resourceId the unique id of the resource that need to be opened
     * @return String containing the contents of the resource file, null if an error occurred.
     */
    public static String getRawTextResource(final Context context, final int resourceId) {

        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        String line;
        final StringBuilder result = new StringBuilder();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
            return result.toString();
        } catch (final IOException e) {
            Log.e(TAG, "Error reading raw resource");
        }

        return null;
    }

    private ResourceUtils() {}
}
