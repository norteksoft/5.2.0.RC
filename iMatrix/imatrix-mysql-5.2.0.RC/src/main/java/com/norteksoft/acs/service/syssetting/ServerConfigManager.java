package com.norteksoft.acs.service.syssetting;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;

/**
 *系统参数设置接口
 * 
 * @author 陈成虎 2009-3-2上午11:52:40
 */
@Service
@Transactional
public class ServerConfigManager {

	private SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		serverConfigDao = new SimpleHibernateTemplate<ServerConfig, Long>(
				sessionFactory, ServerConfig.class);
	}

	/**
	 * 保存
	 * @param entity
	 */
	public void save(ServerConfig entity) {
		serverConfigDao.save(entity);
	}

	/**
	 * 取实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public ServerConfig getServerConfig(Long id) {
		return serverConfigDao.get(id);
	}

	/**
	 * 取公司的服务器配置方式
	 * @param companyId
	 * @return
	 */
	public ServerConfig getServerConfigByCompanyId(Long companyId){
		return (ServerConfig)serverConfigDao.findUnique("from ServerConfig s where s.companyId=?", companyId) ;
	}
	

}
