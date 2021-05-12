package it.polimi.ingsw.commons;

import java.io.Serializable;
import java.util.Objects;

public class Triplet<A, B, C> implements Serializable {
    private A _1;
    private B _2;
    private C _3;

    public Triplet(A _1, B _2, C _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    @Override
    public String toString() {
        return "(" +
                (_1 != null ? _1.toString() : "null") +
                ", " +
                (_2 != null ? _2.toString() : "null") +
                ", " +
                (_3 != null ? _3.toString() : "null") +
                ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(_1, triplet._1) &&
                Objects.equals(_2, triplet._2) &&
                Objects.equals(_3, triplet._3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3);
    }

    public void set_1(A _1) {
        this._1 = _1;
    }

    public void set_2(B _2) {
        this._2 = _2;
    }

    public void set_3(C _3) {
        this._3 = _3;
    }

    public A get_1() {
        return _1;
    }

    public B get_2() {
        return _2;
    }

    public C get_3() {
        return _3;
    }
}
