package com.norteksoft.acs.service.authorization;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;


@SuppressWarnings("deprecation")
@Service
@Transactional
public class  FunctionGroupManager{
	

	private static String BUSINESSSYSTEM_ID = "businessSystem.id";
	private static String DELETED = "deleted";
	private static String FUNCTIONGROUP = "functionGroup";
	private static String CODE = "code";
	private static String NAME ="name";
	private static String FUNCTIONGROUP_ID ="functionGroup.id";
	private static String ID = "id";
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	
	private SimpleHibernateTemplate<Function, Long> functionDao;
	
	
	private static String hql = "from FunctionGroup fg where  fg.deleted=?";
    
	private Long companyId;
	
	public Long getCompanyId() {
		if(companyId == null){
			return ContextUtils.getCompanyId();
		}else 
			return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(sessionFactory,FunctionGroup.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(
				sessionFactory, Function.class);
	}
	
  	/**
  	 * 查询所有资源组信息
  	 */
	@Transactional(readOnly = true)
	public List<FunctionGroup> getAllfunctionGroup(){
		return functionGroupDao.findByCriteria(Restrictions.eq(DELETED, false));
	}
	/**
	 * 获取单条资源组信息
	 */
	@Transactional(readOnly = true)
	public FunctionGroup getFunctionGroup(Long id) {
		return functionGroupDao.get(id);
	}
	
	/**
	 * 分页查询所有资源组信息
	 */
	@Transactional(readOnly = true)
	public Page<FunctionGroup> getAllFunctionGroup(Page<FunctionGroup> page,String functionGroupId,String functionGroupName) {
		
			if(functionGroupId!=null&&!"".equals(functionGroupId)&&functionGroupName!=null&&!"".equals(functionGroupName)){
				return functionGroupDao.findByCriteria(page, 
						                               Restrictions.like("code", "%"+functionGroupId+"%"),
								                       Restrictions.like("name", "%"+functionGroupName+"%"),
								                       Restrictions.eq(DELETED, false));
			}
			if(functionGroupId!=null&&!"".equals(functionGroupId)){
				return functionGroupDao.findByCriteria(page, 
						                               Restrictions.like("code", "%"+functionGroupId+"%"),
						                               Restrictions.eq(DELETED, false));
			}
			if(functionGroupName!=null&&!"".equals(functionGroupName)){
				return functionGroupDao.findByCriteria(page, 
						                               Restrictions.like("name", "%"+functionGroupName+"%"),
													   Restrictions.eq(DELETED, false));
			}
			return functionGroupDao.findByCriteria(page,Restrictions.eq(DELETED, false));
	}
	@Transactional(readOnly = true)
	public Page<Function> getAllFunction(Page<Function> page,String functionCode,String functionName,Long systemid) {
		
			if(functionCode!=null&&!"".equals(functionCode)&&functionName!=null&&!"".equals(functionName)){
				return functionDao.findByCriteria(page,
						                          Restrictions.like(CODE, "%"+ functionCode +"%"),
						                          Restrictions.like(NAME,"%"+functionName +"%"),
						                          Restrictions.isNull(FUNCTIONGROUP),
						                          Restrictions.eq(DELETED, false),
						                          Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
			}
			if(functionCode!=null&&!"".equals(functionCode)){
				return functionDao.findByCriteria(page,
                                                  Restrictions.like(CODE,"%"+ functionCode +"%"),
                                                  Restrictions.isNull(FUNCTIONGROUP),
                                                  Restrictions.eq(DELETED, false),
                                                  Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
			}
			if(functionName!=null&&!"".equals(functionName)){
				return functionDao.findByCriteria(page,
						                          Restrictions.like(NAME,"%"+ functionName +"%"),
						                          Restrictions.isNull(FUNCTIONGROUP),
						                          Restrictions.eq(DELETED, false),
						                          Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
			}
		
		
			return functionDao.findByCriteria(page,
				                             Restrictions.isNull(FUNCTIONGROUP),
				                             Restrictions.eq(DELETED, false),
				                             Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
		
	}
	
	@Transactional(readOnly = true)
	public Page<Function> getAllRomoveFunction(Page<Function> page,String functionCode,String functionName,Long systemid,Long fungId) {
		
			if(functionCode!=null&&!"".equals(functionCode)&&functionName!=null&&!"".equals(functionName)){
				return functionDao.findByCriteria(page,
						                          Restrictions.isNotNull(FUNCTIONGROUP),
						                          Restrictions.like(CODE, "%"+ functionCode +"%"),
						                          Restrictions.like(NAME,"%"+functionName +"%"),
						                          Restrictions.eq(FUNCTIONGROUP_ID, fungId),
						                          Restrictions.eq(DELETED, false),
						                          Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
			}
			if(functionCode!=null&&!"".equals(functionCode)){
				return functionDao.findByCriteria(page,
						                          Restrictions.isNotNull(FUNCTIONGROUP),
                                                  Restrictions.like(CODE,"%"+ functionCode +"%"),
                                                  Restrictions.eq(FUNCTIONGROUP_ID, fungId),
                                                  Restrictions.eq(DELETED, false),
                                                  Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
			}
			if(functionName!=null&&!"".equals(functionName)){
				return functionDao.findByCriteria(page,
						                          Restrictions.isNotNull(FUNCTIONGROUP),
						                          Restrictions.like(NAME,"%"+ functionName +"%"),
						                          Restrictions.eq(FUNCTIONGROUP_ID, fungId),
						                          Restrictions.eq(DELETED, false),
						                          Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
			}
		
		
			return functionDao.findByCriteria(page, 
					                          Restrictions.isNotNull(FUNCTIONGROUP),
				                              Restrictions.eq(FUNCTIONGROUP_ID, fungId),
				                              Restrictions.eq(DELETED, false),
				                              Restrictions.eq(BUSINESSSYSTEM_ID, systemid));
				                          
		
	}
	
	/**
	  * 保存资源组信息
	  */	
	public void saveFunGroup(FunctionGroup functionGroup){
		
		functionGroupDao.save(functionGroup);
	}
	
	/**
	 * 删除资源组信息
	 */
	public void deleteFunGroup(Long id) {
		FunctionGroup functionGroup = functionGroupDao.get(id);
		functionGroup.setDeleted(true);
		functionGroupDao.save(functionGroup);
	}

	/**
	 * 按条件检索资源组
	 */
	@Transactional(readOnly = true)
	public Page<FunctionGroup> getSearchFunctionGroup(Page<FunctionGroup> page,FunctionGroup functionGroup,boolean deleted) {
         StringBuilder functionGroupHql = new StringBuilder(hql);
		
		 if (functionGroup != null) {
			
			String functionGroupId = functionGroup.getCode();
			String functionGroupName = functionGroup.getName();
			if (functionGroupId!=null&&!"".equals(functionGroupId) && functionGroupName!=null&&!"".equals(functionGroupName)) {
				    functionGroupHql.append(" and fg.code like ?");
				    functionGroupHql.append(" and fg.name like ?");
				    return functionGroupDao.find(page, 
				    		                    functionGroupHql.toString(),
						                        false,
						                        "%" + functionGroupId+ "%", "%" + functionGroupName + "%");
			}
			
			if (functionGroupId!=null&&!"".equals(functionGroupId)) {
				    functionGroupHql.append(" and fg.code like ?");
				    return functionGroupDao.find(page, 
				    		                     functionGroupHql.toString(),
						                         false,
						                         "%" + functionGroupId+ "%");
			}
			
			if (functionGroupName!=null&&!"".equals(functionGroupName)) {
				   functionGroupHql.append(" and fg.name like ?");
				   return functionGroupDao.find(page, 
						                        functionGroupHql.toString(),
						                        false,
						                        "%" + functionGroupName + "%");
			}
		}
        
		return functionGroupDao.find(page, hql,false);
	}
	  
	  public void saveFunction(Long paternId,List<Long> functionIds,Integer isAdd){
		  FunctionGroup functionGroup = functionGroupDao.get(paternId);
		  List<Function> list = functionDao.findByCriteria(Restrictions.in(ID, functionIds));
		  List<Function> romoveList = functionDao.findByCriteria(Restrictions.in(ID, functionIds),
				                                                 Restrictions.eq(FUNCTIONGROUP_ID, paternId));
		  StringBuilder functionName = new StringBuilder();
		  if(isAdd==0){
			  for(Function function : list) {
				   function.setFunctionGroup(functionGroup);
				   functionGroup.getFunctions().add(function);
				   functionDao.save(function);
				   functionName.append(function.getName());
				   functionName.append(",");
			   } 
			  functionName.deleteCharAt(functionName.length()-1);
			 
		  }if(isAdd==1){
			  for (Function function : romoveList) {
				  function.setFunctionGroup(null);
				  functionDao.save(function);
				  functionName.append(function.getName());
				  functionName.append(",");
			}
			  functionName.deleteCharAt(functionName.length()-1);
		  }
		
		
	  }
   
	/**
	 * 通过业务系统查询资源组
	 */
	public Page<FunctionGroup> getFuncGroupsBySystem(Page<FunctionGroup> page, Long systemId) {
		return functionGroupDao.findByCriteria(page, Restrictions.eq(BUSINESSSYSTEM_ID, systemId),
				Restrictions.eq(DELETED, false));
	}
	
	public List<FunctionGroup> getFuncGroupsBySystem(Long systemId){
		return functionGroupDao.findList("from FunctionGroup fg where fg.businessSystem.id=? and fg.deleted=?", systemId, false);
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.authorization.FunctionGroup, Long> getFunctionGroupDao() {
		return functionGroupDao;
	}

	public SimpleHibernateTemplate<Function, Long> getFunctionDao() {
		return functionDao;
	}
	
	public List<FunctionGroup> getFunsGroupsBySystem(Long systemId){
		return functionDao.find("from FunctionGroup fg where  fg.businessSystem.id=?", systemId);
	}
	
	public FunctionGroup getFuncGroupByCode(String funcGroupId,Long systemId){
		List<FunctionGroup> funcGroups=functionDao.find("from FunctionGroup fg where  fg.code=? and (fg.businessSystem!=null and fg.businessSystem.id=?)", funcGroupId,systemId);
		if(funcGroups.size()>0)return funcGroups.get(0);
		return null;
	}
	
	
	
}