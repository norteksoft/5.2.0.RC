package com.norteksoft.mms.authority.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.norteksoft.mms.authority.dao.PermissionItemDao;
import com.norteksoft.mms.authority.entity.PermissionItem;
import com.norteksoft.product.orm.Page;

@Service
public class PermissionItemManager {
	@Autowired
	private PermissionItemDao permissionItemDao;
	public void getPermissionItems(Page<PermissionItem> page,Long permissionId){
		permissionItemDao.getPermissionItems(page, permissionId);
	}
	
	public void deletePermissionItem(Long id){
		permissionItemDao.delete(id);
	}
}
