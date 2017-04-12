package muhlenberg.edu.cue.util.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import muhlenberg.edu.cue.R;

/**
 * A popup dialog fragment that will display some text once it's  activated.
 *
 * See res>layout>popup for styling
 *
 * Created by Jalal on
 */
public class CUEPopup extends DialogFragment {

    /**
     * Text to display.
     */
    public static String text = "Hello World";

    /**
     * Singleton because there can only be one popup at a time
     * @return new CUEPop instance
     */
    public static CUEPopup newInstance() {
        return new CUEPopup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.popup, container, false);
        TextView tv = (TextView) v.findViewById(R.id.popupText);
        tv.setText(text);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
