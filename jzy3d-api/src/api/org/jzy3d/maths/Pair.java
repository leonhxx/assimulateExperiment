package org.jzy3d.maths;

import java.io.Serializable;

/**
 * Class Pair. Represents a mathematical pair of objects (a, b).
 */
public class Pair<X, Y> implements Serializable{
    private static final long serialVersionUID = 4246736946032440512L;

    /**
     * a in the pair (a, b)
     */
    public final X a;

    /**
     * b in the pair (a, b)
     */
    public final Y b;

    /**
     * Construct a Pair(a, b)
     * 
     * @param a
     *            a in the pair (a, b)
     * @param b
     *            b in the pair (a, b)
     */
    public Pair(X a, Y b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        }
        else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        }
        else if (!b.equals(other.b))
            return false;
        return true;
    }
}
