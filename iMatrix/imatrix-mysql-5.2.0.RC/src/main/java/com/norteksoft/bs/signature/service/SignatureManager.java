package com.norteksoft.bs.signature.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.signature.dao.SignatureDao;
import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PropUtils;

@Service
@Transactional
public class SignatureManager {

	private Signature signature;
	
	@Autowired
	private SignatureDao signatureDao;
	
	public Signature getSignatureById(Long id) {
		return signatureDao.getSignatureById(id);
	}

	@Transactional(readOnly = false)
	public void save(Signature signature) {
		signatureDao.save(signature);
	}

	/**
	 * 删除签章图片
	 * @param signaturePicture
	 * @return
	 */
	public void deleteSignatureInfoPicture(Long id){
		signatureDao.delete(id);
	}
	
	/**
	 * @throws Exception 
	 * @throws FileNotFoundException
	 * 保存上传
	 */
	@Transactional(readOnly = false)
	public void saveUploadPicture(Long signatureId) throws Exception {
		String time = UUID.randomUUID().toString();
		HttpServletRequest request=ServletActionContext.getRequest();
		//把request强转，因为struts从新封装了request(Filedata是它的参数不能改变)
		MultiPartRequestWrapper wrapper=(MultiPartRequestWrapper)request;
		File filePath=wrapper.getFiles("Filedata")[0];
		String fileName=request.getParameter("Filename");
		String fileType = fileName.substring(fileName.lastIndexOf("."),fileName.length());
		String uploadPath = PropUtils.getProp("application.properties","upload.file.path");
		if(StringUtils.isEmpty(uploadPath)){
			uploadPath = PropUtils.getProp("applicationContent.properties","upload.file.path");
		}
		String path=uploadPath+"/"+"Signature";
		File file =new File(path);
		if(!file.exists())file.mkdirs();
		FileUtils.copyFile(filePath, new File(path+"/"+time+fileType));
		signature = signatureDao.getSignatureById(signatureId);
		String oldPath=signature.getPictureSrc();
		if(oldPath!=null){
			File old =new File(path+oldPath);
			if(old.exists())old.delete();
		}
		signature.setPictureSrc("/"+time+fileType);
		save(signature);
	}
	
	/**
	 * 获得所有签章信息
	 * @param meetingInformation
	 */
	public void getAllSignaturePicture(Page<Signature> page){
		signatureDao.getAllSignaturePicture(page);
	}
	
	/**
	 * 验证创建用户是否重复
	 */
	public Boolean isExistUser(Long userId, Long signatureId){
		List<Signature> signatures = signatureDao.getAllSignatureNoCurrentId(signatureId);
		for(Signature signature : signatures){
		   if(userId.equals(signature.getUserId())){
			   return true;
			 }
		}
		return false;
	}
	
	/**
	 *删除签章
	 * @param signatureDeleteIds
	 */
	@Transactional(readOnly=false)
	public void deleteSignatureByIds(String signatureDeleteIds){
		String[] id = signatureDeleteIds.split(",");
		List<String> signatureIds = new ArrayList<String>();
		for(int i=0; i<=id.length-1; i++){
			signatureIds.add(id[i]);
		}
		for(String sig:signatureIds){
			signatureDao.delete(Long.valueOf(sig));
		}
	}

	public Long getSignIdByUserName(String userName) {
		if(signatureDao.getSignByUserName(userName)!=null){
			return signatureDao.getSignByUserName(userName).getId();
		}
		return null;
	}
}
