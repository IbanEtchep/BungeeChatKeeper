package fr.iban.bungeechatkeeper;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;

public class BoundedList<T> implements Iterable<T> {
    private final LinkedList<T> list;
    private final int capacity;

    public BoundedList(int capacity) {
        this.list = new LinkedList<>();
        this.capacity = capacity;
    }

    public void add(T element) {
        if (list.size() == capacity) {
            list.removeFirst();
        }
        list.addLast(element);
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}