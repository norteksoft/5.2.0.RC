package com.norteksoft.wf.engine.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class for Servlet: UploadServlet
 * 
 */
public class UploadServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/xml;charset=utf-8");
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter writer = response.getWriter();

		try {
			boolean isMultipart = FileUpload.isMultipartContent(request);
			String msg = null;
			byte[] img = null;
			InputStream uploadStream = null;
			// only for publish new report
			if (isMultipart) {
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setHeaderEncoding("utf-8");
				// Parse the request
				List items = null;
				try {
					items = upload.parseRequest(request);
				} catch (Exception e) {
					msg = "Can not parse the request to upload file.";
				}
				// Process the uploaded items
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					item.getString("UTF-8");
					if (item.isFormField()) {
						continue;
					} else {
						uploadStream = item.getInputStream();
						InputStreamReader isr = new InputStreamReader(
								uploadStream, Charset.forName("UTF-8"));
						BufferedReader br = new BufferedReader(isr);
						StringBuffer sb = new StringBuffer();
						String temp = br.readLine();
						while (temp != null) {
							sb.append(temp + " ");
							temp = br.readLine();
						}
						writer.print(sb.toString());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}
}
