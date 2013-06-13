package com.norteksoft.wf.engine.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.dao.DataDictionaryDao;
import com.norteksoft.wf.engine.dao.DataDictionaryProcessDao;
import com.norteksoft.wf.engine.dao.DataDictionaryTypeDao;
import com.norteksoft.wf.engine.dao.DataDictionaryUserDao;
import com.norteksoft.wf.engine.entity.DataDictionary;
import com.norteksoft.wf.engine.entity.DataDictionaryProcess;
import com.norteksoft.wf.engine.entity.DataDictionaryType;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;

@Service
@Transactional
public class DataDictionaryTypeManager {
	private DataDictionaryDao dataDictionaryDao;
	private DataDictionaryTypeDao dataDictionaryTypeDao;
	private DataDictionaryUserDao dataDictionaryUserDao;
	private DataDictionaryProcessDao dataDictionaryProcessDao;
	private Log log = LogFactory.getLog(DataDictionaryTypeManager.class);
	@Autowired
	public void setDataDictionaryDao(DataDictionaryDao dataDictionaryDao) {
		this.dataDictionaryDao = dataDictionaryDao;
	}

	@Autowired
	public void setDataDictionaryTypeDao(DataDictionaryTypeDao dataDictionaryTypeDao) {
		this.dataDictionaryTypeDao = dataDictionaryTypeDao;
	}
	@Autowired
	public void setDataDictionaryUserDao(DataDictionaryUserDao dataDictionaryUserDao) {
		this.dataDictionaryUserDao = dataDictionaryUserDao;
	}
	@Autowired
	public void setDataDictionaryProcessDao(
			DataDictionaryProcessDao dataDictionaryProcessDao) {
		this.dataDictionaryProcessDao = dataDictionaryProcessDao;
	}

	public Long getSystemId(){
    	return ContextUtils.getSystemId();
    }
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}

	public void getDataDictTypesPage(Page<DataDictionaryType> dictPage){
		dataDictionaryTypeDao.getDataDictTypesPage(dictPage,getCompanyId());
	}
	
	public List<DataDictionaryType> getAllDictTypes(){
		return dataDictionaryTypeDao.getAllDictTypes(getCompanyId(),getSystemId());
	}
	
	public List<DataDictionaryType> getAllDictTypes(Long typeId){
		return dataDictionaryTypeDao.getAllDictTypes(getCompanyId(),typeId);
	}
	
	@Transactional(readOnly=false)
	public void saveDictType(DataDictionaryType dataDictionaryType){
		log.debug("***saveDictType方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append(dataDictionaryType)
		.append("]").toString());
		dataDictionaryType.setSystemId(ContextUtils.getSystemId("wf"));
		dataDictionaryTypeDao.save(dataDictionaryType);
		List<DataDictionary> list = dataDictionaryDao.getDataDictsByTypeId(dataDictionaryType.getId(),dataDictionaryType.getCompanyId());
		for(DataDictionary dd:list){
			dd.setTypeName(dataDictionaryType.getName());
			dataDictionaryDao.save(dd);
		}
		log.debug("***saveDictType方法开始");
	}
	
	public DataDictionaryType getDictTypeById(Long id){
		log.debug("***getDictTypeById方法");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("dictTypeId:").append(id)
		.append("]").toString());
		return dataDictionaryTypeDao.getDictTypeById(id);
	}
	
	@Transactional(readOnly=false)
	public void deleteDictType(String ids){
		log.debug("***deleteDictType方法");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append(ids)
		.append("]").toString());
		String[] typeIds=ids.split(",");
	    for(String typeId:typeIds){
	    	DataDictionaryType dataDictionaryType=dataDictionaryTypeDao.get(Long.parseLong(typeId));
	    	List<DataDictionary> list = dataDictionaryDao.getDataDictsByTypeId(dataDictionaryType.getId(),dataDictionaryType.getCompanyId());
			for(DataDictionary dd:list){
				List<DataDictionaryUser> ddus = dataDictionaryUserDao.getDDUs(dd.getId(),getCompanyId());
				for(DataDictionaryUser ddu:ddus){
					dataDictionaryUserDao.delete(ddu);
				}
				List<DataDictionaryProcess> ddps = dataDictionaryProcessDao.getAllDictProcessesByDictId(dd.getId());
				for(DataDictionaryProcess ddp : ddps){
					dataDictionaryProcessDao.delete(ddp);
				}
				dataDictionaryDao.delete(dd.getId());
			}
			dataDictionaryTypeDao.delete(dataDictionaryType);
	    }
		log.debug("***deleteDictType方法开始");
	}
	
	//类型名称是否存在
	public Boolean isTypeNoExist(String no){
		DataDictionaryType dictType=dataDictionaryTypeDao.getDictTypeByNo(no,getCompanyId());
		if(dictType!=null){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 根据类型id集合获得类型编码的集合
	 * @param typeIds
	 * @param companyId
	 * @return
	 */
	public List<String> getDictTypeCodesByIds(String typeIds,Long companyId){
		return dataDictionaryTypeDao.getDictTypeCodesByIds(typeIds, companyId);
	}
	
	public List<DataDictionaryType> getAllDictTypesByCompany(Long companyId){
		return dataDictionaryTypeDao.getAllDictTypes(companyId);
	}
	
	public DataDictionaryType getDictTypeByNo(String code){
		DataDictionaryType dataDictionaryType = dataDictionaryTypeDao.getDictTypeByNo(code,getCompanyId());
		return dataDictionaryType;
	}
	public List<String> getDictTypeIdsByCodes(String typeNos){
		return dataDictionaryTypeDao.getDictTypeIdsByCodes(typeNos);
	}
}
