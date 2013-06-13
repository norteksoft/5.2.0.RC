package com.norteksoft.acs.service.sale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.web.eunms.Encoding;

/**
 * 数据导入
 * @author Administrator
 */
@Service
@Transactional
public class ImportDataManager {
	
	private SimpleHibernateTemplate<Object, Long> jdbcDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		jdbcDao = new SimpleHibernateTemplate<Object, Long>(sessionFactory, Object.class);
	}
	
	public void saveFileData(File file, String tableName) throws Exception{
		List<List<String>> list = getFileLineDatas(file, Encoding.UTF_8);
		for(int i = 1; i < list.size(); i++){
			List<String> columns = new ArrayList<String>();
			List<String> props = new ArrayList<String>();
			for(int j = 0; j < list.get(i).size(); j++){
				if(list.get(i).get(j) != null){
					String prop = list.get(0).get(j);
					columns.add(prop);
					props.add(list.get(i).get(j));
				}
			}
			String sql = getSql(columns, tableName, props);
			jdbcDao.executeSqlUpdate(sql);
		}
	}
	
	/*
	 * 通过表名、列名 生成插入sql语句 
	 * @param propertyes  属性列表
	 * @param tableName   表名
	 * @param propValues  属性值列表
	 */
	private String getSql(List<String> propertyes, String tableName, List<String> propValues){
		StringBuilder sql = new StringBuilder();
		StringBuffer values = new StringBuffer(" values(");
		sql.append("insert into ").append(tableName).append("(");
		String prop = "";
		for(int i = 0; i < propertyes.size(); i++){
			prop = propertyes.get(i);
			sql.append(prop).append(",");
			if("ts".equals(prop.trim())){
				values.append("TIMESTAMP ").append(propValues.get(i).substring(0, 20)).append("',");
			}else{
				values.append(propValues.get(i)).append(",");
			}
		}
		if(sql.lastIndexOf(",") != -1 && sql.lastIndexOf(",") == sql.length() - 1){
			sql.replace(sql.length()-1, sql.length(), "");
		}
		if(values.lastIndexOf(",") != -1 && values.lastIndexOf(",") == values.length() - 1){
			values.replace(values.length()-1, values.length(), "");
		}
		sql.append(") ").append(values.toString()).append(")");
		return sql.toString();
	}
	
	/*
	 * 通过文件名获取文件中所有行的数据
	 * @param filePath
	 * @throws Exception 
	 */
	private List<List<String>> getFileLineDatas(File file, Encoding charset) throws Exception{
		List<String> lineDatas = new ArrayList<String>();
		InputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset.getCode()));
		while(true){
			String line = reader.readLine();
			if(line == null){
				break;
			}else{
				lineDatas.add(line);
			}
		}
		reader.close();
		return analyzeString(lineDatas);
	}
	
	/*
	 * 第一行为表的列名
	 * 之后的为数据
	 * @param lineDatas
	 */
	public List<List<String>> analyzeString(List<String> lineDatas){
		String[] propertyNames = lineDatas.get(0).trim().replaceAll("#", "").split("[,]");
		List<List<String>> result = new ArrayList<List<String>>();
		List<String> lineData = new ArrayList<String>();
		Collections.addAll(lineData, propertyNames);
		result.add(lineData);
		for(int i = 1; i < lineDatas.size(); i++){
			lineData = new ArrayList<String>();
			String line = lineDatas.get(i).replaceAll("\"", "'");
			getPropertys(lineData, line);
			result.add(lineData);
		}
		return result;
	}
	
	/*
	 * 以逗号的方式，分割给定字符串存放到给定的List中
	 */
	private void getPropertys(List<String> datas, String line){
		int index = line.indexOf(',');
		if(index == -1) {
			if(line.trim().length() <= 0)
				datas.add(null);
			else
				datas.add(line.trim());
			return;
		}
		String firstProp = line.substring(0, index);
		if(firstProp == null || firstProp.trim().length() <= 0)
			datas.add(null);
		else 
			datas.add(firstProp);
		String otherProp = line.substring(index+1, line.length());
		getPropertys(datas, otherProp);
	} 
}
