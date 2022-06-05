package com.theincgi.pyBind.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Soft ref keys
 * */
public class SoftHashMap<T, U> implements Map<T,U>, Iterable<Entry<T, U>>{
	
	private LinkedList<SoftReference<T>>[] keys;
	private LinkedList<U>[] values;
	
	private ReferenceQueue<T> refQueue;
	
	private int size = 0;
	
	public SoftHashMap() {
		this(16);
	}
	
	@SuppressWarnings("unchecked")
	public SoftHashMap( int initalCapacity ) {
		keys = new LinkedList[ initalCapacity ];
		values = new LinkedList[ initalCapacity ];
	}
	
	
	public SoftHashMap( ReferenceQueue<T> refQueue ) {
		this(16);
		this.refQueue = refQueue;
	}
	
	@SuppressWarnings("unchecked")
	public SoftHashMap( ReferenceQueue<T> refQueue, int initalCapacity ) {
		this.refQueue = refQueue;
		keys = new LinkedList[ initalCapacity ];
		values = new LinkedList[ initalCapacity ];
	}
	
	
	
	public synchronized int capacity() {
		return keys.length;
	}
	
	/**May be over 1*/
	public synchronized float loadFactor() {
		return size() / (float)capacity();
	}
	
	/**
	 * @throws IllegalArgumentException if newCapacity is less than {@link #size()}
	 * */
	@SuppressWarnings("unchecked")
	public synchronized void resize( int newCapacity ) {
		if(newCapacity < size) throw new IllegalArgumentException("new capacity %d is smaller than the current size %d".formatted(newCapacity, size));
		
		LinkedList<SoftReference<T>>[] keys   = new LinkedList[ newCapacity ];
		LinkedList<U>[]                values = new LinkedList[ newCapacity ];
		
		
		for(int h = 0; h<keys.length; h++) {
			var keysList = keys[ h ];
			if( keysList == null ) continue;
			var valsList = values[ h ];
			var kIt = keysList.iterator();
			var vIt = valsList.iterator();
			while( kIt.hasNext() ) {
				_put( keys, values, kIt.next(), vIt.next() );
			}
		}
	}
	
	@Override
	public synchronized U put(T t, U u) {
		if(loadFactor() > 2.5)
			resize( (int) Math.ceil( loadFactor() / .75 ) );
		
		SoftReference<T> soft;
		if( refQueue == null ) {
			soft = new SoftReference<T>( t );
		} else {
			soft = new SoftReference<T>(t, refQueue);
		}
		var old = _put( this.keys, this.values, soft, u );
		return old.isEmpty() ? null : old.get();
	}
	
	public synchronized Optional<U> put(SoftReference<T> t, U u) {
		return _put( this.keys, this.values, t, u );
	}
	
	@Override
	public synchronized U get(Object key) {
		if( key == null ) throw new NullPointerException("key may not be null");
		int hash = key.hashCode() % keys.length;
		var keyList = keys[ hash ];
		var valList = values[ hash ];
		
		int i = 0;
		for( var soft : keyList ) {
			if( key.equals( soft ) || key.equals( soft.get() )) {
				return valList.get(i);
			}
			i++;
		}
		return null;
	}
	
	@Override
	public synchronized void clear() {
		for(int i = 0; i<keys.length; i++) {
			var k = keys[ i ];
			if( k == null ) continue;
			var v = values[ i ];
			k.clear();
			v.clear();
		}
		size = 0;
	}
	
	/**returns old value or null*/
	private synchronized static <T,U> Optional<U> _put(LinkedList<T>[] keys, LinkedList<U>[] values, T t, U u) {
		if( t==null )
			throw new NullPointerException("Key in SoftHashMap must not be null");
		
		int hash = t.hashCode() % keys.length;
		
		if( keys[ hash ] == null ) {
			keys[ hash ]   = new LinkedList<>();
			values[ hash ] = new LinkedList<>();
		}
		var keyList = keys[ hash ];
		var valList = values[ hash ];
		var it = keyList.listIterator();
		
		int index = -1;
		while(it.hasNext()) {
			var k = it.next(); index++;
			if( t.equals(k) ) {
				var vIt = valList.listIterator( index );
				try {
					return Optional.ofNullable(vIt.next());
				}finally{
					vIt.set(u);
				}
			}
		}
		keys[ hash ].add(t);
		values[ hash ].add(u);
		return Optional.empty();
	}
	
	private synchronized Optional<U> _remove( Object key ) {
		if( key==null )
			throw new NullPointerException("Key in SoftHashMap must not be null");
		int hash = key.hashCode() % keys.length;
		var keyList = keys[ hash ];
		var valList = values[ hash ];
		var kIt = keyList.listIterator();
		var vIt = valList.listIterator();
		
		while(kIt.hasNext()) {
			SoftReference<T> t = kIt.next();
			U u = vIt.next();
			if( key.equals( t ) || key.equals( t.get() ) ) {
				kIt.remove();
				vIt.remove();
				return Optional.ofNullable(u);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public synchronized Set<T> keySet() {
		Set<T> keySet = new HashSet<>();
		for(int i = 0; i<keys.length; i++) {
			if(keys[i] != null)
				for( var ref : keys[i] )
					keySet.add( ref.get() );
		}
		return keySet;
	}
	
	public synchronized Set<SoftReference<T>> softKeySet() {
		Set<SoftReference<T>> keySet = new HashSet<>();
		for(int i = 0; i<keys.length; i++) {
			if(keys[i] != null)
				for( var ref : keys[i] )
					keySet.add( ref );
		}
		return keySet;
	}
	
	@Override
	public synchronized Set<U> values() {
		Set<U> keySet = new HashSet<>();
		for(int i = 0; i<keys.length; i++) {
			if(keys[i] != null)
				for( var v : values[i] )
					keySet.add( v );
		}
		return keySet;
	}
	
	@Override
	public boolean containsKey(Object key) {
		if( key==null )
			throw new NullPointerException("Key in SoftHashMap must not be null");
		int hash = key.hashCode() % keys.length;
		var keyList = keys[ hash ];
		for( var k : keyList )
			if( key.equals( k ) || key.equals( k.get() ) )
				return true;
		return false;
	}
	
	@Override
	public boolean containsValue(Object value) {
		return values().contains( value );
	}
	
	@Override
	public boolean isEmpty() {
		return size==0;
	}
	
	@Override
	public void putAll(Map<? extends T, ? extends U> m) {
		for( var e : m.entrySet() )
			put( e.getKey(), e.getValue() );
	}
	
	@Override
	public U remove(Object key) {
		var opt = _remove(key);
		if(opt.isPresent())
			size--;
		return opt.isPresent() ? opt.get() : null;
	}
	
	@Override
	public synchronized Set<Entry<T, U>> entrySet() {
		Set<Entry<T, U>> set = new HashSet<>();
		var it = iterator();
		while(it.hasNext())
			set.add(it.next());
		return set;
	}
	
	@Override
	public java.util.Iterator<Entry<T, U>> iterator() {
		return new java.util.Iterator<Entry<T,U>>() {
			int i = -1;
			int n = 0;
			ListIterator<SoftReference<T>> kIt;
			ListIterator<U> vIt;
			@Override
			public boolean hasNext() {
				return n < size();
			}
			
			@Override
			public Entry<T, U> next() {
				if(!hasNext()) throw new NoSuchElementException();
				
				if(kIt == null || !kIt.hasNext()) {
					while( keys[++i] == null || keys[i].size() == 0 );
					kIt = keys[ i ].listIterator();
					vIt = values [ i ].listIterator();
				}
				
				final var key = kIt.next();
				final var val = vIt.next();
				
				n++;
				return new Entry<T, U>() {
					@Override
					public T getKey() {
						return key.get();
					}
					
					@Override
					public U getValue() {
						return val;
					}

					@Override
					public U setValue(U value) {
						try {
							return val;
						}finally {
							vIt.set(value);
						}
					}
				};
			}
			
			@Override
			public void remove() {
				if( i >= 0 && kIt != null ) {
					kIt.remove();
					vIt.remove();
				}
			}
			
		};
	}
	
	public java.util.Iterator<Entry<SoftReference<T>, U>> softIterator() {
		return new java.util.Iterator<Entry<SoftReference<T>,U>>() {
			int i = -1;
			int n = 0;
			ListIterator<SoftReference<T>> kIt;
			ListIterator<U> vIt;
			@Override
			public boolean hasNext() {
				return n < size();
			}
			
			@Override
			public Entry<SoftReference<T>, U> next() {
				if(!hasNext()) throw new NoSuchElementException();
				
				if(kIt == null || !kIt.hasNext()) {
					while( keys[++i] == null || keys[i].size() == 0 );
					kIt = keys[ i ].listIterator();
					vIt = values [ i ].listIterator();
				}
				
				final var key = kIt.next();
				final var val = vIt.next();
				
				n++;
				return new Entry<SoftReference<T>, U>() {
					@Override
					public SoftReference<T> getKey() {
						return key;
					}
					
					@Override
					public U getValue() {
						return val;
					}

					@Override
					public U setValue(U value) {
						try {
							return val;
						}finally {
							vIt.set(value);
						}
					}
				};
			}
			
			@Override
			public void remove() {
				if( i >= 0 && kIt != null ) {
					kIt.remove();
					vIt.remove();
				}
			}
			
		};
	}
	
}
