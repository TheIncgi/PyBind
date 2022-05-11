package com.theincgi.pyBind;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class Cleanup {
	
	public static final Cleanup cleanup = new Cleanup();
	
	
	private static ReferenceQueue<Object> queue = new ReferenceQueue<>();
	
	static {
		Thread thread = new Thread(Cleanup::cleanup, "cleanup");
		thread.setDaemon(true);
		thread.start();
	}
	private Cleanup() {
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> WeakReference<T> weak( T obj, Consumer<T> onCleanup ) {
		return (WeakReference<T>) new WeakCleanupAction<T>(obj, onCleanup);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> SoftReference<T> soft( T obj, Consumer<T> onCleanup ) {
		return (SoftReference<T>) new SoftCleanupAction<T>(obj, onCleanup);
	}
	
	@SuppressWarnings("unchecked")
	private static void cleanup() {
		try {
			while(true) {
				var ref = queue.remove();
				if(ref instanceof WeakCleanupAction<?> r)
					r.cleanup();
				else if(ref instanceof SoftCleanupAction<?> r)
					r.cleanup();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private static class WeakCleanupAction<T> extends WeakReference<Object> {
		private Consumer<T> onCleanup;
		public WeakCleanupAction( T obj, Consumer<T> onCleanup ) {
			super(obj, queue);
			this.onCleanup = onCleanup;
		}
		@SuppressWarnings("unchecked")
		protected void cleanup() {
			onCleanup.accept((T) get());
		}
	}
	
	private static class SoftCleanupAction<T> extends SoftReference<Object> {
		private Consumer<T> onCleanup;
		public SoftCleanupAction( T obj, Consumer<T> onCleanup ) {
			super(obj, queue);
			this.onCleanup = onCleanup;
		}
		@SuppressWarnings("unchecked")
		protected void cleanup() {
			onCleanup.accept((T) get());
		}
	}
}
