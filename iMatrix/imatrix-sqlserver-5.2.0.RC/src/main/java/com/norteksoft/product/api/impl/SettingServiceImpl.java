package com.norteksoft.product.api.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.norteksoft.bs.holiday.service.HolidayManager;
import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.bs.options.service.InternationManager;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.bs.signature.service.SignatureManager;
import com.norteksoft.product.api.SettingService;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
public class SettingServiceImpl implements SettingService{
	
	@Autowired
	private OptionGroupManager optionGroupManager;
	
	@Autowired
	private HolidayManager holidayManager;
	@Autowired
	private InternationManager internationManager;
	@Autowired
	private SignatureManager signatureManager;

	public String getOptionGroupDefaultValue(String optionGroupCode) {
		Assert.notNull(optionGroupCode, "optionGroupCode选项组编号不能为null");
		com.norteksoft.bs.options.entity.Option option = optionGroupManager.getDefaultOptionByOptionGroupCode(optionGroupCode);
		return option.getValue();
	}
	
	public String getOptionGroupDefaultValue(Long optionGroupId) {
		Assert.notNull(optionGroupId, "optionGroupId选项组id不能为null");
		com.norteksoft.bs.options.entity.Option option = optionGroupManager.getDefaultOptionByOptionGroupCode(optionGroupId);
		return option.getValue();
	}
	
	/**
	 * 查询所有的选项组
	 */
	public List<OptionGroup> getOptionGroups() {
		return BeanUtil.turnToModelOptionGroupList(optionGroupManager.getOptionGroups());
	}

	/**
	 * 根据选项组查询选项
	 */
	public List<Option> getOptionsByGroup(Long optionGroupId) {
		return BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroup(optionGroupId));
	}

	public OptionGroup getOptionGroupByCode(String code) {
		return BeanUtil.turnToModelOptionGroup(optionGroupManager.getOptionGroupByCode(code));
	}

	public OptionGroup getOptionGroupByName(String name) {
		return BeanUtil.turnToModelOptionGroup(optionGroupManager.getOptionGroupByName(name));
	}

	public List<Option> getOptionsByGroupCode(String code) {
		return BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroupCode(code));
	}

	public List<Option> getOptionsByGroupName(String name) {
		return BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroupName(name));
	}

	public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate){
		return holidayManager.getHolidaySettingDays(startDate, endDate);
	}
	public String getInternationOptionValue(String code) {
		HttpServletRequest requrest=Struts2Utils.getRequest();
		String language = "zh";
		 if(requrest==null){//定时等没有Struts环境中时，直接去数据库中取
			 String defaultLanguage =  getOptionGroupDefaultValue("internation");
			 if(StringUtils.isNotEmpty(defaultLanguage))language = defaultLanguage;
			 Object obj=MemCachedUtils.get(ContextUtils.getCompanyId()+"_"+code);
			 if(obj==null)return code;
			 return getMemcachedInternationOptionValue(code,language);
		 }
		if(requrest.getLocale().getLanguage()==null)return code;
		language = requrest.getLocale().getLanguage();
		if(ContextUtils.getCompanyId()==null)return code;
		return getMemcachedInternationOptionValue(code,language);
	}
	@SuppressWarnings("unchecked")
	private String getMemcachedInternationOptionValue(String code,String language){
		Object obj=MemCachedUtils.get(ContextUtils.getCompanyId()+"_"+code);
		if(obj==null)return code;
		Map<String,String> interOpts=(Map<String,String>)obj;
		if(interOpts==null)return code;
		String interOpt=interOpts.get(language);
		if(StringUtils.isNotEmpty(interOpt))return interOpt;
		return code;
	}

	public void getInternations(Page<Internation> internations) {
		internationManager.getInternations(internations);
	}

	public Long getSignIdByUserName(String userName) {
		return signatureManager.getSignIdByUserName(userName);
	}
}
