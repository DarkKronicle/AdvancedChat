package net.darkkronicle.advancedchat.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListIterator<T> {

    private Iterator<T> iterator;
    private ArrayList<T> list;

    public ListIterator(List<T> list) {
        this.list = new ArrayList<>(list);
        this.iterator = this.list.iterator();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public T next() {
        T val = this.iterator.next();
        this.list.remove(val);
        return val;
    }

    public List<T> getValues() {
        return this.list;
    }
}
