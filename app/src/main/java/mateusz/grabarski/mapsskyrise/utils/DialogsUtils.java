package mateusz.grabarski.mapsskyrise.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import mateusz.grabarski.mapsskyrise.R;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public class DialogsUtils {

    public static AlertDialog.Builder getMessageDialog(Context context, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.information);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }
}
