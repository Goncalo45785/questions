package pt.goncalo.blissquestions.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityUtils {
    public static final String TAG = Activity.class.getSimpleName();

    public static boolean isUrlIntent(Intent intent) {
        return intent != null && intent.getData() != null;
    }

    public static void shareUrlByMail(@NonNull Context context,@NonNull String url) {
        if (url.isEmpty()) {
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        ((Activity) context).finish();
        context.startActivity(shareIntent);
    }

    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }
}
