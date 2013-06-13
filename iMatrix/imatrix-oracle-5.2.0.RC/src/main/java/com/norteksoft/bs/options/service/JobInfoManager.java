package com.norteksoft.bs.options.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.CornInfoDao;
import com.norteksoft.bs.options.dao.JobInfoDao;
import com.norteksoft.bs.options.entity.TimedTask;
import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.Scheduler;

@Service
@Transactional
public class JobInfoManager {
	
	@Autowired
	private CornInfoDao cornInfoDao;
	
	@Autowired
	private JobInfoDao jobInfoDao;
	
	public void saveJobInfo(TimedTask jobInfo){
		this.jobInfoDao.save(jobInfo);
	}
	
	public void deleteJobInfo(Long id){
		this.jobInfoDao.delete(id);
	}
	
	public TimedTask getJobInfo(Long id){
		return this.jobInfoDao.get(id);
	}
	
	public Page<TimedTask> getJobInfo(Page<TimedTask> page,Long systemId){
		return this.jobInfoDao.findPage(page, "from TimedTask j where j.systemId=?", systemId);
	}
	
	/**
	 * 批量删除
	 * @param id
	 */
	public String deleteJobInfos(String ids){
		int num=0;
		int error=0;
		if(StringUtils.isNotEmpty(ids)){
			String str[]=ids.split(",");
			for (String string : str) {
				
				Long id=Long.valueOf(string);
				TimedTask jobInfo=getJobInfo(id);
				if(!(DataState.ENABLE.equals(jobInfo.getDataState()))){
					num++;
					this.cornInfoDao.batchExecute("delete from Timer c where c.jobId=?", id);
					this.jobInfoDao.delete(id);
				}else{
					error++;
				}
			}
		}
		return num+"=-"+error;
	}
	
	/**
	 * 批量设置状态
	 * @param id
	 */
	public int setJobInfos(String ids,DataState state){
		int num=0;
		if(StringUtils.isNotEmpty(ids)){
			String str[]=ids.split(",");
			for (String string : str) {
				num++;
				Long id=Long.valueOf(string);
				TimedTask jobInfo=getJobInfo(id);
				jobInfo.setDataState(state);
				saveJobInfo(jobInfo);
				List<Timer> cornInfos=getCornInfos(id);
				for (Timer cornInfo : cornInfos) {
					if(state.equals(DataState.ENABLE)){
						cornInfo.setJobInfo(jobInfo);
						Scheduler.addJob(cornInfo);
					}else if(state.equals(DataState.DISABLE)){
						Scheduler.deleteJob(cornInfo);
					}
				}
			}
		}
		return num;
	}
	
	
	/**时间设置
	===============================================================
	*/
	public void saveCornInfo(Timer CornInfo){
		this.cornInfoDao.save(CornInfo);
	}
	
	public void deleteCornInfo(Long id){
		this.cornInfoDao.delete(id);
	}
	
	public void deleteCornInfo(Timer CornInfo){
		this.cornInfoDao.delete(CornInfo);
	}
	
	/**
	 * 批量删除
	 * @param id
	 */
	public void deleteCornInfos(String ids){
		if(StringUtils.isNotEmpty(ids)){
			String str[]=ids.split(",");
			for (String string : str) {
				Long id=Long.valueOf(string);
				Timer cornInfo=getCornInfo(id);
				TimedTask jobInfo=getJobInfo(cornInfo.getJobId());
				if(!(DataState.ENABLE.equals(jobInfo.getDataState()))){
					this.cornInfoDao.delete(cornInfo);
				}else{
					Scheduler.deleteJob(cornInfo);
					this.cornInfoDao.delete(cornInfo);
				}
			}
		}
	}
	
	
	public Timer getCornInfo(Long id){
		return this.cornInfoDao.get(id);
	}
	
	public List<Timer> getCornInfos(Long jobId){
		return this.cornInfoDao.find("from Timer c where c.jobId=?", jobId);
	}
	
	public Page<Timer> getCornInfos(Page<Timer> page,Long jobId){
		return this.cornInfoDao.findPage(page,"from Timer c where c.jobId=?", jobId);
	}
	
	public List<Timer> getCornInfos(){
		List<Object> objs = cornInfoDao.getCornInfo();
		List<Timer> infos = new ArrayList<Timer>();
		for(Object obj : objs){
			Object[] arr = (Object[]) obj;
			Timer ci = (Timer)arr[0];
			ci.setJobInfo((TimedTask)arr[1]);
			infos.add(ci);
		}
		return infos;
	}
	/**
	 * 根据系统获得定时信息
	 * @param companyId
	 * @param systemId
	 * @return
	 */
	public List<TimedTask> getJobInfoBySystem(Long companyId,String systemIds){
		return jobInfoDao.getJobInfoBySystem(companyId, systemIds);
	}
	/**
	 * 根据定时信息获得定时时间信息
	 * @param jobInfoId
	 * @return
	 */
	public List<Timer> getCornInfoByJob(Long jobInfoId){
		return cornInfoDao.getCornInfoByJob(jobInfoId);		
	}
	/**
	 * 根据定时编码获得定时信息
	 * @param code
	 * @param systemCode
	 * @return
	 */
	public TimedTask getJobInfoByCode(String code,String systemCode){
		return jobInfoDao.getJobInfoByCode(code, systemCode);
	}
	public Timer getCornInfo(String typeEnum,String corn,String dateTime,String weekTime,String appointTime,String appointSet,Long jobInfoId){
		return cornInfoDao.getCornInfo(typeEnum, corn, dateTime, weekTime, appointTime, appointSet, jobInfoId);
	}
}
