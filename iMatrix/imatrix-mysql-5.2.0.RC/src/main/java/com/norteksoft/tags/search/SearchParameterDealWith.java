package com.norteksoft.tags.search;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.DateFormat;

public class SearchParameterDealWith {
	
	/**
	 * 查询参数处理
	 * @param request 请求
	 * @param entityName 实体的英文名(首字母大写)
	 * @param companyId 公司ID
	 * @return map的key为hql, map的List为参数
	 */
	public static Map<String ,List<Object>> dealWithParametersWithEntity(String searchParameters, String entityName, Long companyId){
		//String searchParameters = request.getParameter("searchParameters");
		if(StringUtils.isNotEmpty(searchParameters)){
			//TODO 可能需要一些对传入条件格式的判断工作，需要前后台配合完成
			List<Object> parameterValues = new ArrayList<Object>();
			parameterValues.add(0, companyId);
			int parameterEndIndex = 0;
			while((parameterEndIndex = searchParameters.indexOf("}"))>=0){	
				int parameterBeginIndex = searchParameters.indexOf("{");
				String parameter = searchParameters.substring(parameterBeginIndex+1, parameterEndIndex);
				String tempParameter = parameter;
				if(tempParameter.contains("like")){// ONLY STRING
					String beforeStr = StringUtils.substringBefore(tempParameter, "like");
					if(beforeStr.contains("not ")){
						String[] tempParameterPart = tempParameter.split("not like");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						tempParameter = "t." + tempParameterName[1] + " not like ?";
						parameterValues.add("%" + tempParameterPart[1].trim() + "%");
					}else{
						String[] tempParameterPart = tempParameter.split("like");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						tempParameter = "t." + tempParameterName[1] + " like ?";
						parameterValues.add("%" + tempParameterPart[1].trim() + "%");
					}
				}else if(tempParameter.contains("=")){// NUMBER OR DATE OR TIME
					int index = tempParameter.indexOf("=");
					String sign = tempParameter.substring(index-1, index);
					if(">".equals(sign)){
						String[] tempParameterPart = tempParameter.split(">=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						if("DATE".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " >= ?";
							parameterValues.add(stringToDate(tempParameterPart[1].trim()));
						}else if("TIME".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " >= ?";
							parameterValues.add(stringToTimestamp(stringToDate(tempParameterPart[1].trim())));
						}
					}else if("<".equals(sign)){
						String[] tempParameterPart = tempParameter.split("<=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						if("DATE".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " <= ?";
							parameterValues.add(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59));
						}else if("TIME".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " <= ?";
							parameterValues.add(stringToTimestamp(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59)));
						}
					}else{
						String[] tempParameterPart = tempParameter.split("=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						if("DATE".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " = ?";
							parameterValues.add(stringToDate(tempParameterPart[1].trim()));
						}else if("TIME".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " = ?";
							parameterValues.add(stringToTimestamp(stringToDate(tempParameterPart[1].trim())));
						}else if("NUMBER".equals(tempParameterName[0])){
							tempParameter = "t." + tempParameterName[1] + " = ?";
							parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
						}
					}
				}else if(tempParameter.contains("<>")){// ONLY NUMBER
					String[] tempParameterPart = tempParameter.split("<>");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					tempParameter = "t." + tempParameterName[1] + " <> ?";
					parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
				}else if(tempParameter.contains(">")){// NUMBER OR DATE OR TIME
					String[] tempParameterPart = tempParameter.split(">");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					if("NUMBER".equals(tempParameterName[0])){
						tempParameter = "t." + tempParameterName[1] + " > ?";
						parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
					}else if("DATE".equals(tempParameterName[0])){
						tempParameter = "t." + tempParameterName[1] + " > ?";
						parameterValues.add(stringToDate(tempParameterPart[1].trim()));
					}else if("TIME".equals(tempParameterName[0])){
						tempParameter = "t." + tempParameterName[1] + " > ?";
						parameterValues.add(stringToTimestamp(stringToDate(tempParameterPart[1].trim())));
					}
				}else if(tempParameter.contains("<")){// NUMBER OR DATE OR TIME
					String[] tempParameterPart = tempParameter.split("<");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					if("NUMBER".equals(tempParameterName[0])){
						tempParameter = "t." + tempParameterName[1] + " < ?";
						parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
					}else if("DATE".equals(tempParameterName[0])){
						tempParameter = "t." + tempParameterName[1] + " < ?";
						parameterValues.add(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59));
					}else if("TIME".equals(tempParameterName[0])){
						tempParameter = "t." + tempParameterName[1] + " < ?";
						parameterValues.add(stringToTimestamp(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59)));
					}
				}
				searchParameters = searchParameters.replace("{"+parameter+"}", tempParameter);
			}
			String hql = "from " + entityName + " t where t.companyId = ? and " + searchParameters + " order by t.id asc";
			Map<String ,List<Object>> parameters = new HashMap<String, List<Object>>();
			parameters.put(hql, parameterValues);
			return parameters;
		}else{
			return null;
		}
	}
	
	private static Date stringToDate(String dateString){
		DateFormat df = DateFormat.getDateInstance();
		Date d = null;
		try {
			d = df.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	private static Date getDate(Date date,int h,int s,int m){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), h, s, m);
		return cal.getTime();
	}
	
	private static Timestamp stringToTimestamp(Date d){
		Timestamp ts = new Timestamp(0);
		ts.setTime(d.getTime());
		return ts;
	}
	
	/**
	 * 无实体的动态查询参数处理-0.01
	 * @param request 请求
	 * @return 条件的Map集合
	 */
	public static Map<String, List<Object>> dealWithParametersWithoutEntity(HttpServletRequest request){
		String searchParameters = request.getParameter("searchParameters");
		if(StringUtils.isNotEmpty(searchParameters)){
			Map<String, List<Object>> parameters = new HashMap<String, List<Object>>();
			int parameterEndIndex = 0;
			while((parameterEndIndex = searchParameters.indexOf("}"))>=0){
				int parameterBeginIndex = searchParameters.indexOf("{");
				String parameter = searchParameters.substring(parameterBeginIndex+1, parameterEndIndex);
				if(parameter.contains("like")){// ONLY STRING
					String beforeStr = StringUtils.substringBefore(parameter, "like");
					if(beforeStr.contains("not ")){
						String[] tempParameterPart = parameter.split("not like");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						List<Object> values = null;
						if(parameters.containsKey(tempParameterName[1])){
							values = parameters.get(tempParameterName[1]);
							values.add(tempParameterPart[1]);
						}else{
							values = new ArrayList<Object>();
							values.add(tempParameterPart[1]);
						}
						parameters.put(tempParameterName[1], values);
					}else{
						String[] tempParameterPart = parameter.split("like");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						List<Object> values = null;
						if(parameters.containsKey(tempParameterName[1])){
							values = parameters.get(tempParameterName[1]);
							values.add(tempParameterPart[1]);
						}else{
							values = new ArrayList<Object>();
							values.add(tempParameterPart[1]);
						}
						parameters.put(tempParameterName[1], values);
					}
				}else if(parameter.contains("=")){// NUMBER OR DATE OR TIME
					int index = parameter.indexOf("=");
					String sign = parameter.substring(index-1, index);
					if(">".equals(sign)){
						String[] tempParameterPart = parameter.split(">=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						List<Object> values = null;
						if(parameters.containsKey(tempParameterName[1])){
							values = parameters.get(tempParameterName[1]);
							values.add(tempParameterPart[1]);
						}else{
							values = new ArrayList<Object>();
							values.add(tempParameterPart[1]);
						}
						parameters.put(tempParameterName[1], values);
					}else if("<".equals(sign)){
						String[] tempParameterPart = parameter.split("<=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						List<Object> values = null;
						if(parameters.containsKey(tempParameterName[1])){
							values = parameters.get(tempParameterName[1]);
							values.add(tempParameterPart[1]);
						}else{
							values = new ArrayList<Object>();
							values.add(tempParameterPart[1]);
						}
						parameters.put(tempParameterName[1], values);
					}else{
						String[] tempParameterPart = parameter.split("=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						List<Object> values = null;
						if(parameters.containsKey(tempParameterName[1])){
							values = parameters.get(tempParameterName[1]);
							values.add(tempParameterPart[1]);
						}else{
							values = new ArrayList<Object>();
							values.add(tempParameterPart[1]);
						}
						parameters.put(tempParameterName[1], values);
					}
				}else if(parameter.contains("<>")){// ONLY NUMBER
					String[] tempParameterPart = parameter.split("<>");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					List<Object> values = null;
					if(parameters.containsKey(tempParameterName[1])){
						values = parameters.get(tempParameterName[1]);
						values.add(tempParameterPart[1]);
					}else{
						values = new ArrayList<Object>();
						values.add(tempParameterPart[1]);
					}
					parameters.put(tempParameterName[1], values);
				}else if(parameter.contains(">")){// NUMBER OR DATE OR TIME
					String[] tempParameterPart = parameter.split(">");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					List<Object> values = null;
					if(parameters.containsKey(tempParameterName[1])){
						values = parameters.get(tempParameterName[1]);
						values.add(tempParameterPart[1]);
					}else{
						values = new ArrayList<Object>();
						values.add(tempParameterPart[1]);
					}
					parameters.put(tempParameterName[1], values);
				}else if(parameter.contains("<")){// NUMBER OR DATE OR TIME
					String[] tempParameterPart = parameter.split("<");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					List<Object> values = null;
					if(parameters.containsKey(tempParameterName[1])){
						values = parameters.get(tempParameterName[1]);
						values.add(tempParameterPart[1]);
					}else{
						values = new ArrayList<Object>();
						values.add(tempParameterPart[1]);
					}
					parameters.put(tempParameterName[1], values);
				}
				searchParameters = searchParameters.replace("{"+parameter+"}", "");
			}
			return parameters;
		}else{
			return null;
		}
	}
	
	/**
	 * 无实体的动态查询参数处理-0.02
	 * @param request 请求
	 * @return SQL语句中的条件语句
	 */
	public static Map<String, List<Object>> dealWithParametersWithColumns(String searchParameters){
		//String searchParameters = request.getParameter("searchParameters");
		if(StringUtils.isNotEmpty(searchParameters)){
			List<Object> parameterValues = new ArrayList<Object>();
			int parameterEndIndex = 0;
			while((parameterEndIndex = searchParameters.indexOf("}"))>=0){
				int parameterBeginIndex = searchParameters.indexOf("{");
				String parameter = searchParameters.substring(parameterBeginIndex+1, parameterEndIndex);
				String tempStr = parameter;
				if(tempStr.contains("like")){// ONLY STRING
					String beforeStr = StringUtils.substringBefore(tempStr, "like");
					if(beforeStr.contains("not ")){
						String[] tempParameterPart = tempStr.split("not like");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						tempStr = tempParameterName[1] + " not like ?";
						parameterValues.add("%" + tempParameterPart[1].trim() + "%");
					}else{
						String[] tempParameterPart = tempStr.split("like");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						tempStr = tempParameterName[1] + " like ?";
						parameterValues.add("%" + tempParameterPart[1].trim() + "%");
					}
				}else if(tempStr.contains("=")){// NUMBER OR DATE OR TIME
					int index = tempStr.indexOf("=");
					String sign = tempStr.substring(index-1, index);
					if(">".equals(sign)){
						String[] tempParameterPart = tempStr.split(">=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						if("DATE".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " >= ?";
							parameterValues.add(stringToDate(tempParameterPart[1].trim()));
						}else if("TIME".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " >= ?";
							parameterValues.add(stringToTimestamp(stringToDate(tempParameterPart[1].trim())));
						}
					}else if("<".equals(sign)){
						String[] tempParameterPart = tempStr.split("<=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						if("DATE".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " <= ?";
							parameterValues.add(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59));
						}else if("TIME".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " <= ?";
							parameterValues.add(stringToTimestamp(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59)));
						}
					}else{
						String[] tempParameterPart = tempStr.split("=");
						String[] tempParameterName = tempParameterPart[0].trim().split("-");
						if("DATE".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " = ?";
							parameterValues.add(stringToDate(tempParameterPart[1].trim()));
						}else if("TIME".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " = ?";
							parameterValues.add(stringToTimestamp(stringToDate(tempParameterPart[1].trim())));
						}else if("NUMBER".equals(tempParameterName[0])){
							tempStr = tempParameterName[1] + " = ?";
							parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
						}
					}
				}else if(tempStr.contains("<>")){// ONLY NUMBER
					String[] tempParameterPart = tempStr.split("<>");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					tempStr = tempParameterName[1] + " <> ?";
					parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
				}else if(tempStr.contains(">")){// NUMBER OR DATE OR TIME
					String[] tempParameterPart = tempStr.split(">");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					if("NUMBER".equals(tempParameterName[0])){
						tempStr = tempParameterName[1] + " > ?";
						parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
					}else if("DATE".equals(tempParameterName[0])){
						tempStr = tempParameterName[1] + " > ?";
						parameterValues.add(stringToDate(tempParameterPart[1].trim()));
					}else if("TIME".equals(tempParameterName[0])){
						tempStr = tempParameterName[1] + " > ?";
						parameterValues.add(stringToTimestamp(stringToDate(tempParameterPart[1].trim())));
					}
				}else if(tempStr.contains("<")){// NUMBER OR DATE OR TIME
					String[] tempParameterPart = tempStr.split("<");
					String[] tempParameterName = tempParameterPart[0].trim().split("-");
					if("NUMBER".equals(tempParameterName[0])){
						tempStr = tempParameterName[1] + " < ?";
						parameterValues.add(Long.valueOf(tempParameterPart[1].trim()).longValue());
					}else if("DATE".equals(tempParameterName[0])){
						tempStr = tempParameterName[1] + " < ?";
						parameterValues.add(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59));
					}else if("TIME".equals(tempParameterName[0])){
						tempStr = tempParameterName[1] + " < ?";
						parameterValues.add(stringToTimestamp(getDate(stringToDate(tempParameterPart[1].trim()), 23, 59, 59)));
					}
				}
				searchParameters = searchParameters.replace("{"+parameter+"}", tempStr);
			}
			Map<String ,List<Object>> parameters = new HashMap<String, List<Object>>();
			parameters.put(searchParameters, parameterValues);
			return parameters;
		}else{
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		String parameter1 = "({DATE-createDate < 2010-12-10} or {STRING-title like 自己}) and {STRING-groupName like 测试}";
		String parameter2 = "{STRING-groupName like 并发} or (({NUMBER-active = 2} and {DATE-transactDate < 2010-12-10}) and {STRING-creatorName like 吴荣})";  
		@SuppressWarnings("unused")
		Map<String, List<Object>> aa = SearchParameterDealWith.dealWithParametersWithEntity(parameter1, "task", 8050l);
		@SuppressWarnings("unused")
		Map<String, List<Object>> bb = SearchParameterDealWith.dealWithParametersWithColumns(parameter2);
		//System.out.println(SearchParameterDealWith.DealWithcolumnName("replaceMentList"));
	}
	
}
