package com.theincgi.pyBind.pyVals;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class JavaBinds {
	private static final ThreadLocal< HashMap<Long, Object> > objects = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< HashMap<Object, Long> > lookup  = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< AtomicLong > refID = ThreadLocal.withInitial( AtomicLong::new );
	
	private JavaBinds() {
		// TODO Auto-generated constructor stub
	}
	
	public long bind( Object obj ) {
		long id = lookup.get().computeIfAbsent( obj, v -> {
			long ref = refID.get().getAndIncrement();
			objects.get().put(ref, v);
			return ref;
		} );
	}
	
}
