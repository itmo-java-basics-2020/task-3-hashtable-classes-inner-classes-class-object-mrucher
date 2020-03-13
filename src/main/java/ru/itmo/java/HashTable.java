package ru.itmo.java;

public class HashTable {

    private static class Entry {
        private Object Key;
        private Object Value;

        Entry(Object key, Object value) {
            this.Key = key;
            this.Value = value;
        }
    }

    private Entry[] table;
    private boolean[] used;
    private int size = 0;
    private final double loadFactor;
    private int capacity;
    private int threshold;

    private int getNextPowOfTwo(int a) {
        int twoPow = 1;
        while (true) {
            if (twoPow >= a) {
                return twoPow;
            }
            twoPow *= 2;
        }
    }

    HashTable() {
        this(2048, 0.5);
    }

    HashTable(int initialCapacity) {
        this(initialCapacity, 0.5);
    }

    HashTable(int initialCapacity, double loadFactor) {
        this.capacity = getNextPowOfTwo(initialCapacity);
        this.loadFactor = loadFactor;
        table = new Entry[capacity];
        used = new boolean[capacity];
        threshold = (int) (capacity * loadFactor);
        for (int i = 0; i < capacity; i++) {
            used[i] = false;
        }
    }

    private int find(Object key, boolean isSearchedFree) {
        int hash = Math.abs(key.hashCode()) % table.length;
        if (isSearchedFree) {
            while (true) {
                if (table[hash] == null) {
                    return hash;
                }
                hash++;
                if (hash == table.length) {
                    hash = 0;
                }
            }
        }
        while (true) {
            if (table[hash] != null && table[hash].Key.equals(key)) {
                return hash;
            }
            if (table[hash] == null && !used[hash]) {
                return hash;
            }
            hash++;
            if (hash == table.length) {
                hash = 0;
            }
        }
    }

    void resize() {
        Entry[] prevTable = table;
        capacity *= 2;
        table = new Entry[capacity];
        used = new boolean[capacity];
        size = 0;
        for (Entry entry : prevTable) {
            if (entry != null) {
                put(entry.Key, entry.Value);
            }
        }
        threshold = (int) (capacity * loadFactor);
    }

    Object put(Object key, Object value) {
        if (size > threshold || size == capacity) {
            resize();
        }

        Entry element = new Entry(key, value);
        Object prevValue = get(key);
        boolean isNull = false;

        if (prevValue == null) {
            isNull = true;
            size++;
        }

        int index = find(key, isNull);
        table[index] = element;
        used[index] = true;
        return prevValue;
    }

    Object get(Object key) {
        int index = find(key, false);
        if (table[index] == null) {
            return null;
        }
        return table[index].Value;
    }

    Object remove(Object key) {
        int index = find(key, false);
        Entry prevElement = table[index];
        if (prevElement != null) {
            table[index] = null;
            size--;
            return prevElement.Value;
        }
        return null;
    }

    int size() {
        return size;
    }
}