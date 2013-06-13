package com.norteksoft.wf.engine.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.internal.log.Log;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.service.PdfManager;

/**
 * Servlet implementation class WebOffice
 */
public class WebPdf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = Log.getLog(WebPdf.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebPdf() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PdfManager pdfManager = (PdfManager)ContextUtils.getBean("pdfManager");
		 try {
			 DBstep.iMsgServer2000 msgObj = new DBstep.iMsgServer2000();
		      if (request.getMethod().equalsIgnoreCase("POST")) {
		    	  msgObj.MsgVariant(readPackage(request));
		    	  pdfManager.operatePdf(msgObj);
		      }
		      else {
		    	  msgObj.MsgError("请使用Post方法");
		    	  msgObj.MsgTextClear();
		    	  msgObj.MsgFileClear();
		      }
		      sendPackage(msgObj,response);
		    }
		    catch (Exception e) {
		      System.out.println(e.toString());
		    }
	}
	// *************接收流、写回流代码    开始  *******************************
	//取得客户端发来的数据包
	  private byte[] readPackage(HttpServletRequest request) {
	    byte mStream[] = null;
	    int totalRead = 0;
	    int readBytes = 0;
	    int totalBytes = 0;
	    try {
	      totalBytes = request.getContentLength();
	      mStream = new byte[totalBytes];
	      while (totalRead < totalBytes) {
	        request.getInputStream();
	        readBytes = request.getInputStream().read(mStream, totalRead,
	                                                  totalBytes - totalRead);
	        totalRead += readBytes;
	        continue;
	      }
	    }
	    catch (Exception e) {
	    	log.error(e.getMessage(), e);
	    }
	    return (mStream);
	  }
	  
	//发送处理后的数据包
	  private void sendPackage( DBstep.iMsgServer2000 msgObj,HttpServletResponse response) {
	    try {
	      ServletOutputStream OutBinarry = response.getOutputStream();
	      OutBinarry.write(msgObj.MsgVariant());
	      OutBinarry.flush();
	      OutBinarry.close();
	    }
	    catch (IOException e) {
	      System.out.println(e.toString());
	    }
	  }
}
