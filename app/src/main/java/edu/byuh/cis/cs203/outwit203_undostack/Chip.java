package edu.byuh.cis.cs203.outwit203_undostack;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class Chip {

    private int color;
    private boolean power;
    private RectF currentLocation;
    private Cell currentCell;
    private boolean selected;
    private final static Paint GOLD_LEAF, DARKCHIP, LIGHTCHIP;
    private PointF velocity;
    private Cell destination;

    /**
     * Static initializer for the Paint fields that are shared by all chip objects
     */
    static {
        GOLD_LEAF = new Paint();
        GOLD_LEAF.setColor(Color.rgb(202,192,6));
        GOLD_LEAF.setStyle(Paint.Style.FILL);
        DARKCHIP = new Paint(GOLD_LEAF);
        LIGHTCHIP = new Paint(GOLD_LEAF);
        DARKCHIP.setColor(Color.rgb(98,78, 26));
        LIGHTCHIP.setColor(Color.rgb(250,233, 188));
    }

    /**
     * Constructor for the Chip class
     * @param t the team (light or dark) that the chip belongs to
     * @param p true if a power chip, false if normal
     */
    private Chip(int t, boolean p) {
        color = t;
        power = p;
        currentLocation = new RectF();
        selected = false;
        velocity = new PointF();
        destination = null;
    }

    /**
     * A "simple factory" for instantiating normal (non-power) chips
     * @param t the chip's color, light or dark
     * @return a new normal chip
     */
    public static Chip normal(int t) {
        return new Chip(t, false);
    }

    /**
     * A "simple factory" for instantiating power chips
     * @param t the chip's color, light or dark
     * @return a new power chip
     */
    public static Chip power(int t) {
        return new Chip(t, true);
    }

    /**
     * Basic getter for the chip's location
     * @return
     */
    public RectF getBounds() {
        return currentLocation;
    }

    /**
     * Assign this chip to a particular cell
     * @param c the cell that this chip will reside in
     */
    public void setCell(Cell c) {
        if (currentCell != null) {  //kludgy
            currentCell.liberate();
        }
        currentCell = c;
        currentCell.setOccupied();
        velocity.set(0,0);
        currentLocation.set(currentCell.bounds());
    }

    public void select() {
        selected = true;
    }

    public void unselect() {
        selected = false;
    }

    /**
     * Checks whether a given cell is the same cell that the chip resides in
     * @param cel the cell to test
     * @return true if the given cell is this chip's current cell, false otherwise
     */
    public boolean areYouHere(Cell cel) {
        return (currentCell == cel);
    }

    /**
     * Draw the chip on the screen
     * @param c the Canvas object
     * @param blackLine a Paint object, passed in for convenience
     */
    public void draw(Canvas c, Paint blackLine) {
//        if (currentLocation.isEmpty()) {
//            currentLocation.set(currentCell.bounds());
//        }
        if (selected) {
            c.drawCircle(currentLocation.centerX(), currentLocation.centerY(),
                    currentLocation.width()*0.6f, GOLD_LEAF);
        }
        if (getColor() == Team.DARK) {
            c.drawCircle(currentLocation.centerX(), currentLocation.centerY(),
                    currentLocation.width()*0.45f, DARKCHIP);
        } else {
            c.drawCircle(currentLocation.centerX(), currentLocation.centerY(),
                    currentLocation.width()*0.45f, LIGHTCHIP);
        }
        c.drawCircle(currentLocation.centerX(), currentLocation.centerY(),
                currentLocation.width()*0.45f, blackLine);

        if (power) {
            c.drawCircle(currentLocation.centerX(), currentLocation.centerY(),
                    currentLocation.width()*0.2f, GOLD_LEAF);
        }
    }

    /**
     * Tests whether the given (x,y) coordinate is within this chip's bounding box
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if (x,y) is inside this chip's bounding box, false otherwise
     */
    public boolean contains(float x, float y) {
        return currentLocation.contains(x,y);
    }

    /**
     * Basic getter method for the chip's color (light or dark)
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * is the chip in its "home corner"?
     * @return true if the chip is in its home corner; false otherwise
     */
    public boolean isHome() {
        return color == currentCell.color();
    }

    /**
     * simple getter method for the power field
     * @return true if this is a power chip; false otherwise.
     */
    public boolean isPowerChip() {
        return power;
    }

    /**
     * simple getter for the current cell
     * @return the current cell
     */
    public Cell getHostCell() {
        return currentCell;
    }

    /**
     * Is animation currently happening?
     * @return true if the token is currently moving (i.e. has a non-zero velocity); false otherwise.
     */
    public boolean isMoving() {
        return (velocity.x != 0 || velocity.y != 0);
    }

    /**
     * Assign a destination location to the token
     * @param c the cell where the token should stop
     */
    public void setDestination(Cell c) {
        //refreshLocationIfNeeded();
        destination = c;
        //float dx = Math.signum(destination.bounds().left - currentLocation.left);
        //float dy = Math.signum(destination.bounds().top - currentLocation.top);
        PointF dir = currentCell.directionTo(destination);
        velocity.x = dir.x * currentLocation.width() * 0.333f;
        velocity.y = dir.y * currentLocation.width() * 0.333f;
    }

    /**
     * Move the token by its current velocity.
     * Stop when it reaches its destination location.
     */
    public void animate() {
        if (isMoving()) {
            float dx = destination.bounds().left - currentLocation.left;
            float dy = destination.bounds().top - currentLocation.top;
            if (PointF.length(dx, dy) < currentLocation.width() / 2) {
                setCell(destination);
            }
            currentLocation.offset(velocity.x, velocity.y);
        }
    }


}