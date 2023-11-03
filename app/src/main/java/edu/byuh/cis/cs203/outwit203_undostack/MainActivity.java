package edu.byuh.cis.cs203.outwit203_undostack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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
    /**
     * Called when the activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
//        gv.pauseGame();
        Log.d("CS203", "Pause!");
    }
    /**
     * Called when the activity is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
//        gv.resumeGame();
        Log.d("CS203", "Start again!");
    }
    /**
     * Perform any final cleanup before the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CS203", "Good bye!");
    }
    /**
     * Called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("CS203", "Back to previous");
    }
}