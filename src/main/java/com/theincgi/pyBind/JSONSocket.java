package com.theincgi.pyBind;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class JSONSocket implements Closeable {
	
	DataOutputStream out;
	DataInputStream in;
	BufferedInputStream inBuf;
	
	public JSONSocket(Socket socket) throws IOException {
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream( inBuf = new BufferedInputStream( socket.getInputStream() ));
	}
	
	public void write(JSONObject obj) throws IOException {
		String str = obj.toString();
		out.writeInt(str.length());
		out.write(str.getBytes());
		out.flush();
	}
	
	public boolean hasJSON() throws IOException {
		if(in.available() < Integer.BYTES) return false;
		inBuf.mark(4);
		int msgLength = in.readInt();
		if(msgLength <= in.available()) {
			inBuf.reset();
			return true;
		}else{
			inBuf.reset();
			return false;
		}
	}
	
	public JSONObject readJSON() throws IOException {
		inBuf.mark(4);
		int msgLength = in.readInt();
		if(msgLength <= in.available()) 
			return new JSONObject( new String( in.readNBytes(msgLength)));
		
		inBuf.reset();
		return null;
	}
	
	@Override
	public void close() throws IOException {
		out.close();
		in.close();
	}
}
