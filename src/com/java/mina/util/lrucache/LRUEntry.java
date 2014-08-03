package com.java.mina.util.lrucache;

public class LRUEntry<K, V> {
	
	private K key;
	
	private V value;
	
	private LRUEntry<K, V> prev;
	
	private LRUEntry<K, V> next;

	
	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public LRUEntry<K, V> getPrev() {
		return prev;
	}

	public void setPrev(LRUEntry<K, V> prev) {
		this.prev = prev;
	}

	public LRUEntry<K, V> getNext() {
		return next;
	}

	public void setNext(LRUEntry<K, V> next) {
		this.next = next;
	}
}
