package com.norteksoft.wf.engine.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.service.OfficeManager;

/**
 * Servlet implementation class WebOffice
 */
public class WebOffice extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebOffice() {
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
		OfficeManager officeManager = (OfficeManager)ContextUtils.getBean("officeManager");
		try {
			DBstep.iMsgServer2000 msgServer = new DBstep.iMsgServer2000();
			if(request.getMethod().equalsIgnoreCase("POST")) {
				msgServer.MsgVariant(readPackage(request));
				officeManager.operateOffice(msgServer);
			}else{
				msgServer.MsgError("请使用Post方法");
				msgServer.MsgTextClear();
				msgServer.MsgFileClear();
			}
			sendPackage(msgServer,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送处理后的数据包
	 * @param response
	 */
	private void sendPackage(DBstep.iMsgServer2000 msgServer,HttpServletResponse response) {
		try {
			
			response.getOutputStream().write(msgServer.MsgVariant());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 取得客户端发来的数据包
	 * @param request
	 * @return
	 */
	private byte[] readPackage(HttpServletRequest request) {
		byte stream[] = null;
		int totalRead = 0;
		int readBytes = 0;
		int totalBytes = 0;
		try {
			totalBytes = request.getContentLength();
			stream = new byte[totalBytes];
			while (totalRead < totalBytes) {
				readBytes = request.getInputStream().read(stream, totalRead,
						totalBytes - totalRead);
				if(readBytes==-1)break;
				totalRead += readBytes;
				continue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (stream);
	}
}
