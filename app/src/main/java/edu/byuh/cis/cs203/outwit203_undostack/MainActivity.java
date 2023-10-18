package edu.byuh.cis.cs203.outwit203_undostack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private GameView gv;

    /**
     * The onCreate method is one of the first methods that gets called
     * when an Activity starts. We override this method to do any one-time
     * initialization stuff
     * @param b not used in this program. In general, the Bundle object is used
     *          to preserve data from one instance of the program to the next;
     *          for example, after a device rotation event.
     */
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        gv = new GameView(this);
        setContentView(gv);
    }
}