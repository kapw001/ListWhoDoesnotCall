package cashkaro.com.listwhodoesnotcall;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by yasar on 10/8/17.
 */

public class Utils {

    private static final String TAG = "Utils";
    private static ProgressDialog progressDialog;

    public static void showProgress(Context context, String msg) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public static void hideProgress() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }
}
