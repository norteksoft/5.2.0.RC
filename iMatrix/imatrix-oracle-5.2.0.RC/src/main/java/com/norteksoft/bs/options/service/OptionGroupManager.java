package com.norteksoft.bs.options.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.OptionDao;
import com.norteksoft.bs.options.dao.OptionGroupDao;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class OptionGroupManager {

	private OptionDao optionDao;
	
	private OptionGroupDao optionGroupDao;
	
	@Autowired
	public void setOptionDao(OptionDao optionDao) {
		this.optionDao = optionDao;
	}
	
	@Autowired
	public void setOptionGroupDao(OptionGroupDao optionGroupDao) {
		this.optionGroupDao = optionGroupDao;
	}

	
	public void getAllOptionGroupForPage(Page<OptionGroup> groups,Long systemId) {
		optionGroupDao.getAllOptionGroupForPage(groups,systemId);
	}
	
	public OptionGroup getOptionGroup(Long id){
		return optionGroupDao.get(id);
	}
	
	public boolean checkOptionGroupCanCreate(String name, Long groupId){
		OptionGroup group = optionGroupDao.CheckOptionGroupName(name);
		if(group == null){
			return true;
		}else{
			if(group.getId().equals(groupId)){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public boolean checkOptionGroupForOnly(String groupNo){
		OptionGroup group = optionGroupDao.CheckOptionGroupNo(groupNo);
		if(group == null){
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional(readOnly=false)
	public List<Option> saveOptionGroup(OptionGroup optionGroup, List<Option> options){
		List<Option> optionList = new ArrayList<Option>();
		optionGroupDao.save(optionGroup);
		for (Option option : options) {
			if(option==null){continue;}
			option.setOptionGroup(optionGroup);
			option.setCompanyId(ContextUtils.getCompanyId());
			optionDao.save(option);
			optionList.add(option);
		}
		
		return optionList;
	}
	
	@Transactional(readOnly=false)
	public void deleteOptionGroup(Long id){
		optionGroupDao.delete(id);
	}
	private List<Long> getDeleteId(String ids){
		String[] strIds = ids.split(",");
		List<Long> idList = new ArrayList<Long>();
		for (String string : strIds) {
			idList.add(Long.valueOf(string.trim()));
		}
		return idList;
	}
	public String deleteOptionGroups(String ids){
		StringBuilder sb=new StringBuilder();
		int successNum=0;
		List<Long> deleteIds = getDeleteId(ids);
		for (Long id : deleteIds) {
			OptionGroup group=getOptionGroup(id);
			if("internation".equals(group.getCode())){
				sb.append("底层应用internation选项组，无法删除;");
			}else{
				deleteOptionGroup(id);
				successNum++;
			}
		}
		if(successNum!=0)sb.append("删除成功").append(successNum).append("个");
		return sb.toString();
	}
	
	@Transactional(readOnly=false)
	public void deleteOption(Long optionId) {
		optionDao.delete(optionId);
	}
	
	
	
	
	
	/**
	 * 查询所有的选项组
	 */
	public List<OptionGroup> getOptionGroups() {
		return optionGroupDao.getOptionGroups();
	}

	/**
	 * 根据选项组查询选项
	 */
	public List<Option> getOptionsByGroup(Long optionGroupId) {
		return optionDao.getOptionsByGroup(optionGroupId);
	}
	
    /**
     * 根据选项组编号查询选项组
     * @param code
     * @return
     */
    public OptionGroup getOptionGroupByCode(String code){
    	return optionGroupDao.getOptionGroupByCode(code);
    }

    /**
     * 根据选项组名称查询选项组
     * @param code
     * @return
     */
    public OptionGroup getOptionGroupByName(String name){
    	return optionGroupDao.getOptionGroupByName(name);
    }
    
    /**
     * 根据选项组编号查询选项
     * @param code
     * @return
     */
    public List<Option> getOptionsByGroupCode(String code){
    	return optionDao.getOptionsByGroupCode(code);
    }
    
    public Option getDefaultOptionByOptionGroupCode(Long optionGroupId){
    	List<Option> options = getOptionsByGroup(optionGroupId);
    	return getDefaultOption(options);
    }
    
    public Option getDefaultOptionByOptionGroupCode(String groupCode){
    	List<Option> options = getOptionsByGroupCode(groupCode);
    	return getDefaultOption(options);
    }
    
    public Option getDefaultOption(List<Option> options){
    	Option defaultOption = null;
    	for(Option opt : options){
    		if(opt.getSelected()){
    			defaultOption = opt;
    			break;
    		}
    	}
    	if(!options.isEmpty() && defaultOption == null) defaultOption = options.get(0);
    	return defaultOption;
    }
    
    /**
     * 根据选项组名称查询选项
     * @param code
     * @return
     */
    public List<Option> getOptionsByGroupName(String name){
    	return optionDao.getOptionsByGroupName(name);
    }
    public List<OptionGroup> getOptionGroups(Long companyId,String systemIds){
    	return optionGroupDao.getOptionGroups(companyId,systemIds);
    }
    public List<Option> getOptionsByGroup(Long optionGroupId,Long companyId){
    	return optionDao.getOptionsByGroup(optionGroupId, companyId);
    }
    @Transactional(readOnly=false)
    public void saveOptionGroup(OptionGroup group){
    	optionGroupDao.save(group);
    }
    public Option getOptionByInfo(String value,String name,String groupCode){
    	return optionDao.getOptionByInfo(value, name, groupCode);
    }
    @Transactional(readOnly=false)
    public void saveOption(Option option){
    	optionDao.save(option);
    }
    
    public Option getOptionById(Long optionId){
    	return optionDao.get(optionId);
    }
}
