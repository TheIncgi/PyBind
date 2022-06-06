package com.theincgi.pyBind.pyVals;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.theincgi.pyBind.Cleanup;
import com.theincgi.pyBind.PyBind;
import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.utils.SoftHashMap;

/*
 * if java trys to clean up, and still ref'd in python, move to strong ref
 * if python cleans up/unbinds simply removed from any maps
 * */
public class JavaBinds {
	private static final ThreadLocal< HashMap<Long, Object> > pyOnly = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< HashMap<Long, Reference<Object>>> javaRefWatch = ThreadLocal.withInitial( HashMap::new );
	private static final ThreadLocal< SoftHashMap<Object, Long>> lookup  = ThreadLocal.withInitial( SoftHashMap::new );
//	private static final ThreadLocal< AtomicLong > refID = ThreadLocal.withInitial( AtomicLong::new );
	
	
	private JavaBinds() {
		
	}
	
	private static long getNextRef() throws PyBindException {
		try {
			return PyBind.getSocketHandler().mkRef();
		} catch ( InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	
	public static long bind( Object obj ) throws PyBindException {
		long ref = getNextRef();
		return bind( obj, ref );
	}
	
	/**
	 * Used when using static get(long ref), re-uses old ref id
	 * */
	private static long bind( Object obj, long ref ) {
		return lookup.get().computeIfAbsent( obj, v -> {
			var soft = Cleanup.soft(obj, a->{
				try {
					if(isRemotelyReferenced(a))
						pyOnly.get().put(ref, a);
				} catch (PyBindException | IOException | InterruptedException | ExecutionException e) {
					e.printStackTrace();
					//GC'd!
				}
			});
			lookup.get().put(soft, ref);
			javaRefWatch.get().put(ref, soft);
			return ref;
		} );
	}
	
	public static void unbind( long ref ) {
		var obj = javaRefWatch.get().remove( ref );
		lookup.get().remove( obj );
		pyOnly.get().remove( ref );
		
	}
	public static void unbind( Object obj ) {
		Long ref = lookup.get().get( obj );
		if(ref != null )
			unbind( ref );
	}
	
	public static boolean isLocallyReferenced(Object obj) {
		long ref = lookup.get().get(obj);
		return javaRefWatch.get().get(ref).get()!=null;
	}
	public static boolean isRemotelyReferenced(Object obj) throws PyBindException, IOException, InterruptedException, ExecutionException {
		long ref = lookup.get().get(obj);
		return PyBind.getSocketHandler().countRef(ref) > 1;
	}
	
	public static Object instance(String cName, PyVal... args) throws ClassNotFoundException {
		Class<?> cls = Class.forName(cName);
		var cons = chooseBestMatch(cls.getConstructors(), args);
		Object[] objs = new Object[ args.length ];
		
		int i = 0;
		for( ; i < cons.getParameterCount() - (cons.isVarArgs() ? 1 : 0); i++ ) {
			var toCls = cons.getParameterTypes()[i];
			var listTypes = toCls.arrayType();
			//TODO
//			objs[ i ] = Common.coerce(args[i], cons.getParameterTypes()[ i ], if(false) {})
		}
		
		for( ;i < args.length; i++) {
			//TODO
//			objs[i] = Common.coerce(args[i], cons.getParameterTypes()[i], if() {})
		}
		return null;
	}
	
	public static <T> Constructor<T> chooseBestMatch( Constructor<T>[] options, PyVal... args) {
		Constructor<T> best = null;
		int score = 0x10000;
		for ( var c : options ) {
			int s = scoreArgs( c, args );
			if ( s < score ) {
				score = s;
				best = c;
				if ( score == 0 )
					break;
			}
		}
		
		// any match? 
		if ( best == null )
			throw new PyBindException("no coercible public method");
		
		// invoke it
		return best;
	}
	
	private static <T> int scoreArgs( Constructor<T> c, PyVal... args) {
		int n = args.length;
		var params = c.getParameterTypes();
		var fixedargs = c.isVarArgs() ? Arrays.copyOfRange(params, 0, params.length-1) : params;
		var varargs = c.isVarArgs()? params[ params.length - 1 ] : null;
		
		int s = n>fixedargs.length && varargs==null? 0x100 * (n-fixedargs.length): 0;
		for ( int j=0; j<fixedargs.length; j++ )
			s += score( fixedargs[j], args[j] );
		if ( varargs != null )
			for ( int k=fixedargs.length; k<n; k++ )
				s += score( varargs, args[k] );
		return s;
	}


	private static int score(Class<?> cls, PyVal arg) {
		if( arg.isFloat() )
			return inheritanceLevels(cls, Double.class );
		if( arg.isInt() )
			return inheritanceLevels(cls, Integer.class);
		if( arg.isBool() )
			return inheritanceLevels(cls, Boolean.class);
		if( arg.isStr() )
			return inheritanceLevels(cls, String.class);
		if( arg.isDict() )
			return inheritanceLevels(cls, Map.class);
		if( arg.isNone() )
			return 0x10;
		if( arg.isJavaObj() )
			return inheritanceLevels(cls,  arg.checkJavaObject().getObjectType() );
			
		return 0x10000;
	}
	
	
	
	
	/** 
	 * Determine levels of inheritance between a base class and a subclass
	 * @param baseclass base class to look for
	 * @param subclass class from which to start looking
	 * @return number of inheritance levels between subclass and baseclass, 
	 * or SCORE_UNCOERCIBLE if not a subclass
	 * 
	 * Modified source from LuaJ
	 */
	private static final int inheritanceLevels( Class<?> baseclass, Class<?> subclass ) {
		if ( subclass == null )
			return 0x10000;
		if ( baseclass == subclass )
			return 0;
		int min = Math.min( 0x10000, inheritanceLevels( baseclass, subclass.getSuperclass() ) + 1 );
		Class<?>[] ifaces = subclass.getInterfaces();
		for ( int i=0; i<ifaces.length; i++ ) 
			min = Math.min(min, inheritanceLevels(baseclass, ifaces[i]) + 1 );
		return min;
	}


	public static boolean checkExists(long ref) {
		return javaRefWatch.get().containsKey( ref ) || pyOnly.get().containsKey( ref );
	}


	public static Object get(long ref) {
		if( pyOnly.get().containsKey( ref ) ) {
			var obj = pyOnly.get().get( ref );
			pyOnly.get().remove( ref );
			bind( obj, ref );
		}
		return javaRefWatch.get().get( ref ).get();
	}
}
