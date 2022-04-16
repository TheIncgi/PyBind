package com.theincgi.pyBind;

import static com.theincgi.pyBind.PyBindSockerHandler.Actions.EVAL;
import static com.theincgi.pyBind.PyBindSockerHandler.Actions.EXEC;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.COPY;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.IGNORE;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.REF;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.PyBindSockerHandler.Actions;
import com.theincgi.pyBind.PyBindSockerHandler.ResultMode;
import com.theincgi.pyBind.pyVals.PyFunc;
import com.theincgi.pyBind.pyVals.PyVal;

public class PyBind implements AutoCloseable, Closeable{
	
	private static PyBindings pyBinds;
	private static PyBindSockerHandler socketHandler;
	private static Process pyProcess;
	
	private static File pythonCmd = Common.findOnPath("python.exe");
	private static File pythonWorkingDir = new File(System.getProperty("user.dir"));
	
	public static void setPythonCmd(File pythonCmd) {
		PyBind.pythonCmd = pythonCmd;
	}
	
	public static void init() throws IOException, InterruptedException {
		init(0); //pick a port auto
	}
	/**
	 * 0 - pick port and start py<br>
	 * other - wait for connection on port
	 * @throws InterruptedException 
	 * */
	public static void init(int port) throws IOException, InterruptedException {
		if(port == 0)
			initJavaHost();
		else
			initPythonHost( port );
		
		pyBinds = bindPy(PyBindings.class);
	}
	private static void initJavaHost() throws IOException, InterruptedException {
		final ServerSocket ss = new ServerSocket(0, 0, null);
		final int port = ss.getLocalPort();
		
		var socket = Executors.newSingleThreadExecutor().submit(()->{
			try {
				System.out.println("Waiting for connection on port "+port);
				return ss.accept();
			}finally {
				ss.close();
			}
		});
		
		
		
		pyProcess = launchPython(port);
		
		try {
			PyBind.socketHandler = new PyBindSockerHandler(socket.get(15, TimeUnit.SECONDS));
			System.out.println("Connected!");
		} catch (InterruptedException e) {
			throw e;
		} catch (ExecutionException e) {
			throw new IOException(e);
		} catch (TimeoutException e) {
//			new String(pyProcess.getInputStream().readAllBytes())
//			new String(pyProcess.getErrorStream().readAllBytes())
			pyProcess.destroy();
			throw new IOException(e);
		}
	}
	private static void initPythonHost(int port) throws IOException {
		PyBind.socketHandler = new PyBindSockerHandler(new Socket(InetAddress.getByName(null), port));
	}
	
	private static Process launchPython(int port) throws IOException {
		putScript("init.py");
		ProcessBuilder pb = new ProcessBuilder(pythonCmd.getAbsolutePath(),"-u", "pyBind/init.py", port+"");
		pb.directory(pythonWorkingDir);
		pb.inheritIO();
		return pb.start();
	}
	
	private static void putScript(String f) throws IOException {
		File to = new File(new File(pythonWorkingDir,"pyBind"), f);
		if(to.exists()) return;
		
		to.getParentFile().mkdirs();
		try(FileOutputStream fos = new FileOutputStream(to)){
			try(var stream = PyBind.class.getResourceAsStream(f)){
				if(stream==null)
					throw new NullPointerException("Couldn't load resource '"+f+"'");
				fos.write(stream.readAllBytes());
			}
		}
		System.out.println("Copied "+f);
	}
	
	public static String getPyVersion() {
		return pyBinds.getVersion();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T bindPy(Class<T> cls) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		WeakHashMap<String, PyFunc> generated = new WeakHashMap<>();
		return (T) Proxy.newProxyInstance(cl, new Class[] {cls}, new java.lang.reflect.InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(method.isAnnotationPresent(Py.class)) {
					Py pyInfo = method.getAnnotation(Py.class);
					var paramInfo = method.getParameterAnnotations();
					String key = pyInfo.lib() + "#" + pyInfo.name();
					PyFunc func = generated.computeIfAbsent(key, k->{						
						return bindPy(pyInfo.lib(), pyInfo.name());
					});
					
					JSONObject callInfo = new JSONObject();
					JSONArray callArgs = new JSONArray();
					JSONObject callKwargs = new JSONObject();
					callInfo.put("args", callArgs);
					callInfo.put("kwargs", callKwargs);
					
					//coerce input
					for(int i = 0; i < args.length; i++) {
						Object arg = args[i];
						PyVal pyArg = PyVal.toPyVal(arg);
						String kwargName = Common.findKwargName(paramInfo[i]);
						if( kwargName != null ) {
							callKwargs.put(kwargName, pyArg.asJsonValue());
						}else {
							callArgs.put(pyArg.asJsonValue());
						}
					}
					
					JSONObject f = socketHandler.send(Actions.CALL, pyInfo.mode(), callInfo);
					
					Class rType = method.getReturnType();
					if(rType.equals(Void.class))
						return null;
					
					PyVal result = PyVal.fromJson(f);
					return Common.coerce(result, rType);
				}
				throw new RuntimeException("Missing @Py on "+method.getName());
			}});
	}
	
	@SuppressWarnings("unchecked")
	public static PyFunc bindPy(String lib, String name) {
		return null;
	}
	
	public static PyBindSockerHandler getSocketHandler() {
		return socketHandler;
	}
	
	public static void exec(String python) {
		socketHandler.send(EXEC, IGNORE, python);
	}
	public static PyVal eval(String python, PyVal...args) {
		return socketHandler.send(EVAL, COPY, python, args);
	}
	public static PyVal evalRef(String python, PyVal...args) {
		return socketHandler.send(EVAL, REF, python, args);
	}
	
	
	
	public void close() throws IOException {
		socket.close();
		pyProcess.destroy();
	};
	
	public enum BindMethod {
		PROCESS,
		SOCKET,
	}
	
}
