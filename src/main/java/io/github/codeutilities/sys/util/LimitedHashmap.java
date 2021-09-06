package io.github.codeutilities.sys.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LimitedHashmap<K,V> extends LinkedHashMap<K,V> {

    int maxSize;

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size()>maxSize;
    }

    public LimitedHashmap(int maxSize) {
        this.maxSize = maxSize;
    }
}
