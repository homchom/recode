package io.github.codeutilities.util.file;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class stores objects in a hash map, and discards them if they remain unused
 * for a certain period of time. Once discarded, objects will be moved to a weakly
 * referenced hash map and remain there until garbage collected. If an expired object
 * is accessed before it is garbage collected, it will be moved back to the strongly
 * referenced hash map.
 * <p>
 * All methods in this class are thread safe.
 */
public class WeakStorage<K, V> implements Closeable {

    private final long dataTimeout;
    /**
     * The amount of time between data timeout checks, in milliseconds.
     */
    private final long discardDelay;

    private final Thread timeoutThread;
    private final Map<K, Entry> storage = new HashMap<>();
    private final Map<K, WeakReference<V>> weakStorage = new HashMap<>();
    private boolean closed = false;

    @SuppressWarnings("BusyWait")
    public WeakStorage(long dataTimeout) {
        this.dataTimeout = dataTimeout;
        this.discardDelay = dataTimeout * 3;

        this.timeoutThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(discardDelay);
                    processTimeout();
                    weakStorage.entrySet().removeIf((entry) -> entry.getValue().get() == null);
                }
            } catch (InterruptedException ignored) {
            }
        });
        timeoutThread.start();
        timeoutThread.setName("WeakStorage Check Thread");
        timeoutThread.setPriority(2);
    }

    private void processTimeout() {
        long currentTime = System.currentTimeMillis();

        Iterator<Map.Entry<K, Entry>> iterator = storage.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Entry> entry = iterator.next();
            Entry dataEntry = entry.getValue();
            long timeElapsed = currentTime - dataEntry.loadedTime;

            if (timeElapsed > dataTimeout) {
                iterator.remove();
                weakStorage.put(entry.getKey(), new WeakReference<>(dataEntry.data));
            }
        }
    }

    /**
     * If your object is not found, it was most likely garbage collected.
     */
    public synchronized V get(K key) {
        if (closed) {
            throw new IllegalStateException();
        }

        Entry storedObject = storage.get(key);
        if (storedObject != null) {
            storedObject.loadedTime = System.currentTimeMillis();
            return storedObject.data;
        }

        WeakReference<V> reference = weakStorage.get(key);
        if (reference != null) {
            V weakObject = reference.get();
            if (weakObject != null) {
                // See comment in the put method
                put(key, weakObject);
                return weakObject;
            }
        }

        return null;
    }

    public V getOrDefault(K key, V value) {
        V stored = get(key);

        if (stored == null) {
            return value;
        }

        return stored;
    }

    public boolean isStored(K key) {
        return get(key) != null;
    }

    /**
     * Does not push the object back to the main map.
     */
    public synchronized boolean isWeaklyStored(K key) {
        return weakStorage.get(key).get() != null;
    }

    public void put(K key, V object) {
        if (closed) {
            throw new IllegalStateException();
        }

        Entry entry = new Entry(object, System.currentTimeMillis());

        synchronized (this) {
            // It is possible for the object to already be present in
            // the weak hash map, but that should not have any effect
            // on functionality
            storage.put(key, entry);
        }
    }

    public synchronized V remove(K key) {
        Entry entry = storage.remove(key);
        WeakReference<V> reference = weakStorage.remove(key);

        if (entry != null) {
            return entry.data;
        } else if (reference != null) {
            return reference.get();
        }

        return null;
    }

    @Override
    public synchronized void close() {
        closed = true;
        storage.clear();
        weakStorage.clear();
        timeoutThread.interrupt();
    }


    private class Entry {

        private final V data;
        private long loadedTime;

        public Entry(V data, long loadedTime) {
            this.data = data;
            this.loadedTime = loadedTime;
        }
    }
}
