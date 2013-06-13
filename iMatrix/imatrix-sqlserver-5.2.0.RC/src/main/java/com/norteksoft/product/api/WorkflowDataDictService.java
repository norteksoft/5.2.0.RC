package com.norteksoft.product.api;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.wf.base.enumeration.DataDictUseType;
import com.norteksoft.wf.engine.client.DictQueryCondition;
import com.norteksoft.product.api.entity.DataDictionary;

public interface WorkflowDataDictService {

	/**
	 * 根据给定的条件查询数据字典列表
	 * @param condition
	 * @return 数据字典集合
	 */
	public List<DataDictionary> queryDataDict(DictQueryCondition condition);
	
	/**
	 * 根据数据字典查询人员列表
	 * @param dictIds 数据字典id的集合
	 * @return 用户登录名集合
	 */
	public List<String> getCandidate(List<Long> dictIds);
	
	/**
	 * 根据数据字典ID获取办理人登录名
	 * @param dictId 数据字典id
	 * @return 用户登录名集合
	 */
	public List<String> getCandidate(Long dictId);
	
	/**
	 * 根据数据字典ID查询办理人登录名及用户名
	 * @param dictIds 数据字典id集合
	 * @return set集合，String[]{loginName,userName}
	 * @deprecated
	 * 替换为<code>HashMap getUserNames(List<Long> dictIds)</code>
	 */
	public Set<String[]> getCandidateNames(List<Long> dictIds);
	
	/**
	 * 根据数据字典ID查询办理人登录名及用户名
	 * @param dictId 数据字典id
	 * @return set集合，String[]{loginName,userName}
	 * @deprecated
	 * 替换为<code>HashMap getUserNames(Long dictId)</code>
	 */
	public Set<String[]> getCandidateNames(Long dictId);
	
	/**
	 * 根据数据字典ID查询办理人登录名及用户名
	 * @param dictIds 数据字典id集合
	 * @return HashMap集合，key为loginName，value为userName
	 */
	public HashMap<String,String> getUserNames(List<Long> dictIds);
	
	/**
	 * 根据数据字典ID查询办理人登录名及用户名
	 * @param dictId 数据字典id
	 * @return HashMap集合，key为loginName，value为userName
	 */
	public HashMap<String,String> getUserNames(Long dictId);
	
	/**
	 * 根据数据字典ID集合查询数据字典列表
	 * @param dictIds 数据字典id集合
	 * @return 数据字典集合
	 */
	public List<DataDictionary> queryDataDict(List<Long> dictIds);
	
	/**
	 * 根据数据字典ID查询数据字典
	 * @param dictId 数据字典id
	 * @return 数据字典
	 */
	public DataDictionary queryDataDict(Long dictId);
	/**
	 * 根据用户登录名获得数据字典列表
	 * @param loginName 用户登录名
	 * @return
	 */
	public List<DataDictionary> queryDataDicts(String loginName);
    
    /**
     * 根据用户ID查询该用户的直属领导
     * @param userId 用户id
     * @return 该用户的直属领导
     */
    public User getDirectLeader(Long userId);
    
    /**
     * 根据用户ID查询该用户的直属领导
     * @param userId
     * @return
     */
    @Deprecated
    public User getDirectLeader(Long userId,Long companyId);
    
    /**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName 用户登录名
     * @return 该用户的直属领导
     */
    public User getDirectLeader(String loginName);
    /**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName 用户登录名
     * @return 该用户的直属领导
     */
    public List<User> getDirectLeaders(String loginName);
    
    /**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
    @Deprecated
    public User getDirectLeader(String loginName,Long companyId);
    
    /**
	 * 根据数据字典typNo和用途查询数据字典
	 * @param typeNo 数据字典类型编号
	 * @param dataDictUseType 用途 {@link com.norteksoft.wf.base.enumeration.DataDictUseType}
	 * @return 数据字典集合
	 */
	public List<DataDictionary> queryDataDict(String typeNo,DataDictUseType dataDictUseType);
	
	/**
	 * 根据数据字典查询人员和备注信息
	 * @return 人员和备注信息集合
	 */
	public List<String> getCandidateAddition(List<Long> dictIds);
	
	/**
	 * 根据数据字典查询人员和备注信息
	 * @return 人员和备注信息集合
	 */
	public List<String> getCandidateAddition(Long dictId);
	/**
	 * 根据数据字典title得到人员登录名
	 * @param title 数据字典标题
	 * @return 人员登录名集合
	 */
	public List<String> getCandidate(String title);
	
	
	/**
     * 查询所有选项组
     * @return 选项组集合
     */
	@Deprecated
    public List<OptionGroup> getOptionGroups();
	
    /**
     * 根据选项组编号查询选项组
     * @param code 选项组编号
     * @return 选项组
     */
	@Deprecated
    public OptionGroup getOptionGroupByCode(String code);

    /**
     * 根据选项组名称查询选项组
     * @param code 选项组编号
     * @return 选项组
     */
	@Deprecated
    public OptionGroup getOptionGroupByName(String name);
    
    /**
     * 根据选项组查询所有选项 
     * @param optionGroupId 选项组id
     * @return 选项集合
     */
	@Deprecated
    public List<Option> getOptionsByGroup(Long optionGroupId);
    
    /**
     * 根据选项组编号查询选项
     * @param code 选项组编号
     * @return 选项集合
     */
	@Deprecated
    public List<Option> getOptionsByGroupCode(String code);
    
    /**
     * 根据选项组名称查询选项
     * @param name 选项名称 
     * @return 选项集合
     */
	@Deprecated
    public List<Option> getOptionsByGroupName(String name);
}
