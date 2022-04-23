package com.theincgi.pyBind;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class JSONSocket {
	
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
		inBuf.mark(4);
		int msgLength = in.readInt();
		inBuf.reset();
		if(msgLength <= in.available()) {
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
}
