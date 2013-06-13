package com.norteksoft.mms.module.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.module.dao.OperationDao;
import com.norteksoft.mms.module.entity.Operation;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional(readOnly=true)
public class OperationManager {
	@Autowired
	private OperationDao operationDao;
	public Operation getOperation(Long id){
		return operationDao.get(id);
	}
	public void getOperations(Page<Operation> pages,Long systemId){
		operationDao.getOperations(pages,systemId);
	}
	public Operation getOperationByCode(String code,Long systemId){
		return operationDao.getOperationByCode(code,systemId);
	}
	public void getOperationChildren(Page<Operation> page,Long operationId){
		operationDao.getOperationChildren(page, operationId);
	}
	@Transactional(readOnly=false)
	public void deleteOperation(Long id){
		operationDao.delete(id);
	}
	@Transactional(readOnly=false)
	public void deleteOperations(String ids){
		String[] idList=ids.split(",");
		for(String id:idList){
			if(StringUtils.isNotEmpty(id)){
				operationDao.delete(Long.parseLong(id));
			}
		}
	}
	@Transactional(readOnly=false)
	public void save(Operation operation){
		operationDao.save(operation);
	}
	@Transactional(readOnly=false)
	public void saveOperation(Operation operation){
		operationDao.save(operation);
		List<Object> list=JsonParser.getFormTableDatas(Operation.class);
		List<Operation> children=new ArrayList<Operation>();
		for(Object obj:list){
			Operation inter=(Operation)obj;
			inter.setSystemId(operation.getSystemId());
			inter.setParent(operation);
			operationDao.save(inter);
			children.add(inter);
		}
		operation.setChildren(children);
	}
	/**
	 * 验证编号是否存在
	 * @param code
	 * @param id
	 * @return 存在返回true,反之
	 */
	public boolean isOperationExist(String code,Long id,Long systemId){
		Operation operation=getOperationByCode(code,systemId);
		if(operation==null){
			return false;
		}else{
			if(id==null)return true;
			if(operation.getId().equals(id)){
				return false;
			}else{
				return true;
			}
		}
	}
	/**
	 * 查询所有通用类型
	 * @return
	 */
	public List<Operation> getOperations(String systemIds,Long companyId){
		return operationDao.getOperations(systemIds,companyId);
	}
	public List<Operation> getAllParentOperations(Long systemId){
		return operationDao.getAllParentOperations(systemId);
	}
	public List<Operation> getOperationChildrenList(Long operationId){
		return operationDao.getOperationChildrenList(operationId);
	}
	@Transactional(readOnly=false)
	public void dealwithOperation(Long systemId){
		List<Operation> operations = getAllParentOperations(systemId);
		List<Long> deleteIds = new ArrayList<Long>();
		for(Operation operation:operations){
			List<Operation> children = getOperationChildrenList(operation.getId());
			if(children.size()<=0)deleteIds.add(operation.getId());
		}
		for(Long id:deleteIds){
			deleteOperation(id);
		}
	}
	
}
