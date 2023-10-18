package edu.byuh.cis.cs203.outwit203_undostack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class Cell {
    private int x;
    private int y;
    private int cellColor;
    private boolean occupied;
    private RectF geometry;

    /**
     * Create a new Cell object. A Cell is a unique square on the 9x10 Outwit board
     * @param xx the relative X position
     * @param yy the relative Y position
     * @param t the color: dark, light, or neutral
     * @param cellSize the size of the cell, in pixels
     */
    public Cell(int xx, int yy, int t, float cellSize) {
        x = xx;
        y = yy;
        occupied = false;
        cellColor = t;
        geometry = new RectF(x*cellSize, y*cellSize, (x+1)*cellSize, (y+1)*cellSize);
    }

    /**
     * Basic getter for the X coordinate
     * @return the x coordinate of the cell
     */
    public int x() {
        return x;
    }

    /**
     * Basic getter for the Y coordinate
     * @return the y coordinate of the cell
     */
    public int y() {
        return y;
    }

    /**
     * Basic getter for the cell color
     * @return the color code of the cell: light, dark, or neutral
     */
    public int color() {
        return cellColor;
    }

    /**
     * Mark the cell as being occupied by a chip
     */
    public void setOccupied() {
        occupied = true;
    }

    /**
     * Mark the cell as no longer occupied by a chip
     */
    public void liberate() {
        occupied = false;
    }

    /**
     * Basic getter for the occupied field
     * @return true if there is a chip on this cell; false otherwise
     */
    public boolean isOccupied() {
        return occupied;
    }

    public boolean contains(float x, float y) {
        return geometry.contains(x,y);
    }

    /**
     * Basic getter for the geometry field
     * @return the rectangular area on the screen where this cell resides
     */
    public RectF bounds() {
        return geometry;
    }

    /**
     * Determines whether moving the given chip onto this cell is legal, or not
     * @param chippy the Chip object that the user selected
     * @return true if the cell could receive this chip; false otherwise
     */
    public boolean isLegalMove(Chip chippy) {

        boolean legal = true;

        //can't move onto an occupied cell
        if (occupied) {
            legal = false;
        }

        //can't move from "home" into a neutral cell
        else if (chippy.isHome() && cellColor == Team.NEUTRAL) {
            legal = false;
        }

        //OK to move into a home cell
        else if (cellColor == chippy.getColor()) {
            legal = true;
        }

        //OK to move from neutral cell to neutral cell
        else if ( !chippy.isHome() && cellColor == Team.NEUTRAL) {
            legal = true;
        }

        //can't move into a cell of the opposite team
        else if (cellColor != chippy.getColor()) {
            legal = false;
        }

        return legal;
    }

    /**
     * Draw a small dot in the middle of the cell
     * @param c the Canvas object for drawing
     * @param p the Pain object to use for color
     */
    public void drawHighlight(Canvas c, Paint p) {
        c.drawCircle(geometry.centerX(), geometry.centerY(), geometry.width()*0.2f, p);
    }

    /**
     * Computes direction, represented as a PointF, that a chip on "this" cell must move to arrive at dest.     *
     * @param dest the destination cell
     * @return a PointF object, consisting only of the numbers -1, 0, 1.
     */
    public PointF directionTo(Cell dest) {
        float dx = Math.signum(dest.x - this.x);
        float dy = Math.signum(dest.y - this.y);
        return new PointF(dx, dy);
    }


}

