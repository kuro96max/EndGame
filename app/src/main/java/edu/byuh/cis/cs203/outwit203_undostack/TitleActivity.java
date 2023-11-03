package edu.byuh.cis.cs203.outwit203_undostack;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TitleActivity extends AppCompatActivity {
    private ImageView main;
    /**
     * Called when the activity is starting.
     *
     * @param b If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        main = new ImageView(this);
        main.setImageResource(R.drawable.outwit);
        main.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(main);
    }
    /**
     * Called when a touch event is dispatched to the activity.
     *
     * @param m The MotionEvent object containing full information about the event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent (MotionEvent m){
        float w = main.getWidth();
        float h = main.getHeight();
        if (m.getAction() == MotionEvent.ACTION_DOWN){
            float x = m.getX();
            float y = m.getY();
            if (y > 4*h/5 ) {
                main.setImageResource(R.drawable.start);
            } else if (x<w/4f&&y<h/4) {
                main.setImageResource(R.drawable.pressinfo);
            } else if (x>w*3/4&&y<h/4){
                main.setImageResource(R.drawable.setting);
            }
        }
        if (m.getAction() == MotionEvent.ACTION_UP){
            main.setImageResource(R.drawable.outwit);
            float x = m.getX();
            float y = m.getY();
            if (y > 4*h/5 ){
                Intent start = new Intent(this, MainActivity.class);
                startActivity(start);
            } else if (x<w/4&&y<h/4) {
                //TODO lunch "about box"
                AlertDialog.Builder ab = new AlertDialog.Builder(this)
                        .setTitle("About Outwit Game")
                        .setMessage("To win the game, tap a chip and place it in a square area of the same color.\n"+ "\n"+"Game Design: Shohei Kurokawa\n" +
                                "Programming: Shohei Kurokawa\n" +
                                "Artwork: Shohei Kurokawa")
                        .setNeutralButton("Close", (di, i) -> di.cancel() )
                        .setCancelable(false);
                ab.show();
            } else if (x>w*3/4&&y<h/4){
                //TODO lunch setting
            } else {}
        }
        return true;
    }
}
