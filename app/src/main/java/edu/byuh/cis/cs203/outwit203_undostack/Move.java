package edu.byuh.cis.cs203.outwit203_undostack;

public class Move {
    private final Cell src;
    private final Cell dest;

    /**
     * canonical constructor. Assigns a source and destination cell to this move
     * @param s the source cell
     * @param d the destination cell
     */
    public Move(Cell s, Cell d) {
        src = s;
        dest = d;
    }

    /**
     * Basic "getter" method for the source field
     * @return the source cell
     */
    public Cell src() {
        return src;
    }

    /**
     * Basic "getter" method for the destination field
     * @return the destination cell
     */
    public Cell dest() {
        return dest;
    }
}
