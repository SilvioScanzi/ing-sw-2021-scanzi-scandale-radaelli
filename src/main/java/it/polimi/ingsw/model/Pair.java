package it.polimi.ingsw.model;

import java.util.Objects;

public class Pair<A, B> {
    public A key;
    public B value;

    public Pair(A key, B value) {
        this.key = key;
        this.value = value;
    }

    public void setPair(A key, B value){
        this.key=key;
        this.value=value;
    }

    public void setKey(A key) {
        this.key = key;
    }

    public void setValue(B value) {
        this.value = value;
    }

    public A getKey(){
        return key;
    }

    public B getValue(){
        return value;
    }

    @Override
    public String toString() {
        return "(" +
                (key != null ? key.toString() : "null") +
                ", " +
                (value != null ? value.toString() : "null") +
                ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) &&
                Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
