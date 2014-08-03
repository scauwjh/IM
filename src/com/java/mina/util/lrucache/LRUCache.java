package com.java.mina.util.lrucache;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;


/**
 * <p>LRUCache</p>
 * <p>Use LRU algorithm to solve the message queue problem</p>
 * @author wjh
 * @date 2014-08-03
 */
public class LRUCache<K, V> {
	
	private HashMap<K, LRUEntry<K, V>> map;
	private Vector<LRUEntry<K, V>> freeEntrys;
	private LRUEntry<K, V> head;
	private LRUEntry<K, V> tail;
	private LRUEntry<K, V>[] entrys;
	
	/**
	 * init
	 * @param size
	 */
	@SuppressWarnings("unchecked")
	public LRUCache(Integer size) {
		entrys = new LRUEntry[size];
		map = new HashMap<K, LRUEntry<K, V>>();
		freeEntrys = new Vector<LRUEntry<K, V>>();
		for (int i = 0; i < size; i++) {
			entrys[i] = new LRUEntry<K, V>();
			freeEntrys.add(entrys[i]);
		}
		head = new LRUEntry<K, V>();
		tail = new LRUEntry<K, V>();
		
		head.setPrev(null);
		head.setNext(tail);
		tail.setPrev(head);
		tail.setNext(null);
	}
	
	/**
	 * put function
	 * @param key
	 * @param value
	 */
	public void put(K key, V value) {
		LRUEntry<K, V> node = map.get(key);
		if (node != null) {
			detach(node);
			node.setValue(value);
			attach(node);
		} else {
			if (freeEntrys.isEmpty()) {
				// cache is full, remove the oldest message, and save to DB
				node = tail.getPrev();
				map.remove(node.getKey());
				remove(node);
				detach(node);
			} else {
				node = freeEntrys.remove(0);
			}
			if (node == null)
				System.out.println("test");
			node.setValue(value);
			node.setKey(key);
			map.put(key, node);
			attach(node);
		}
	}
	
	/**
	 * get function
	 * @param key
	 * @return
	 */
	public V get(K key) {
		LRUEntry<K, V> node = map.get(key);
		if (node != null) {
			// update link
			detach(node);
			attach(node);
			return node.getValue();
		}
		return null;
	}
	
	/**
	 * <p>to deal with the oldest node tha detach
	 * from the link by override this function</p>
	 * @param node
	 */
	protected void remove(LRUEntry<K, V> node) {
		
	}
	
	/**
	 * detach the node from the link
	 * @param node
	 */
	protected void detach(LRUEntry<K, V> node) {
		node.getPrev().setNext(node.getNext());
		node.getNext().setPrev(node.getPrev());
	}
	
	/**
	 * attach to the head of the link
	 * @param node
	 */
	protected void attach(LRUEntry<K, V> node) {
		node.setPrev(head);
		node.setNext(head.getNext());
		head.setNext(node);
		node.getNext().setPrev(node);
	}
	
	
	/**
	 * main function for test
	 * @param args
	 */
	public static void main(String[] args) {
		LRUCache<String, String> cache = new LRUCache<String, String>(3);
		Scanner in = new Scanner(System.in);
		String method = null;
		String value = null;
		String key = null;
		while(true) {
			method = in.next();
			if (method.equals("exit"))
				break;
			key = in.next();
			if (method.equals("get")) {
				value = cache.get(key);
				System.out.println("value: " + value);
				continue;
			}
			value = in.next();
			cache.put(key, value);
		}
	}
}
