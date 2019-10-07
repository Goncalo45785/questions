package pt.goncalo.blissquestions.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
    public static final String TAG = Activity.class.getSimpleName();

    public final static boolean isUrlIntent(Intent intent) {
        return intent != null && intent.getData() != null;
    }
}
