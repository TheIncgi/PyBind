package com.theincgi.pyBind;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.theincgi.pyBind.pyVals.JavaBinds;
import com.theincgi.pyBind.pyVals.PyFunc;
import com.theincgi.pyBind.pyVals.PyRef;
import com.theincgi.pyBind.pyVals.PyTuple;
import com.theincgi.pyBind.pyVals.PyVal;
import com.theincgi.pyBind.utils.Common;

public class PyBindSockerHandler implements Closeable {
	private JSONSocket socket;
	private long msgID = 0;
	HashMap<Long, FutureResponse<JSONObject>> responses = new HashMap<>();
	
	public PyBindSockerHandler(Socket socket) throws IOException {
		this.socket = new JSONSocket(socket);
		
		Thread requestHandler = new Thread(this::handleRequests);
		requestHandler.setDaemon(true);
		requestHandler.start();
	}
	
	private synchronized Future<JSONObject> send(JSONObject json) throws IOException {
		long id = msgID++;
		json.put("id", id);
		FutureResponse<JSONObject> future = new FutureResponse<>();
		responses.put(id, future);
		socket.write(json);
		return future;
	}
	
	private void handleRequests() {
		try {
			while(true) {
				if( socket.hasJSON() ) {
					JSONObject packet = socket.readJSON();
					if(packet.has("id")) {
						FutureResponse<JSONObject> resp = responses.get(packet.getLong("id"));
						resp.setResult( packet.getJSONObject("msg") );
						
					}else{
						String op = packet.getString("op");
						switch(op) {
							case "ERR":{
								var msg = packet.getJSONObject("msg");
								FutureResponse<JSONObject> resp = responses.get(msg.getLong("from"));
								resp.setException(new PyException( msg.getString("msg") ));
								break;
							}
							case "CALL": {
								var msg = packet.getJSONObject("msg");
								long ref = msg.getLong("ref");
								PyVal args = PyVal.fromJson(msg.getJSONObject("args"));
								var obj = JavaBinds.get( ref );
								
								String sig = msg.optString("sig", null);
								Method method = null;
								if( sig!=null  ) {
									
									for(Method m : obj.getClass().getMethods()) {
										if(Common.getMethodSignature( m ).equals(sig)) {
											method = m;
											break;
										}
									}
								}else {
									method = Common.chooseMethod( obj.getClass(), args );
								}
								
								Object[] coerced = Common.coerceArgs( method, args );
								Object result = method.invoke( obj, coerced );
								
							}
								
							default:
								throw new RuntimeException("Unhandled op '"+op+"'");
						}
					}
				}else{
					if(responses.size() > 0) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
					}
				}
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Main request proccessing loop has ended");
	}
	
	public JSONObject send(Actions action, ResultMode mode,  JSONObject info) throws JSONException, InterruptedException, ExecutionException, IOException {
		info.put("op", action.name());
		info.put("mode", mode.name());
		return send(info).get();
	}
	
	
	public PyVal call(long ref, JSONArray args) throws JSONException, InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("args", info);
		info.put("ref", ref);
		JSONObject result = send(Actions.CALL, ResultMode.COPY, info);
		return PyVal.fromJson(result);
	}
	public PyVal call(long ref, JSONObject kwargs) throws JSONException, InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("kwargs", info);
		info.put("ref", ref);
		JSONObject result = send(Actions.CALL, ResultMode.COPY, info);
		return PyVal.fromJson(result);
	}
	public PyVal call(long ref, JSONArray args, JSONObject kwargs) throws JSONException, InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("args", args);
		info.put("kwargs", kwargs);
		info.put("ref", ref);
		JSONObject result = send(Actions.CALL, ResultMode.COPY, info);
		return PyVal.fromJson(result);
	}
	
	public PyVal invoke(long ref, JSONArray args) throws JSONException, InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("args", info);
		info.put("ref", ref);
		JSONObject result = send(Actions.CALL, ResultMode.REF, info);
		return PyVal.fromJson(result);
	}
	public PyVal invoke(long ref, JSONObject kwargs) throws JSONException, InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("kwargs", info);
		info.put("ref", ref);
		JSONObject result = send(Actions.CALL, ResultMode.REF, info);
		return PyVal.fromJson(result);
	}
	public PyVal invoke(long ref, JSONArray args, JSONObject kwargs) throws JSONException, InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("args", args);
		info.put("kwargs", kwargs);
		info.put("ref", ref);
		JSONObject result = send(Actions.CALL, ResultMode.REF, info);
		return PyVal.fromJson(result);
	}
	
	public PyVal bind(String lib, String name) throws InterruptedException, ExecutionException, IOException {
		JSONObject request = new JSONObject();
		request.put("op", "BIND");
		request.put("lib", lib);
		request.put("name", name);
		JSONObject resp = send(request).get();
		return new PyRef(resp.getLong("ref"));
	}
	
	public PyVal bindGlobal(String name) throws InterruptedException, ExecutionException, IOException {
		JSONObject request = new JSONObject();
		request.put("op", "BIND_GLOBAL");
		request.put("name", name);
		JSONObject resp = send(request).get();
		return new PyRef(resp.getLong("ref"));
	}
	
	public void unbind(long ref) throws IOException {
		JSONObject info = new JSONObject();
		info.put("op", "UNBIND");
		info.put("ref", ref);
		send(info);
	}
	
	public int countRef(long ref) throws IOException, InterruptedException, ExecutionException {
		JSONObject info = new JSONObject();
		info.put("op", "REFCOUNT");
		info.put("ref", ref);
		JSONObject resp = send(info).get();
		return PyVal.fromJson(resp).toInt();
	}
	
	public boolean isCallable(long ref) {
		try {
			JSONObject info = new JSONObject();
			info.put("op", "IS_CALLABLE");
			info.put("ref", ref);
			JSONObject resp = send(info).get();
			return PyVal.fromJson(resp).toBool();
		} catch (JSONException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	
	public long mkRef() throws InterruptedException, ExecutionException, IOException {
		JSONObject info = new JSONObject();
		info.put("op", "MKREF");
		JSONObject resp = send(info).get();
		return PyVal.fromJson(resp).toInt();
	}
	public PyVal attrib(long ref, String name, boolean resultAsRef) {
		try {
			JSONObject info = new JSONObject();
			info.put("op", "GET_ATTR");
			info.put("ref", ref);
			info.put("name", name);
			info.put("asRef", resultAsRef);
			JSONObject resp = send(info).get();
			return PyVal.fromJson( resp );
		} catch (JSONException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}

	/**
	 * Returns PyVal of dereferenced value, or ref if it can't be represented in another way
	 * */
	public PyVal get(long ref) throws PyBindException {
		try {
			JSONObject info = new JSONObject();
			info.put("op", "GET");
			info.put("ref", ref);
			return PyVal.fromJson( send(info).get() );
		} catch (InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	public String refType(long ref) throws PyBindException {
		try {
			JSONObject info = new JSONObject();
			info.put("op", "TYPE");
			info.put("ref", ref);
			JSONObject resp = send(info).get();
			return resp.getString("type");
		} catch (JSONException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
	}
	
	private class  FutureResponse<T> implements Future<T> {
		private T result;
		private PyException ex;
		
		public void setResult(T result) {
			synchronized (this) {
				this.result = result;
				this.notifyAll();
			}
		}
		public void setException( PyException ex ) {
			synchronized (this) {
				this.ex = ex;
				this.notifyAll();
			}
		}
		
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return result!=null;
		}

		@Override
		public T get() throws InterruptedException, PyException {
			synchronized (this) {
				while(result==null && ex==null)
					this.wait();
			}
			if(ex!=null)
				try{throw ex;}catch (PyException e) {throw new PyException(e);} //add in own stack trace
			return result;
		}

		@Override
		public T get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			synchronized (this) {
				long millis = unit.toMillis(timeout);
				long then = System.currentTimeMillis();
				while(result==null && ex==null) {
					if(millis < 0) {
						throw new TimeoutException();
					}
					this.wait(millis);
					long now = System.currentTimeMillis();
					millis -= now - then;
					then = now;
				}
			}
			if(ex!=null)
				try{throw ex;}catch (PyException e) {throw new PyException(e);} //add in own stack trace
			return result;
		}
		
	}
	
	public enum Actions {
		GET, //plain value
		SET, //ref
		CALL,
		EXEC,
		EVAL,
		ERR;
	}
	public enum ResultMode {
		COPY,
		REF,
		IGNORE;
	}
	public PyRef shareJavaObject(long ref, String expectedType, String clas) {
		try {
			JSONObject info = new JSONObject();
			info.put("op", "JBIND");
			info.put("expect", expectedType);
			info.put("class", clas);
			info.put("ref", ref);
			send(info);
			return new PyRef( ref );
//			return PyVal.fromJson( resp );
		} catch (JSONException | IOException e) {
			throw new PyBindException(e);
		}
	}

	

	
}
