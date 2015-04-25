package io.github.gatimus.hooftuner;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class About extends DialogFragment implements DialogInterface.OnClickListener{


    private static final String TAG = "About";
    private Builder builder;
    private StringBuilder stringBuilder;
    private String msg;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "Create");
        super.onCreateDialog(savedInstanceState);
        stringBuilder = new StringBuilder();
        stringBuilder.append("v").append(BuildConfig.VERSION_CODE);
        stringBuilder.append("(").append(BuildConfig.VERSION_NAME).append(")\n");
        stringBuilder.append(getString(R.string.msg_about));
        msg = stringBuilder.toString();
        builder = new Builder(getActivity());
        builder.setTitle(R.string.action_about);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(msg);
        builder.setNeutralButton(R.string.ok, this);
        return builder.create();
    } //onCreateDialog


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE :
                dialog.dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE :
                dialog.dismiss();
                break;
            case DialogInterface.BUTTON_NEUTRAL :
                dialog.dismiss();
                break;
            default:
                dialog.dismiss();
                break;
        }
    } //onClick


} //class
