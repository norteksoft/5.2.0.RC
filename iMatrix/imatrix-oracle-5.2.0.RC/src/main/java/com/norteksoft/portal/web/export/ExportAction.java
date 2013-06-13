package com.norteksoft.portal.web.export;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/export")
@ParentPackage("default")
public class ExportAction extends CRUDActionSupport{
private static final long serialVersionUID = 1L;
	
	private String fileName;
	
	/**
	 * 导出
	 * @return
	 * @throws Exception
	 */
	@Action("export-data")
	public String exportData() throws Exception {
		String path =  PropUtils.getProp("excel.export.file.path");
		int signIndex=fileName.lastIndexOf("_");
		String showName=fileName.substring(0,signIndex);
		String serverName=fileName.substring(signIndex+1,fileName.length());
		serverName=path+serverName;
		File file=new File(serverName);
		byte[] content=FileUtils.readFileToByteArray(file);
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		OutputStream out=null;
		try {
			byte[] byname = showName.getBytes("gbk");
			showName = new String(byname,"8859_1");
			response.addHeader("Content-Disposition", "attachment;filename="+showName);
			out=response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
			file.delete();
		}
		return null;
	}

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}

