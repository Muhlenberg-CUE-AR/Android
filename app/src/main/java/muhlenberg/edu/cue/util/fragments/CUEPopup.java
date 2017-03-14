package muhlenberg.edu.cue.util.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View tv = v.findViewById(R.id.popupText);
        ((TextView)tv).setText(text);
        return v;
    }
}
