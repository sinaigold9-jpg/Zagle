package com.zajel.app.core;

import java.util.LinkedHashMap;
import java.util.Map;

/** Bounded in-memory cache to avoid retaining unbounded server responses. */
public final class BoundedMemoryCache<K, V> {
    private final Map<K, V> entries;
    public BoundedMemoryCache(final int maximumEntries) {
        if (maximumEntries < 1) throw new IllegalArgumentException("maximumEntries must be positive");
        entries = new LinkedHashMap<K, V>(maximumEntries, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > maximumEntries; }
        };
    }
    public synchronized V get(K key) { return entries.get(key); }
    public synchronized void put(K key, V value) { if (key != null && value != null) entries.put(key, value); }
    public synchronized void remove(K key) { entries.remove(key); }
    public synchronized void clear() { entries.clear(); }
}
