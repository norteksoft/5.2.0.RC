package com.norteksoft.mms.base.data;
/**
 * 数据迁移接口
 * @author liudongxia
 *
 */
public interface DataTransfer {
	/**
	 * 导出
	 * @param systemIds 导出的系统id集合，以逗号隔开
	 * @param companyId 数据所在的公司id
	 * @param fileConfig 文件配置
	 */
	public void backup(String systemIds,Long companyId,FileConfigModel fileConfig);
	/**
	 * 导入
	 * @param companyId 要导入数据的公司id
	 * @param fileConfig 文件配置
	 * @param imatrixInfo 导入基础数据时，底层平台的ip、端口、服务名称
	 */
	public void restore(Long companyId,FileConfigModel fileConfig,String... imatrixInfo);
}
