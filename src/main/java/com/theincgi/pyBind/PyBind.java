package com.theincgi.pyBind;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.theincgi.pyBind.pyVals.PyFunc;
import com.theincgi.pyBind.pyVals.PyVal;

public class PyBind implements AutoCloseable, Closeable{
	
	private static PyBindings pyBinds;
	private static Socket socket;
	private static Process pyProcess;
	
	private static String pythonCmd = "python";
	private static File pythonWorkingDir = new File(System.getProperty("user.dir"));
	
	public static void setPythonCmd(String pythonCmd) {
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
		int port = ss.getLocalPort();
		Future<Socket> socket = new FutureTask<Socket>(()->{
			try {
				return ss.accept();
			}finally {
				ss.close();
			}
		});
		
		
		pyProcess = launchPython(port);
		
		try {
			PyBind.socket = socket.get();
		} catch (InterruptedException e) {
			throw e;
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	}
	private static void initPythonHost(int port) throws IOException {
		PyBind.socket = new Socket(InetAddress.getByName(null), port);
	}
	
	private static Process launchPython(int port) throws IOException {
		putScript("pyBind/init.py");
		ProcessBuilder pb = new ProcessBuilder(pythonCmd, port+"");
		pb.directory(pythonWorkingDir);
		pb.inheritIO();
		return pb.start();
	}
	
	private static void putScript(String f) throws IOException {
		File to = new File(pythonWorkingDir, f);
		if(to.exists()) return;
		
		to.getParentFile().mkdirs();
		try(FileOutputStream fos = new FileOutputStream(to)){
			fos.write(PyBind.class.getResourceAsStream(f).readAllBytes());
		}
	}
	
	public static String getPyVersion() {
		return pyBinds.getVersion();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T bindPy(Class<T> cls) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		return (T) Proxy.newProxyInstance(cl, new Class[] {cls}, new java.lang.reflect.InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(method.isAnnotationPresent(Py.class)) {
					Py pyInfo = method.getAnnotation(Py.class);
					
					return pyInfo;
				}
				return null;
			}});
	}
	
	@SuppressWarnings("unchecked")
	public static PyFunc bindPy(String lib, String name) {
		return null;
	}
	
	public static PyVal exec(String python) {
		return null;
	}
	
	private class ListeningClassLoader extends ClassLoader {
		public ListeningClassLoader() {
		}
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			System.out.println(name);
			return super.loadClass(name);
		}
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
