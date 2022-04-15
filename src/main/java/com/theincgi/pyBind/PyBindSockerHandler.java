package com.theincgi.pyBind;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.theincgi.pyBind.pyVals.PyRef;
import com.theincgi.pyBind.pyVals.PyVal;

public class PyBindSockerHandler {
	private Socket socket;
	private long msgID = 0;
	HashMap<Long, FutureResponse<String>> responses = new HashMap<>();
	
	public PyBindSockerHandler(Socket socket) {
		this.socket = socket;
	}
	
	private synchronized Future<String> send(JSONObject json) throws IOException {
		long id = msgID++;
		json.put("id", id);
		var future = new FutureResponse<String>();
		responses.put(id, future);
		socket.getOutputStream().write(json.toString().getBytes());
		socket.getOutputStream().flush();
		return future;
	}
	
	public JSONObject send(Actions action, ResultMode mode,  JSONObject info) {
		info.put("op", action.name());
		info.put("mode", mode.name());
		return new JSONObject(send(info).get());
	}
	
	public PyVal bind(String lib, String name) throws InterruptedException, ExecutionException, IOException {
		JSONObject request = new JSONObject();
		request.put("op", "BIND");
		request.put("lib", lib);
		request.put("name", name);
		String resp = send(request).get();
		JSONObject obj = new JSONObject(resp);
		return new PyRef(obj.getString("uuid")).eval();
	}
	
	public PyVal bindGlobal(String name) throws InterruptedException, ExecutionException, IOException {
		JSONObject request = new JSONObject();
		request.put("op", "BIND_GLOBAL");
		request.put("name", name);
		String resp = send(request).get();
		JSONObject obj = new JSONObject(resp);
		return new PyRef(obj.getString("uuid")).eval();
	}
	
	public void unbind(String uuid) {
		
	}
	
	private class  FutureResponse<T> implements Future<T> {
		private T result;
		
		public void setResult(T result) {
			synchronized (this) {
				this.result = result;
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
		public T get() throws InterruptedException, ExecutionException {
			synchronized (this) {
				while(result==null)
					this.wait();
			}
			return result;
		}

		@Override
		public T get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			synchronized (this) {
				long millis = unit.toMillis(timeout);
				long then = System.currentTimeMillis();
				while(result==null) {
					if(millis < 0) {
						throw new TimeoutException();
					}
					this.wait(millis);
					long now = System.currentTimeMillis();
					millis -= now - then;
					then = now;
				}
			}
			return result;
		}
		
	}
	
	public enum Actions {
		GET, //plain value
		SET, //ref
		CALL,
		EXEC;
	}
	public enum ResultMode {
		COPY,
		REF,
		IGNORE;
	}
}
