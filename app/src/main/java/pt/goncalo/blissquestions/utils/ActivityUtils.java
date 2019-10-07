package pt.goncalo.blissquestions.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

public class ActivityUtils {
    public static final String TAG = Activity.class.getSimpleName();

    public static boolean isUrlIntent(Intent intent) {
        return intent != null && intent.getData() != null;
    }

    public static void shareUrlByMail(@NonNull Context context,@NonNull String url,@NonNull String title) {
        if (url.isEmpty()) {
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }
}
