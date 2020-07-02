package me.reasonless.codeutilities.objects;

import java.util.Comparator;
import java.util.Map;

//Credit: https://stackoverflow.com/questions/11504902/simple-way-to-count-occurrences-of-string-and-sort-by-them
public class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {
  Map<K, V> map;
  public ValueComparator(Map<K, V> base) {
    this.map = base;
  }
  @Override
  public int compare(K o1, K o2) {
    return map.get(o2).compareTo(map.get(o1));
  }
}
