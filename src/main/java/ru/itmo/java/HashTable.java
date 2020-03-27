package ru.itmo.java;

public class HashTable {

    private static final int DEFAULT_INITIAL_CAPACITY = 2048;
    private static final double DEFAULT_LOADFACTOR = 0.5;

    private entry[] table;
    private boolean[] used;
    private int size = 0;
    private final double loadFactor;
    private int capacity;
    private int threshold;

    private static class entry {
        private Object Key;
        private Object Value;

        entry(Object key, Object value) {
            this.Key = key;
            this.Value = value;
        }
    }

    HashTable() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOADFACTOR);
    }

    HashTable(int initialCapacity) {
        this(initialCapacity, 0.5);
    }

    HashTable(int initialCapacity, double loadFactor) {
        this.capacity = getNextPowOfTwo(initialCapacity);
        this.loadFactor = loadFactor;
        table = new entry[capacity];
        used = new boolean[capacity];
        threshold = (int) (capacity * loadFactor);
        for (int i = 0; i < capacity; i++) {
            used[i] = false;
        }
    }


    Object put(Object key, Object value) {
        if (size > threshold || size == capacity) {
            resize();
        }

        entry element = new entry(key, value);
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
        entry prevElement = table[index];
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

    private int getNextPowOfTwo(int a) {
        int twoPow = 1;
        while (true) {
            if (twoPow >= a) {
                return twoPow;
            }
            twoPow *= 2;
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
            if ((table[hash] != null && table[hash].Key.equals(key)) || (table[hash] == null && !used[hash])) {
                return hash;
            }
            hash++;
            if (hash == table.length) {
                hash = 0;
            }
        }
    }

    private void resize() {
        entry[] prevTable = table;
        capacity *= 2;
        table = new entry[capacity];
        used = new boolean[capacity];
        size = 0;
        for (HashTable.entry entry : prevTable) {
            if (entry != null) {
                put(entry.Key, entry.Value);
            }
        }
        threshold = (int) (capacity * loadFactor);
    }
}