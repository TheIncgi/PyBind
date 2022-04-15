package com.theincgi.pyBind;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Common {
	public static String expected(String type, String got) {
		return "expected %s, got %s".formatted(type, got);
	}

	public static File findOnPath( String prgm ){
		String[] dirs = System.getenv("PATH").split(";");
		for( String dir: dirs ){
			File tmp = Paths.get( dir, prgm ).toFile();
			if( tmp.canExecute() ) 
				return tmp;
		}
		return null;
	}
}
