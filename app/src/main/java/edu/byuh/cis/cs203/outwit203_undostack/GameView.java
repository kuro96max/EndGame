package edu.byuh.cis.cs203.outwit203_undostack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameView extends View {

    private Paint lightCell;
    private Paint darkCell;
    private Paint neutralColor;
    private Paint blackLine;
    private Paint rojo;
    private Cell[][] cellz;
    private List<Chip> chipz;
    private List<Cell> legalMoves;
    private Chip selected;
    private boolean initialized = false;
    private Timer tim;
    private RectF undoButton;
    private Bitmap undoIcon;
    private Stack<Move> undoStack;
    private int  currentPlayer = Team.DARK;
    private Paint paint;
    private boolean moving;

    private class Timer extends Handler {
        private boolean paused;

        /**
         * Start the timer
         */
        public void resume() {
            paused = false;
            sendMessageDelayed(obtainMessage(), 10);
        }

        /**
         * pause the timer
         */
        public void pause() {
            paused = true;
            removeCallbacksAndMessages(null);
        }

        /**
         * Instantiate the timer and start it running
         */
        public Timer() {
            resume();
        }

        /**
         * The most important method in the Timer class.
         * Here, we put all the code that needs to happen at each clock-tick
         * @param m the Message object (unused)
         */
        @Override
        public void handleMessage(Message m) {
            for (var c : chipz) {
                c.animate();
            }
            invalidate();
            if (!paused) {
                sendMessageDelayed(obtainMessage(), 10);
            }
            if (!anyMovingChips()) {
                checkForWinner();
            }
        }
    }

    /**
     * Our constructor. This is where we initialize our fields.
     * @param c Context is the superclass of Activity. Thus, this
     *          parameter is basically a polymorphic reference to
     *          whatever Activity created this View... in this case,
     *          it's our MainActivity.
     */
    public GameView(Context c) {
        super(c);
        lightCell = new Paint();
        lightCell.setColor(Color.rgb(217, 198, 149));
        lightCell.setStyle(Paint.Style.FILL);
        darkCell = new Paint(lightCell);
        darkCell.setColor(Color.rgb(133, 98, 6));
        neutralColor = new Paint(lightCell);
        neutralColor.setColor(Color.rgb(231,175,28));
        blackLine = new Paint();
        blackLine.setColor(Color.BLACK);
        blackLine.setStyle(Paint.Style.STROKE);
        rojo = new Paint();
        rojo.setColor(Color.RED);
        rojo.setStyle(Paint.Style.FILL);
        cellz = new Cell[9][10]; //[x][y]
        chipz = new ArrayList<>();
        legalMoves = new ArrayList<>();
        selected = null;
        undoIcon = BitmapFactory.decodeResource(getResources(), R.drawable.undo);
        undoStack = new Stack<>();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * onDraw is roughly equivalent to the paintComponent method in the
     * JPanel class from the standard Java API. Override it to perform
     * custom drawing for the user interface
     * @param c the Canvas object, which contains the methods we need for
     *          drawing basic shapes. Similar to the Graphics class in
     *          the standard Java API.
     */
    @Override
    public void onDraw(Canvas c) {

        final var w = c.getWidth();
        final var h = c.getHeight();
        final float cellSize = w/9f;
        if (! initialized) {
            blackLine.setStrokeWidth(cellSize * 0.03f);

            //create all the cells
            for (var i=0; i<10; i++) {
                for (var j=0; j<9; j++) {
                    var color = Team.NEUTRAL;
                    if (i<3 && j>5) {
                        color = Team.LIGHT;
                    } else if (i>6 && j<3) {
                        color = Team.DARK;
                    }
                    cellz[j][i] = new Cell(j,i,color,cellSize);
                }
            }

            //create all the chips
            for (var i=0; i<9; i++) {
                Chip dark = null;
                Chip light = null;
                if (i==4) {
                    dark = Chip.power(Team.DARK);
                    light = Chip.power(Team.LIGHT);
                } else {
                    dark = Chip.normal(Team.DARK);
                    light = Chip.normal(Team.LIGHT);
                }
                dark.setCell(cellz[i][i]);
                light.setCell(cellz[i][i+1]);
                chipz.add(dark);
                chipz.add(light);
            }

            //create the undo button
            undoButton = new RectF(w-cellSize, h-cellSize, w, h);

            //instantiate the timer
            tim = new Timer();

            initialized = true;
        }

        //draw the orange background
        c.drawRect(0,0,cellSize*9,cellSize*10,neutralColor);

        //draw the light brown corner
        c.drawRect(cellSize*6,0, cellSize*10, cellSize*3, lightCell);

        //draw the dark brown color
        c.drawRect(0, cellSize*7, cellSize*3, cellSize*10, darkCell);

        //draw a nice solid border around the whole thing
        c.drawRect(0,0,cellSize*9,cellSize*10, blackLine);

        //draw horizontal black lines
        for (int i=1; i<=9; i++) {
            c.drawLine(0, i*cellSize, cellSize*9, cellSize*i, blackLine);
        }
        //draw vertical black lines
        for (int i=1; i<=8; i++) {
            c.drawLine(i*cellSize, 0, i*cellSize, cellSize*10, blackLine);
        }

        //draw the chips
        for (Chip ch : chipz) {
            ch.draw(c, blackLine);
        }

        //draw the highlights
        for (Cell lm : legalMoves) {
            lm.drawHighlight(c, rojo);
        }

        //draw the undo button
        c.drawRoundRect(undoButton, undoButton.width()*0.1f, undoButton.width()*0.1f, blackLine);
        c.drawBitmap(undoIcon, null, undoButton, null);

        if (currentPlayer == 1){
            c.drawText("Dark Team Turn",cellSize*2f, cellSize*11f, paint);
        } else {
            c.drawText("Light Team Turn",cellSize*2f, cellSize*11f, paint);
        }
    }

    /**
     * This method gets called anytime the user touches the screen
     * @param m the object that holds information about the touch event
     * @return true (to prevent the touch event from getting passed to other objects. Please refer to the Chain of Responsibility design pattern.
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        //Log.d("CS203","inside GameView::onTouchEvent");
        final var x = m.getX();
        final var y = m.getY();
        if (m.getAction() == MotionEvent.ACTION_DOWN) {

            //ignore touch events while a chip is moving
            if (anyMovingChips()) {
                return true;
            }

            //did the user tap the undo button?
            if (undoButton.contains(x,y)) {
                undoLastMove();
            }

            //did the user tap one of the "legal move" cells?
            for (var cell : legalMoves) {
                if (cell.contains(x,y)) {
                    final var moov = new Move(selected.getHostCell(), cell);
                    undoStack.push(moov);
                    selected.setDestination(cell);
                    if (currentPlayer==Team.DARK){
                        currentPlayer = Team.LIGHT;
                    } else if (currentPlayer == Team.LIGHT){
                        currentPlayer = Team.DARK;
                    }
                    selected = null;
                    break;
                }
            }

            //first, clear old selections
            for (var chippy : chipz) {
                chippy.unselect();
            }
            legalMoves.clear();

            //now, check which chip got tapped
            for (var chippy : chipz) {
                if (chippy.contains(x, y)&&chippy.getColor()==currentPlayer) {
                    //if user taps the selected chip, unselect it
                    if (selected == chippy) {
                        selected.unselect();
                        selected = null;
                        legalMoves.clear();
                        break;
                    }
                    selected = chippy;
                    chippy.select();
                    findPossibleMoves();
                    break;
                }
            }
            invalidate();
        }
        return true;
    }

    /**
     * Pop the most recent move off the stack
     * and execute it in reverse.
     * Show a toast if the stack is empty.
     */
    public void undoLastMove() {

        if (anyMovingChips()) {
            return;
        }

        if (selected != null) {
            //un-highlight the selected chip before undo-ing
            selected.unselect();
            legalMoves.clear();
        }

        if (undoStack.isEmpty()) {
            var toasty = Toast.makeText(getContext(), "No moves to undo!", Toast.LENGTH_LONG);
            toasty.show();
        } else {
            Move move = undoStack.pop();
            Cell current = move.dest();
            Cell moveTo = move.src();
            selected = getChipAt(current);
            selected.setDestination(moveTo);
            selected.unselect();
            selected = null;
            if (currentPlayer==Team.DARK){
                currentPlayer = Team.LIGHT;
            } else {
                currentPlayer = Team.DARK;
            }
        }
    }

    /**
     * Given a cell, find the chip that's on it.
     * @param cel the Cell we're investigating
     * @return the Chip currently sitting on that Cell, or null if the cell is vacant.
     */
    private Chip getChipAt(Cell cel) {
        for (Chip ch : chipz) {
            if (ch.areYouHere(cel)) {
                return ch;
            }
        }
        return null;
    }

    /**
     * Populates the legalMoves arraylist with all possible moves
     * for the currently-selected chip
     */
    private void findPossibleMoves() {
        legalMoves.clear();
        int newX, newY;
        final Cell currentCell = selected.getHostCell();
        if (selected.isPowerChip()) {
            //can we go right?
            for (newX = currentCell.x()+1; newX < 9; newX++) {
                Cell candidate = cellz[newX][currentCell.y()];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                } else {
                    break;
                }
            }
            //can we go left?
            for (newX = currentCell.x()-1; newX >= 0; newX--) {
                Cell candidate = cellz[newX][currentCell.y()];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                } else {
                    break;
                }
            }
            //can we go up?
            for (newY = currentCell.y()-1; newY >= 0; newY--) {
                Cell candidate = cellz[currentCell.x()][newY];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                } else {
                    break;
                }
            }
            //can we go down?
            for (newY = currentCell.y()+1; newY < 10; newY++) {
                Cell candidate = cellz[currentCell.x()][newY];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                } else {
                    break;
                }
            }
            //can we go up/right diagonal?
            newX = currentCell.x()+1;
            newY = currentCell.y()-1;
            while (newX < 9 && newY >= 0) {
                Cell candidate = cellz[newX][newY];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                    newX++;
                    newY--;
                } else {
                    break;
                }
            }
            //can we go up/left diagonal?
            newX = currentCell.x()-1;
            newY = currentCell.y()-1;
            while (newX >= 0 && newY >= 0) {
                Cell candidate = cellz[newX][newY];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                    newX--;
                    newY--;
                } else {
                    break;
                }
            }
            //can we go down/right diagonal?
            newX = currentCell.x()+1;
            newY = currentCell.y()+1;
            while (newX < 9 && newY < 10) {
                Cell candidate = cellz[newX][newY];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                    newX++;
                    newY++;
                } else {
                    break;
                }
            }
            //can we go down/left diagonal?
            newX = currentCell.x()-1;
            newY = currentCell.y()+1;
            while (newX >= 0 && newY < 10) {
                Cell candidate = cellz[newX][newY];
                if (candidate.isLegalMove(selected)) {
                    legalMoves.add(candidate);
                    newX--;
                    newY++;
                } else {
                    break;
                }
            }

            //REGULAR CHIPS (not power chips)
        } else {
            //can we go right?
            Cell vettedCandidate = null;
            for (newX = currentCell.x()+1; newX < 9; newX++) {
                Cell candidate = cellz[newX][currentCell.y()];
                if (candidate.isLegalMove(selected)) {
                    vettedCandidate = candidate;
                } else {
                    break;
                }
            }
            if (vettedCandidate != null) {
                legalMoves.add(vettedCandidate);
            }
            //can we go left?
            vettedCandidate = null;
            for (newX = currentCell.x()-1; newX >= 0; newX--) {
                Cell candidate = cellz[newX][currentCell.y()];
                if (candidate.isLegalMove(selected)) {
                    vettedCandidate = candidate;
                } else {
                    break;
                }
            }
            if (vettedCandidate != null) {
                legalMoves.add(vettedCandidate);
            }

            //can we go up?
            vettedCandidate = null;
            for (newY = currentCell.y()-1; newY >= 0; newY--) {
                Cell candidate = cellz[currentCell.x()][newY];
                if (candidate.isLegalMove(selected)) {
                    vettedCandidate = candidate;
                } else {
                    break;
                }
            }
            if (vettedCandidate != null) {
                legalMoves.add(vettedCandidate);
            }

            //can we go down?
            vettedCandidate = null;
            for (newY = currentCell.y()+1; newY < 10; newY++) {
                Cell candidate = cellz[currentCell.x()][newY];
                if (candidate.isLegalMove(selected)) {
                    vettedCandidate = candidate;
                } else {
                    break;
                }
            }
            if (vettedCandidate != null) {
                legalMoves.add(vettedCandidate);
            }
        }
    }

    /**
     * checks if a chip is moving
     * @return true if a chip is currently moving; false otherwise.
     */
    private boolean anyMovingChips() {
        boolean result = false;
        for (var c : chipz) {
            if (c.isMoving()) {
                result = true;
                break;
            }
        }
        return result;
    }
    /**
     * Checks the current game state for a winner.
     * <p>
     * This method iterates through all the chips to count how many of them are in their home position.
     * If all chips of a particular team are in their home position, a dialog is shown declaring that team as the winner.
     * </p>
     * <p>
     * Note: It seems there might be an error in the code. Both dialog messages state that "Team Dark" won. You might want to adjust this.
     * </p>
     */
    private void checkForWinner(){
        int da = 0;
        int li = 0;
        for (var i: chipz){
            if(i.isHome() && i.getColor() == Team.DARK){
                da+=1;
            } else if (i.isHome() && i.getColor() == Team.LIGHT){
                li+=1;
            }
        }

        if (da==9){
            AlertDialog.Builder ab = new AlertDialog.Builder(getContext())
                    .setTitle("Game Result")
                    .setMessage("Congratulation! Team Dark won this game.Thank you for playing Outwit!")
                    .setPositiveButton("Play Again!", (gori, i)-> reset())
                    .setNegativeButton("Quit", (go, i)->((Activity)getContext()).finish())
                    .setCancelable(false);
            ab.show();
            tim.pause();
        }
        if (li==9){
            AlertDialog.Builder cd = new AlertDialog.Builder(getContext())
                    .setTitle("Game Result")
                    .setMessage("Congratulation! Team Dark won this game.Thank you for playing Outwit!")
                    .setPositiveButton("Play Again!", (di, i)-> reset())
                    .setNegativeButton("Quit", (d, i)->((Activity)getContext()).finish())
                    .setCancelable(false);
            cd.show();
            tim.pause();
        }
    }

    /**
     * Resets the game state to its initial configuration.
     * <p>
     * This method clears all game-related collections, sets the current player to DARK, and initializes the game board with chips in their starting positions.
     * </p>
     */
    private void reset(){
        chipz.clear();
        legalMoves.clear();
        undoStack.clear();
        currentPlayer =Team.DARK;
        moving = false;
        tim.resume();

        for (var i=0; i<10; i++) {
            for (var j=0; j<9; j++) {
                cellz[j][i].liberate();
            }
        }
        for (var i=0; i<9; i++) {
            Chip dark = null;
            Chip light = null;
            if (i==4) {
                dark = Chip.power(Team.DARK);
                light = Chip.power(Team.LIGHT);
            } else {
                dark = Chip.normal(Team.DARK);
                light = Chip.normal(Team.LIGHT);
            }
            dark.setCell(cellz[i][i]);
            light.setCell(cellz[i][i+1]);
            chipz.add(dark);
            chipz.add(light);
        }
    }
}