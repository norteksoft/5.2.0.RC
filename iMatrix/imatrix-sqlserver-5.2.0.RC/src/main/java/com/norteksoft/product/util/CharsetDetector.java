package com.norteksoft.product.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;
/**
 * 这个类就可以检测字节流编码
 * @author Administrator
 *
 */
public class CharsetDetector {
	private boolean found = false;
	private String result;
	private int lang;

	public String[] detectChineseCharset(InputStream in) throws IOException {
		lang = nsPSMDetector.CHINESE;
		String[] prob;
		// Initalize the nsDetector() ;
		nsDetector det = new nsDetector(lang);
		// Set an observer...
		// The Notify() will be called when a matching charset is found.

		det.Init(new nsICharsetDetectionObserver() {

			public void Notify(String charset) {
				found = true;
				result = charset;
			}
		});
		BufferedInputStream imp = new BufferedInputStream(in);
		byte[] buf = new byte[1024];
		int len;
		boolean isAscii = true;
		while ((len = imp.read(buf, 0, buf.length)) != -1) {
			// Check if the stream is only ascii.
			if (isAscii)
				isAscii = det.isAscii(buf, len);
			// DoIt if non-ascii and not done yet.
			if (!isAscii) {
				if (det.DoIt(buf, len, false))
					break;
			}
		}
		imp.close();
		in.close();
		det.DataEnd();
		if (isAscii) {
			found = true;
			prob = new String[] { "ASCII" };
		} else if (found) {
			prob = new String[] { result };
		} else {
			prob = det.getProbableCharsets();
		}
		return prob;
	}

	public String[] detectAllCharset(InputStream in) throws IOException {
		try {
			lang = nsPSMDetector.ALL;
			return detectChineseCharset(in);
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static void main(String[] args) throws IOException {
		 CharsetDetector charDect = new CharsetDetector();
	      FileInputStream input = new FileInputStream(new File("D:/2.txt"));
	       
	        String[] probableSet = charDect.detectChineseCharset(input);
	        for (String charset : probableSet)
	        {
	            System.out.println(charset);
	        }
	}
}
