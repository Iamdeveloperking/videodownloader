

package avd.downloader.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;



public abstract class RenameDialog implements DialogInterface.OnClickListener {
    private EditText text;
    private Context context;

    protected RenameDialog(Context context, String hint) {
        this.context = context;
        text = new EditText(context);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setHint(hint);
        new AlertDialog.Builder(context)
                .setView(text).setMessage("Type new name:")
                .setPositiveButton("OK", this)
                .setNegativeButton("CANCEL", this)
                .create()
                .show();
    }

    @Override
    public final void onClick(DialogInterface dialog, int which) {
        Utils.hideSoftKeyboard((Activity) context, text.getWindowToken());
        if (which == DialogInterface.BUTTON_POSITIVE) {
            onOK(text.getText().toString());
        }
    }

    public abstract void onOK(String newName);
}
