package ch.uzh.supersede.feedbacklibrary.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public class PopUp {

    public enum DialogueOption {
        OK, CANCEL;
    }

    private Context context;
    private String title;
    private String message;
    private String cancelLabel;
    private String okLabel;
    private OnClickListener okClickListener;
    private OnClickListener cancelClickListener;
    private EditText inputText;
    private boolean showOk = true;
    private boolean showCancel = true;

    private String dialogueOutput = null;
    private DialogueOption dialogueOption = null;

    public String getDialogueOutput() {
        return dialogueOutput;
    }

    public DialogueOption getDialogueOption() {
        return dialogueOption;
    }


    public PopUp(Context context) {
        this.context = context;
    }

    public PopUp withTitle(String title) {
        this.title = title;
        return this;
    }

    public PopUp withMessage(String message) {
        this.message = message;
        return this;
    }

    public PopUp withInput(final EditText inputText) {
        this.inputText = inputText;
        return this;
    }

    public PopUp withCustomCancel(String cancelLabel, OnClickListener clickListener) {
        this.cancelLabel = cancelLabel;
        this.cancelClickListener = clickListener;
        return this;
    }

    public PopUp withCustomOk(String okLabel, OnClickListener clickListener) {
        this.okLabel = okLabel;
        this.okClickListener = clickListener;
        return this;
    }

    public PopUp withoutOk() {
        this.showOk = false;
        return this;
    }

    public PopUp withoutCancel() {
        this.showCancel = false;
        return this;
    }

    public PopUp buildAndShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        if (inputText != null) {
            builder.setView(inputText);
        }
        if (showOk) {
            if (okClickListener != null) {
                builder.setPositiveButton(okLabel, okClickListener);
            } else {
                builder.setPositiveButton("OK", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            }
        }
        if (showCancel){
            if (cancelClickListener != null) {
                builder.setPositiveButton(okLabel, cancelClickListener);
            } else {
                builder.setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            }
        }
        builder.show();
        return this;
    }
}
