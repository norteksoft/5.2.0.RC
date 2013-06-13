package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.enumeration.TimingType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class CornInfoDao extends HibernateDao<Timer, Long> {
	
	public List<Object> getCornInfo(){
		String hql = "select c,j from Timer c,TimedTask j where c.jobId=j.id and j.dataState=?";
		return this.findNoCompanyCondition(hql,DataState.ENABLE);
	}
	
	public List<Timer> getCornInfoByJob(Long jobInfoId){
		return this.findNoCompanyCondition("from Timer c where c.jobId=?", jobInfoId);
	}
	
	public Timer getCornInfo(String typeEnum,String corn,String dateTime,String weekTime,String appointTime,String appointSet,Long jobInfoId){
		String hql="from Timer t ";
		List<Timer> corns=null;
		if(TimingType.everyDate.toString().equals(typeEnum)){
			hql=hql+" where t.timingType=? and t.jobId=? and t.corn=?";
			corns=this.find(hql,TimingType.everyDate,jobInfoId,corn);
			if(corns.size()>0)return corns.get(0);
		}else if(TimingType.everyMonth.toString().equals(typeEnum)){
			hql=hql+" where t.timingType=? and t.jobId=? and t.dateTime=? and t.corn=?";
			corns=this.find(hql,TimingType.everyMonth,jobInfoId,dateTime,corn);
			if(corns.size()>0)return corns.get(0);
		}else if(TimingType.everyWeek.toString().equals(typeEnum)){
			hql=hql+" where t.timingType=? and t.jobId=? and t.weekTime=?";
			corns=this.find(hql,TimingType.everyWeek,jobInfoId,weekTime);
			if(corns.size()>0)return corns.get(0);
		}else if(TimingType.appointSet.toString().equals(typeEnum)){
			hql=hql+" where t.timingType=? and t.jobId=? and t.appointSet=?";
			corns=this.find(hql,TimingType.appointSet,jobInfoId,appointSet);
			if(corns.size()>0)return corns.get(0);
		}else if(TimingType.appointTime.toString().equals(typeEnum)){
			hql=hql+" where t.timingType=? and t.jobId=? and t.appointTime=?";
			corns=this.find(hql,TimingType.appointTime,jobInfoId,appointTime);
			if(corns.size()>0)return corns.get(0);
		}
		return null;
	}
	
}
