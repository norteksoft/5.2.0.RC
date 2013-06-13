package com.norteksoft.product.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.product.orm.Page;


public interface SettingService {
	
	/**
	 * 根据选项组编号查询选项组默认值
	 * @param optionGroupCode
	 * @return
	 */
	String getOptionGroupDefaultValue(String optionGroupCode);
	
	/**
	 * 根据选项组ID查询选项组默认值
	 * @param optionGroupCode
	 * @return
	 */
	String getOptionGroupDefaultValue(Long optionGroupId);
	
	/**
     * 查询所有选项组
     * @return 选项组集合
     */
    public List<OptionGroup> getOptionGroups();
	
    /**
     * 根据选项组编号查询选项组
     * @param code 选项组编号
     * @return 选项组
     */
    public OptionGroup getOptionGroupByCode(String code);

    /**
     * 根据选项组名称查询选项组
     * @param code 选项组编号
     * @return 选项组
     */
    public OptionGroup getOptionGroupByName(String name);
    
    /**
     * 根据选项组ID查询所有选项 
     * @param optionGroupId 选项组id
     * @return 选项集合
     */
    public List<Option> getOptionsByGroup(Long optionGroupId);
    
    /**
     * 根据选项组编号查询选项
     * @param code 选项组编号
     * @return 选项集合
     */
    public List<Option> getOptionsByGroupCode(String code);
    
    /**
     * 根据选项组名称查询选项
     * @param name 选项名称 
     * @return 选项集合
     */
    public List<Option> getOptionsByGroupName(String name);
    
    /**
     * 查询给定日期段中所有的节假日和工作日
     * @param startDate
     * @param endDate
     * @return Map<String, List<Date>>  key[spareDate:节假日, workDate:工作日]
     */
    public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate);
    /**
     * 国际化设置中获得该编号对应的值
     * @param code  国际化编号 
     * @param language  语言种类
     * @return
     */
    public String getInternationOptionValue(String code);
    /**
     * 分页查询国际化设置
     * @param internations
     */
    public void getInternations(Page<Internation> internations);

    /**
     * 根据用户名称取得签章id
     * @param userName
     * @return
     */
    public Long getSignIdByUserName(String userName);
}
