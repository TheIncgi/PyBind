package com.theincgi.pyBind.pyVals;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.theincgi.pyBind.Cleanup;
import com.theincgi.pyBind.PyBind;
import com.theincgi.pyBind.PyBindException;

public class JavaBinds {
	private static final ThreadLocal< HashMap<Long, Object> > pyOnly = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< HashMap<Long, Reference<Object>>> javaRefWatch = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< HashMap<Reference<Object>, Long> > lookup  = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< AtomicLong > refID = ThreadLocal.withInitial( AtomicLong::new );
	
	private JavaBinds() {
	}
	
	public long bind( Object obj ) {
		return lookup.get().computeIfAbsent( obj, v -> {
			long ref = refID.get().getAndIncrement();
			var soft = Cleanup.soft(obj, null);
			lookup.get().put(soft, ref);
			javaRefWatch.get().put(ref, soft);
			return ref;
		} );
	}
	
	public void unbind( long ref ) {
		
	}
	public void unbind( Object obj ) {
		
	}
	
	public boolean isLocallyReferenced(Object obj) {
		long ref = lookup.get().get(obj);
		return javaRefWatch.get().get(ref).get()!=null;
	}
	public boolean isRemotelyReferenced(Object obj) throws PyBindException, IOException {
		long ref = lookup.get().get(obj);
		return PyBind.getSocketHandler().countRef(ref) > 1;
	}
	
	
}
