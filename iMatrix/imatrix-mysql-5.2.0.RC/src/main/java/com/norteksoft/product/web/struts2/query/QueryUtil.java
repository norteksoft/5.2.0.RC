package com.norteksoft.product.web.struts2.query;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings( { "unchecked" })
public class QueryUtil {
	public QueryUtil() {
	}

	/**
	 * 形成迭代集合 这个方法引用自org.apache.struts.taglib.logic.IterateTag
	 * 
	 * @param collection
	 *            String
	 * @return Iterator
	 */
	public static Iterator obj2Iterator(Object collection) {
		Iterator iterator = null;

		// below code is adapted from struts sourcecode
		// Construct an iterator for this collection
		if (collection.getClass().isArray()) {
			try {
				// If we're lucky, it is an array of objects
				// that we can iterate over with no copying
				iterator = Arrays.asList((Object[]) collection).iterator();
			} catch (ClassCastException e) {
				// Rats -- it is an array of primitives
				int length = Array.getLength(collection);
				ArrayList c = new ArrayList(length);
				for (int i = 0; i < length; i++) {
					c.add(Array.get(collection, i));
				}
				iterator = c.iterator();
			}
		} else if (collection instanceof Collection) {
			iterator = ((Collection) collection).iterator();
		} else if (collection instanceof Iterator) {
			iterator = (Iterator) collection;
		} else if (collection instanceof Map) {
			iterator = ((Map) collection).entrySet().iterator();
		}
		return iterator;
	}

	/**
	 * 这个方法引用自org.apache.struts.util.ResponseUtils Filter the specified string
	 * for characters that are sensitive to HTML interpreters, returning the
	 * string with these characters replaced by the corresponding character
	 * entities.
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static String filter(String value) {

		if (value == null || value.length() == 0) {
			return value;
		}

		StringBuffer result = null;
		String filtered = null;
		for (int i = 0; i < value.length(); i++) {
			filtered = null;
			switch (value.charAt(i)) {
			case '<':
				filtered = "&lt;";
				break;
			case '>':
				filtered = "&gt;";
				break;
			case '&':
				filtered = "&amp;";
				break;
			case '"':
				filtered = "&quot;";
				break;
			case '\'':
				filtered = "&#39;";
				break;
			}

			if (result == null) {
				if (filtered != null) {
					result = new StringBuffer(value.length() + 50);
					if (i > 0) {
						result.append(value.substring(0, i));
					}
					result.append(filtered);
				}
			} else {
				if (filtered == null) {
					result.append(value.charAt(i));
				} else {
					result.append(filtered);
				}
			}
		}

		return result == null ? value : result.toString();
	}

	/**
	 * 集合返回ArrayList类型
	 * 
	 * @param collection
	 *            Object
	 * @return ArrayList
	 */
	public static ArrayList toArrayList(Object collection) {
		Iterator it = obj2Iterator(collection);
		ArrayList list = new ArrayList();
		if (it != null) {
			while (it.hasNext()) {
				list.add(it.next());
			}
		}
		return list;
	}

	/**
	 * 集合返回ArrayList类型
	 * 
	 * @param collection
	 *            Object
	 * @param filter
	 *            boolean
	 * @return ArrayList
	 */
	public static ArrayList toArrayList(Object collection, boolean filter) {
		Iterator it = obj2Iterator(collection);
		ArrayList list = new ArrayList();
		if (it != null) {
			while (it.hasNext()) {
				if (filter) {
					list.add(filter((String) it.next()));
				} else {
					list.add((String) it.next());
				}
			}
		}
		return list;
	}

	/**
	 * 根据类型和属性值返回对应类型的对象
	 * 
	 * @param type
	 *            String
	 * @param value
	 *            String
	 * @return Object
	 */
	public static Object getObjectByRealType(String type, String value) {
		Object obj = null;
		// 不是字符串是去掉前后的空格,避免导致转化为其他类型时出错
		if (!FieldType.STRING.equals(type)) {
			value = value.trim();
		}
		if (FieldType.STRING.equals(type)) {
			obj = value;
		} else if (FieldType.INTEGER.equals(type)) {
			obj = new Integer(value);
		} else if (FieldType.FLOAT.equals(type)) {
			obj = new Float(value);
		} else if (FieldType.DOUBLE.equals(type)) {
			obj = new Double(value);
		} else if (FieldType.SHORT.equals(type)) {
			obj = new Short(value);
		} else if (FieldType.LONG.equals(type)) {
			obj = new Long(value);
		} else if (FieldType.BOOLEAN.equals(type)) {
			obj = new Boolean(value);
		} else if (FieldType.CALENDAR.equals(type)) {
			obj = formatCalendar(value);
		} else if (FieldType.SQLDATE.equals(type)) {
			obj = formatSqlDate(value);
		} else if (FieldType.UTILDATE.equals(type)) {
			obj = formatUtilDate(value);
		} else if (FieldType.BOOLEAN.equals(type)) {
			obj = formatTimestamp(value);
		} else if (FieldType.CHARACTER.equals(type)) {
			obj = new Character(value.charAt(0));
		}

		return obj;
	}

	public static Calendar formatCalendar(String strDate) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(formatUtilDate(strDate));
		return cal;
	}

	public static java.sql.Timestamp formatTimestamp(String strDate) {
		java.text.SimpleDateFormat myFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String mystrdate = null;
		try {
			mystrdate = myFormat.format(myFormat.parse(strDate));
			return java.sql.Timestamp.valueOf(mystrdate);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static java.sql.Date formatSqlDate(String strDate) {
		try {
			java.text.SimpleDateFormat myFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			String mystrdate = myFormat.format(myFormat.parse(strDate));
			return java.sql.Date.valueOf(mystrdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据yyyy-MM-dd HH:mm:ss 格式的日期字符获得java.util.date日期格式
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public static java.util.Date formatUtilDate(String strDate) {
		int year;
		int month;
		int day;
		int hour;
		int minute;
		int second;

		if (strDate != null
				&& (strDate.trim().length() == 10 || strDate.trim().length() == 19)) {
			year = Integer.parseInt(strDate.substring(0, 4));
			month = Integer.parseInt(strDate.substring(5, 7));
			day = Integer.parseInt(strDate.substring(8, 10));
			if (strDate.trim().length() == 19) {
				hour = Integer.parseInt(strDate.substring(11, 13));
				minute = Integer.parseInt(strDate.substring(14, 16));
				second = Integer.parseInt(strDate.substring(17, 19));
			} else {
				hour = 0;
				minute = 0;
				second = 0;
			}
			Calendar c = java.util.Calendar.getInstance();
			c.set(year, month - 1, day, hour, minute, second);
			return c.getTime();
		} else {
			return null;
		}
	}

}
