package com.norteksoft.product.web.struts2.query;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "unchecked" })
public class StringUtil {

  //中文格式,如:2000年01月01日
  public static final int CN = 0;

  //'/',如:2000/01/01
  public static final int SLIP = 1;

  //'.',如:2000.01.01
  public static final int DOT = 2;

  //'-',如:2000.01.01
  public static final int DASH = 3;

  public StringUtil() {
  }

  /**
   * 去除一个字符串的'0'前缀
   * @param sequ String
   * @return String
   */
  public static String trimPreZero(String sequ) {
    String patternString = "0*";
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(sequ);
    return matcher.find() ? sequ.substring(matcher.end()) : ""; //后面的字符
  }

  /**
   * 取sql语句从"from"之后的字符串
   * @param sql String
   * @return String
   */
  public static String trimFrom(String sql) {
    String patternString = "[Ff][Rr][Oo][Mm]";
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(sql);

    return matcher.find() ? sql.substring(matcher.start()) : ""; //后面的字符
  }

  public static int findStrPosition(String sql, String targetStr) {
    String patternString = str2RegexStr(targetStr);
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(sql);

    return matcher.find() ? matcher.start() : -1;
  }

  public static boolean findSelectSpaceDistinct(String sql) {
    StringBuffer patternString = new StringBuffer();
    patternString.append("\\s*").append(str2RegexStr("select"))
        .append("\\s*").append("distinct");

    Pattern pattern = Pattern.compile(patternString.toString());
    Matcher matcher = pattern.matcher(sql);

    return matcher.find() && matcher.start() == 0 ? true : false;
  }

  public static String findObjName(String sql) {
    // 8 is the length of 'distinct'
    return sql.substring(findStrPosition(sql, "distinct") + 8,
                         findStrPosition(sql, "from")).trim();
  }

  /**
   * 根据字符串FROM
   * 形成形如[Ff][Rr][Oo][Mm]的正则表达式
   * @param str String
   * @return String
   */
  public static String str2RegexStr(String str) {
    String upperCaseStr = str.toUpperCase();
    char[] strArr = upperCaseStr.toCharArray();
    char[] regexArr = new char[str.length() * 4];
    for (int i = 0; i < strArr.length; i++) {
      regexArr[4 * i] = '[';
      regexArr[4 * i + 1] = strArr[i];
      regexArr[4 * i + 2] = (char) (strArr[i] + 32); // to lower case
      regexArr[4 * i + 3] = ']';
    }

    return String.copyValueOf(regexArr);
  }

  /**
   * 取一个字符序列的下一个
   * @param sequ String
   * @return String
   */
  public static String getNextSequ(String sequ) {
    StringBuffer nextSequ = new StringBuffer();
    int fullLen = sequ.length();
    String postStr = trimPreZero(sequ);
    if (!"".equals(postStr)) {
      if (isInteger(postStr)) {
        long postVal = Long.parseLong(postStr);
        int newLen = String.valueOf(++postVal).length();
        nextSequ = addPostZero(nextSequ, fullLen - newLen);
        nextSequ.append(String.valueOf(postVal));
      }
      else {
        return sequ;
      }
    }
    else {
      nextSequ = addPostZero(nextSequ, fullLen - 1);
      nextSequ.append(String.valueOf(1));
    }
    return nextSequ.toString();
  }

  /**
   * 为字符串设置前置'0'
   * @param strBuf StringBuffer
   * @param num int
   * @return StringBuffer
   */
  public static StringBuffer addPostZero(StringBuffer strBuf, int num) {
    for (int i = 0; i < num; i++) {
      strBuf.append("0");
    }
    return strBuf;
  }

  /**
   * 判断一个字符串是否为整数
   * @param str String
   * @return boolean
   */
  public static boolean isInteger(String str) {
    try {
      Long.parseLong(str);
    }
    catch (NumberFormatException ex) {
      return false;
    }
    return true;
  }

  /**
   * 测试用
   * @param test int[]
   * @return String
   */
  public static String test(int[] test) {
    int temp = 1;
    for (int i = test.length - 1; i >= 0 && temp == 1; i--) {
      if (test[i] + 1 > 9) {
        test[i] = (test[i] + 1) % 10;
        temp = 1;
      }
      else {
        test[i]++;
        temp = 0;
      }
    }
    return null;
  }

  /**
   * 取一个字符串的下一个序号
   * 如果新字符串超过长度,则取字符串长度加1
   * @param str String
   * @return String
   */
  public static String nextSequence(String str) {
    char[] tempChar = str.trim().toCharArray();
    int i = tempChar.length;
    int carry = 1; //是否有进位的标志
    while (--i >= 0) {
      int temp = tempChar[i] - 47; //47 = 48 - 1(carry)意思:先将字符转为数字,然后加1
      if (temp > 9) {
        tempChar[i] = (char) (temp % 10 + 48); //需要进位,继续
      }
      else {
        tempChar[i]++; //不需要进位,自增1,结束
        carry = 0;
        break;
      }
    }
    String nextSequence = String.copyValueOf(tempChar);
    //判断字符串是否超过长度
    return i == -1 && carry == 1 ? "1" + nextSequence : nextSequence;
  }

  /**
   * 取一个字符串的下一个序号
   * @param str String
   * @return String
   */
  public static String nextSequence2(String str) {
    char[] tempChar = str.trim().toCharArray();
    int over = 1; //是否超过长度的标志
    for (int i = tempChar.length - 1; i >= 0; i--) {
      int temp = tempChar[i] - 47; //47 = 48 - 1(carry)意思:先将字符转为数字,然后加1
      if (temp > 9) {
        tempChar[i] = (char) (temp % 10 + 48); //需要进位,继续
        over = i - 1;
      }
      else {
        tempChar[i]++; //不需要进位,自增1,结束
        break;
      }
    }
    String nextSequence = String.copyValueOf(tempChar);
    //判断字符串是否超过长度
    return over == -1 ? "1" + nextSequence : nextSequence;
  }

  /**
   * 中文格式,如:2000年01月01日
   * @param str String
   * @return String
   */
  public static String cn2Date(String str) {
    char[] tempStr = str.toCharArray();
    for (int i = 0; i < tempStr.length; i++) {
      switch (tempStr[i]) {
        case '年':
        case '月':
          tempStr[i] = '-';
          break;
        case '日':
          tempStr[i] = ' ';
          break;
      }
    }
    return String.valueOf(tempStr).trim();
  }

  /**
   * 格式,如:2000/01/01
   * @param str String
   * @return String
   */
  public static String slip2Date(String str) {
    return str.replace('/', '-');
  }

  /**
   * 格式,如:2000.01.01
   * @param str String
   * @return String
   */
  public static String dot2Date(String str) {
    return str.replace('.', '-');
  }

  /**
   * 转换日期为需要的格式
   * @param str String
   * @param flag int
   * @return String
   */
  public static String formatDate(String str, int flag) {
    String format = "";
    if (str != null && !str.equals("")) {
      switch (flag) {
        case 0:
          format = cn2Date(str);
          break;
        case 1:
          format = slip2Date(str);
          break;
        case 2:
          format = dot2Date(str);
          break;
      }
    }
    return format;
  }

  /**
   * 日期以2000年01月01日格式显示
   * @param str String
   * @return String
   */
  public static String date2Cn(String str) {
    if (str != null && !str.equals("")) {
      char[] tempStr = str.toCharArray();
      tempStr[4] = '年';
      tempStr[7] = '月';
      return String.valueOf(tempStr).concat("日");
    }
    else {
      return str;
    }
  }

  /**
   * 显示日期的格式
   * @param str String
   * @param flag int
   * @return String
   */
  public static String formatDateShow(String str, int flag) {
    if (str != null && !str.equals("")) {
      String formatDate = str;
      switch (flag) {
        case 0:
          char[] tempStr = str.toCharArray();
          tempStr[4] = '年';
          tempStr[7] = '月';
          formatDate = String.valueOf(tempStr).concat("日");
          break;
        case 1:
          formatDate = str.replace('-', '/');
          break;
        case 2:
          formatDate = str.replace('-', '.');
          break;
      }
      return formatDate;
    }
    else {
      return str;
    }
  }

  /**
   *替换不规则的order by 子句为" ORDER BY "
   * @param sql String
   * @return String
   */
  public static String replaceOrderBy(String sql) {
    String patternString = "\\s*[Oo][Rr][Dd][Ee][Rr]\\s+[Bb][Yy]\\s*";
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(sql);
    String wantStr = matcher.replaceAll(" ORDER BY "); //后面的字符
    return wantStr;
  }

  /**
   * 过滤 sql语句中的order by 子句
   * 注意：order by 子句以及order by 字段不能含有()
   * @param sequ String
   * @return String
   */
  public static String removeOrderBy(String sequ) {
    String patternString = "\\sORDER\\sBY\\s[a-zA-Z0-9\\.\\_\\,\\s]+";
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(replaceOrderBy(sequ));
    String wantStr = matcher.replaceAll("");
    return wantStr;
  }

  /**
   * 形成查询语句的in()子句
   * @param strArr String[]
   * @return String
   */
  public static String formInStr(String[] strArr) {
    StringBuffer sbf = new StringBuffer();
    if (strArr != null && strArr.length > 0) {
      sbf.append("'").append(strArr[0]).append("'");
      for (int i = 1; i < strArr.length; i++) {
        sbf.append(",").append("'").append(strArr[i]).append("'");
      }
    }
    return sbf.toString();
  }

  /**
   * 形成in条件语句
   * @param irt Iterator
   * @param add boolean
   * @return String
   */
  private static String inConditionHelp(Iterator irt, boolean add) {
    StringBuffer sb = new StringBuffer();
    while (irt.hasNext()) {
      sb.append(addQuotation(irt.next().toString(), add)).append(",");
    }

    return sb.toString().length() > 0 ?
        sb.toString().substring(0, sb.toString().length() - 1) : sb.toString();
  }

  /**
   * 形成in的预处理查询条件
   * @param irt Iterator
   * @return String
   */
  private static String inPreparedHelp(Iterator irt) {
    StringBuffer sb = new StringBuffer();
    while (irt.hasNext()) {
      irt.next();
      sb.append("?").append(",");
    }

    return sb.toString().length() > 0 ?
        sb.toString().substring(0, sb.toString().length() - 1) : sb.toString();
  }

  /**
   * 形成查询语句的in()子句
   * @param collection Object
   * @param add boolean
   * @return String
   */
  public static String formInPrepared(Object collection) {
    StringBuffer sb = new StringBuffer();
    Iterator irt = null;
    if (collection instanceof String) {
      sb.append("?");
    }
    else {
      irt = QueryUtil.obj2Iterator(collection);
      sb.append(inPreparedHelp(irt));
    }
    return sb.toString();
  }

  /**
   * 形成查询语句的in()子句
   * @param collection Object
   * @param add boolean
   * @return String
   */
  public static String formInCondition(Object collection, boolean add) {
    StringBuffer sb = new StringBuffer();
    Iterator irt = null;
    if (collection instanceof String) {
      sb.append(addQuotation( (String) collection, add));
    }
    else {
      irt = QueryUtil.obj2Iterator(collection);
      sb.append(inConditionHelp(irt, add));
    }
    return sb.toString();
  }

  /**
   * 增加单引号
   * @param str String
   * @param add boolean
   * @return String
   */
  private static String addQuotation(String str, boolean add) {
    StringBuffer sb = new StringBuffer();
    if (str != null) {
      if (add) {
        sb.append("'").append(str).append("'");
      }
      else {
        sb.append(str);
      }
    }
    else {
      return null;
    }
    return sb.toString();
  }

  /**
   * 查找一个字符串中是否存在指定的字符
   * @param str String
   * @param targetStr String
   * @param noCase boolean
   * @return boolean true 区分大小写，false 不区分大小写
   */
  public static boolean findString(String str, String targetStr, boolean noCase) {

    String temp = str;
    String targetTemp = targetStr;
    if (noCase) {
      temp = str.toLowerCase();
      targetTemp = targetStr.toLowerCase();
    }
    Pattern pattern = Pattern.compile(targetTemp);
    Matcher matcher = pattern.matcher(temp);

    if (matcher.find()) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * 设置路径
   * @param url String
   * @return String
   */
  public static String changeUrl(String url) {
    for (int i = 0; i < url.length(); i++) {
      if (url.charAt(i) == '\\') {
        url = url.replace('\\', '/');
      }
    }
    return url;
  }

  /**
   * 查找子字符串
   * @param mother String
   * @param son String
   * @return boolean
   */
  public static boolean findFixStr(String mother, String son) {
    boolean find = false;
    if (mother != null && son != null &&
        !"".equals(mother) && !"".equals(son)) {
      int pos = mother.indexOf(son);
      if (pos >= 0) {
        find = true;
      }
    }
    return find;
  }

  /**
   * 查找字符
   * @param target String
   * @param ch char
   * @return boolean
   */
  public static boolean strHasChar(String target, char ch) {
    char[] target_char = target.toCharArray();
    for (int i = 0; i < target_char.length; i++) {
      if (ch == target_char[i]) {
        return true;
      }
    }
    return false;
  }

  /**
   * 替换给定一个位置的字符
   * @param target String
   * @param ch char
   * @param position int
   * @return String
   */
  public static String replaceCharWithPosition(String target, char ch,
                                               int position) {
    char[] target_char = target.toCharArray();
    target_char[position] = ch;
    return String.valueOf(target_char);
  }

  /**
   * 用字符串with替换字符串replace
   * @param target String
   * @param replace String
   * @param with String
   * @return String
   */
  public static String repalceString(String target, String replace, String with) {
    StringBuffer sbf = new StringBuffer();
    sbf.append(with).append(target.substring(replace.length()));
    return sbf.toString();
  }

  /**
   * 对 sql server 的 SQL语句拆分
   * @param sql String
   * @return int
   */
  public static int getAfterSelectInsertPoint(String sql) {
    int selectIndex = sql.toLowerCase().indexOf("select");
    final int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");

    return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
  }

  /**
   * 对 sql server 的 SQL语句增加 top 信息，
   * 用以实现分页
   * @param querySelect String
   * @param limit int
   * @return String
   */
  public static String getLimitString(String querySelect, int limit) {
    return new StringBuffer(querySelect.length() + 8)
        .append(querySelect)
        .insert(getAfterSelectInsertPoint(querySelect), " top " + limit).
        toString();
  }

  /**
   * 分解特定的字符串
   * 形如：12.12.12.12, 1234.12434,
   * 121212.12.12.12.12.1.2.12121.212121
   * @param digitString String
   * @return String[]
   */
  public static String[] splitDigitString(String digitString) {
    ArrayList stringList = new ArrayList();
    String patternString = "\\d+([\\.]\\d+)+";
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(digitString);
    while (matcher.find()) {
      stringList.add(matcher.group());
    }

    String[] temp = new String[stringList.size()];
    System.arraycopy(stringList.toArray(), 0, temp, 0, stringList.size());
    return temp;
  }

  /**
   * 删除百分数的百分号
   * @param str String
   * @return String
   */
  public static String removePercent(String str) {
    if (str != null && !"".equals(str) &&
        str.indexOf("%") > 0) {
      return str.substring(0, str.indexOf("%"));
    }
    return str;
  }

  /**
   * 对数字增加前缀0，以达到指定的长度
   * @param num int
   * @param len int
   * @return String
   */
  public static String addPreZero(int num, int len) {
    String temp = String.valueOf(num);
    StringBuffer sb = new StringBuffer();
    if (temp.length() < len) {
      for (int i = 0; i < len - temp.length(); i++) {
        sb.append("0");
      }
    }
    return sb.append(temp).toString();
  }

  /**
   * 这个方法引用自org.apache.struts.util.ResponseUtils
   * Filter the specified string for characters that are sensitive to
   * HTML interpreters, returning the string with these characters replaced
   * by the corresponding character entities.
   * @param value String
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
        case '\n':
          filtered = " ";
          break;
        case '\r':
          filtered = " ";
          break;
        case '\t':
          filtered = " ";
          break;
        case '\'':
          filtered = " ";
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
      }
      else {
        if (filtered == null) {
          result.append(value.charAt(i));
        }
        else {
          result.append(filtered);
        }
      }
    }

    return result == null ? value : result.toString();
  }

  /**
   * 取需要的序列号(复杂)
   * @param list List
   * @return List
   */
  public static List arrangeSequence(List list) {
    if (list == null || list.size() == 0) { //没有任何数据
      return list;
    }
    else {
      int count = 0; //total count
      List result = new ArrayList();
      StringBuffer str = new StringBuffer("0"); //保存ID的序列号
      String orderTrigger = "-1"; //序号标记
      String preTime = null;
      for (Iterator it = list.iterator(); it.hasNext(); ) {
        count++;
        HashMap theMap = (HashMap) it.next();
        String id = (String) theMap.get("id");
        String order = (String) theMap.get("order");

        if (!orderTrigger.equals("-1") && !orderTrigger.equals(order) ||
            list.size() == count) {
          if (list.size() == count) {
            if (orderTrigger.equals(order)) {
              str.append(",").append(id);
            }
            else {
              if (orderTrigger.equals("-1")) {
                str.append(",").append(id);
              }
              HashMap resultMap = new HashMap();
              resultMap.put("IDs", str.toString());
              if (orderTrigger.equals("-1")) {
                resultMap.put("inputTime", (String) theMap.get("time"));
              }
              else {
                resultMap.put("inputTime", preTime);
              }
              result.add(resultMap);
              str = new StringBuffer("0");
            }
            if (!orderTrigger.equals("-1") && !orderTrigger.equals(order)) {
              str.append(",").append(id); //
            }
          }
          if (!orderTrigger.equals("-1")) {
            HashMap resultMap = new HashMap();
            resultMap.put("IDs", str.toString());
            if (list.size() == count) {
              resultMap.put("inputTime", (String) theMap.get("time"));
            }
            else {
              resultMap.put("inputTime", preTime);
            }
            result.add(resultMap);
            str = new StringBuffer("0");
          }
        }
        orderTrigger = order;
        preTime = (String) theMap.get("time");
        str.append(",").append(id);
      }
      return result;
    }
  }

  /**
   * 取字符串序列(简单)
   * @param list List
   * @return List
   */
  public static List fetchSequence(List list) {
    if (list == null || list.size() == 0) {
      return list;
    }
    else {
      List result = new ArrayList();
      String preOrder = "-1";
      for (Iterator it = list.iterator(); it.hasNext(); ) {
        HashMap theMap = (HashMap) it.next();
        if (!preOrder.equals( (String) theMap.get("order"))) {
          result.add( (String) theMap.get("time"));
          preOrder = (String) theMap.get("order");
        }
      }
      return result;
    }
  }
}
