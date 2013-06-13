package com.norteksoft.bs.signature.web;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.bs.signature.service.SignatureManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/signature")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "signature", type = "redirectAction") })
public class SignatureAction extends CrudActionSupport<Signature> {

	private static final long serialVersionUID = 1L;
	private Long signatureId;
	private Signature signature;
	private String pictureExist;
	private Long userId;
	private String signatureDeleteIds;
	private String signatureIsUpd;
	private Page<Signature> signaturePage = new Page<Signature>(0,true);

	@Autowired
	private SignatureManager signatureManager;

	@Override
	@Action("signature")
	public String list() throws Exception {
		if(signaturePage.getPageSize()>1){
			signatureManager.getAllSignaturePicture(signaturePage);
			this.renderText(PageUtils.pageToJson(signaturePage));
			return null;
		}
		return "signature";
	}

	/*
	 * 输入页面
	 */
	@Override
	@Action("signature-input")
	public String input() throws Exception {
		if(signatureId!=null){
			signature = signatureManager.getSignatureById(signatureId);
			if(signature.getPictureSrc()!=null&&!signature.getPictureSrc().isEmpty()){
				pictureExist=signature.getPictureSrc();
			}
		}
		return "signature-input";
	}

	/*
	 * 保存
	 */
	@Override
	@Action("signature-save")
	public String save() throws Exception {
		signatureManager.save(signature);
		signatureId = signature.getId();
		if(signature.getPictureSrc()!=null&&!signature.getPictureSrc().isEmpty()){
			pictureExist=signature.getPictureSrc();
		}
		addActionMessage("<font class=\"onSuccess\"><nobr>保存成功！</nobr></font>");
		return input();
	}

	/**
	 * 验证创建用户是否重复
	 */
	@Action("signature-validate")
	public String signaturevalidate() throws Exception {
		if(userId==null){
			this.renderText("singnature_save_validate_ok");
		}else{
			this.renderText(signatureManager.isExistUser(userId,signatureId).toString());
		}
		return null;
	}
	
	
	/**
	 * 删除签章
	 * @return
	 * @throws Exception
	 */
	@Override
	@Action("signature-picture-delete")
	public String delete() throws Exception {
		signatureManager.deleteSignatureByIds(signatureDeleteIds);
		return list();
	}

	@Override
	protected void prepareModel() throws Exception {
		if (signatureId == null) {
			signature = new Signature();
		} else {
			signature = signatureManager.getSignatureById(signatureId);
		}
	}

	public Signature getModel() {
		return signature;
	}

	/*
	 * 选人树
	 */
	@Action("signature-user-tree")
	public String getSignatureUserTree() throws Exception {
		return SUCCESS;
	}

	/*
	 * 上传图片
	 */
	@Action("signature-picture-upload")
	public String upload() throws Exception {
		signatureManager.saveUploadPicture(signatureId);
		return null;
	}

	/*
	 * 上传图片显示
	 */
	@Action("signature-picture-show")
	public String showSignaturePicture() throws Exception {
		if(signatureId!=null){
			signature = signatureManager.getSignatureById(signatureId);
			String uploadPath = PropUtils.getProp("application.properties","upload.file.path");
			if(StringUtils.isEmpty(uploadPath)){
				uploadPath = PropUtils.getProp("applicationContent.properties","upload.file.path");
			}
			String path=uploadPath+"/"+"Signature"+signature.getPictureSrc();
			File file=new File(path);
			if(file.exists())PropUtils.showPic(file);
		}
		return null;
	}

	  /**
	 * 所有文件上传之后调用显示图片
	 * @return
	 * @throws Exception
	 */
	@Action("signature-picture-upload-after-show")
	public String showPicture() throws Exception{
		return input();
	}

	public Long getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(Long signatureId) {
		this.signatureId = signatureId;
	}

	public String getPictureExist() {
		return pictureExist;
	}

	public void setPictureExist(String pictureExist) {
		this.pictureExist = pictureExist;
	}

	public Page<Signature> getSignaturePage() {
		return signaturePage;
	}

	public void setSignaturePage(Page<Signature> signaturePage) {
		this.signaturePage = signaturePage;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSignatureDeleteIds() {
		return signatureDeleteIds;
	}

	public void setSignatureDeleteIds(String signatureDeleteIds) {
		this.signatureDeleteIds = signatureDeleteIds;
	}

	public String getSignatureIsUpd() {
		return signatureIsUpd;
	}

	public void setSignatureIsUpd(String signatureIsUpd) {
		this.signatureIsUpd = signatureIsUpd;
	}

}
