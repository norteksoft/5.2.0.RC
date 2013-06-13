package com.norteksoft.bs.signature.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.bs.signature.dao.SignatureDao;
import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.DataSheetConfig;
import com.norteksoft.mms.base.data.DataTransfer;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;

@Service
@Transactional
public class ImportSignatureManager implements DataTransfer {
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private SignatureDao signatureDao;
	@Autowired
	private DataHandle dataHandle;
	@Autowired
    private CompanyManager companyManager;
	

	public void backup(String systemIds, Long companyId,FileConfigModel fileConfig) {
		try {
			String path=fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/";
			File file = new File(path+fileConfig.getFilename()+".xls");
			OutputStream out=null;
			out=new FileOutputStream(file);
			exportSignature(out,path);
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
	}
	
	private void exportSignature(OutputStream fileOut,String path){
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_SIGNATURE']");
		wb = new HSSFWorkbook();
		//导入定义excel信息
    	HSSFSheet sheet=wb.createSheet("BS_SIGNATURE");
        HSSFRow row = sheet.createRow(0);
        
        dataHandle.getFileHead(wb,row,confs);
        List<Signature> signatures=signatureDao.getAllSignatureNoCurrentId(null);
		for(Signature signature:signatures){
			importSignature(signature,sheet,confs,path);
		}
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	private void importSignature(Signature signature,HSSFSheet sheet,List<DataSheetConfig> confs,String path) {
		if(signature!=null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
        		DataSheetConfig conf=confs.get(i);
        		if(!conf.isIgnore()){
        			setFieldValue(conf,i,rowi,signature,path);
        		}
        	}
		}
	}
	
	private void setFieldValue(DataSheetConfig conf,int i,HSSFRow rowi,Signature signature,String path){
		HSSFCell cell = rowi.createCell(i);
		String fieldName=conf.getFieldName();
		String value="";
		try {
			if("userLoginName".equals(fieldName)){
				Long userId=signature.getUserId();
				com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
				value=user!=null?user.getLoginName():"";
			}else if("pictureSrc".equals(fieldName)){
				String pictureSrc=signature.getPictureSrc();
				String time = UUID.randomUUID().toString();
				if(StringUtils.isNotEmpty(pictureSrc)){
					String uploadPath = PropUtils.getProp("application.properties","upload.file.path");
					if(StringUtils.isEmpty(uploadPath)){
						uploadPath = PropUtils.getProp("applicationContent.properties","upload.file.path");
					}
					String filePath = uploadPath+"/"+"Signature"+pictureSrc;
					File file =new File(filePath);
					String fileType=pictureSrc.substring(pictureSrc.lastIndexOf("."),pictureSrc.length());
					if(file.exists()){
						FileUtils.copyFile(file, new File(path+time+fileType));
						value="/"+time+fileType;
					}
				}
			}else{
				value = BeanUtils.getProperty(signature, fieldName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cell.setCellValue(value);
	}

	public void restore(Long companyId, FileConfigModel fileConfig,String... imatrixInfo) {
		File file =null;
		if(StringUtils.isNotEmpty(fileConfig.getFilename())){
			String path=fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/";
			file=new File(path+fileConfig.getFilename()+".xls");
			if(file.exists()){
				importSignature(file, companyId,path);
			}
		}
	}
	private void importSignature(File file,Long companyId,String path){
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='BS_SIGNATURE']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		//创建时间,创建人姓名,创建人id,公司id
		FileInputStream fis=null;
 		InputStreamReader fr=null;
 		BufferedReader br=null;
 		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("BS_SIGNATURE");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importSignatureData(sheet,confs,map,path);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importSignatureData(sheet,confs,map,path);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importSignatureData(sheet,confs,map,path);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(br!=null)br.close();
	 			if(fr!=null)fr.close();
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}
	private void importSignatureData(HSSFSheet sheet,List<DataSheetConfig> confs,Map<String,Integer> map,String path){
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addSignature(confs,row,map,path);
			}
		}
	}
	private void addSignature(List<DataSheetConfig> confs,HSSFRow row,Map<String,Integer> map ,String path){
		try {
			Integer index=map.get("userLoginName");
			String userLoginName=row.getCell(index).getStringCellValue();//用户登录名名
			com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserByLoginName(userLoginName);
			if(user!=null){
				Signature signature=signatureDao.getSignByUserId(user.getId());
				if(signature==null){
					signature=new Signature();
				}
				signature.setUserId(user.getId());
				for(int j=0;j<confs.size();j++){
					DataSheetConfig conf=confs.get(j);
					if(!conf.isIgnore()){
						String fieldName=conf.getFieldName();
						String value=null;
						if(!"userLoginName".equals(fieldName)){
							if(row.getCell(j)!=null){
								value=row.getCell(j).getStringCellValue();
							}
							if("pictureSrc".equals(fieldName)){
								//重设value
								value="";
								
								String pictureSrc=row.getCell(j).getStringCellValue();
								String time = UUID.randomUUID().toString();
								if(StringUtils.isNotEmpty(pictureSrc)){
									String fileType=pictureSrc.substring(pictureSrc.lastIndexOf("."),pictureSrc.length());
									String uploadPath = PropUtils.getProp("application.properties","upload.file.path");
									if(StringUtils.isEmpty(uploadPath)){
										uploadPath = PropUtils.getProp("applicationContent.properties","upload.file.path");
									}
									String filePath=uploadPath+"/"+"Signature/"+time+fileType;
									File file =new File(path+pictureSrc);
									FileUtils.copyFile(file, new File(filePath));
									if(!file.exists())file.mkdirs();
									value="/"+time+fileType;
								}
							}
							if(StringUtils.isNotEmpty(value)){//导入数据
								dataHandle.setValue(signature,fieldName,conf.getDataType(),value,conf.getEnumName());
							}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
								dataHandle.setValue(signature,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
							}
						}
					}
				}
				signature.setCreatedTime(new Date());
				signature.setCreator(ContextUtils.getLoginName());
				signature.setCreatorName(ContextUtils.getUserName());
				signature.setCompanyId(ContextUtils.getCompanyId());
				signatureDao.save(signature);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
