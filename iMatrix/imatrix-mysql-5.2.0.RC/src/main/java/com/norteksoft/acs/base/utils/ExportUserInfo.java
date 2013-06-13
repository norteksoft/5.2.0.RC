package com.norteksoft.acs.base.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;

public class ExportUserInfo {
	
	private static final Log logger = LogFactory.getLog(ExportUserInfo.class);
	
	public static void exportUser(OutputStream fileOut, List<Department> depts, Long companyId){
	HSSFWorkbook wb;
    try
    {
		wb = new HSSFWorkbook();
    	HSSFSheet sheet=wb.createSheet("user-info");
        
        HSSFFont boldFont = wb.createFont();
        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);
        
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell0 = row.createCell(0);
        cell0.setCellValue("部门");
        cell0.setCellStyle(boldStyle);
        HSSFCell cell1 = row.createCell(1);
        cell1.setCellValue("姓名");
        cell1.setCellStyle(boldStyle);
        HSSFCell cell2 = row.createCell(2);
        cell2.setCellValue("登录名");
        cell2.setCellStyle(boldStyle);
        HSSFCell cell3 = row.createCell(3);
        cell3.setCellValue("电话");
        cell3.setCellStyle(boldStyle);
        HSSFCell cell4 = row.createCell(4);
        cell4.setCellValue("性别");
        cell4.setCellStyle(boldStyle);
        HSSFCell cell5 = row.createCell(5);
        cell5.setCellValue("电邮");
        cell5.setCellStyle(boldStyle);
        HSSFCell cell6 = row.createCell(6);
        cell6.setCellValue("权重");
        cell6.setCellStyle(boldStyle);
        HSSFCell cell7 = row.createCell(7);
        cell7.setCellValue("邮件大小(M)");
        cell7.setCellStyle(boldStyle);
        HSSFCell cell8 = row.createCell(8);
        cell8.setCellValue("密级");
        cell8.setCellStyle(boldStyle);
        HSSFCell cell9 = row.createCell(9);
        cell9.setCellValue("邮箱配置");
        cell9.setCellStyle(boldStyle);
        //导出部门和人员信息
        for(int i=0;i<depts.size();i++){
        	List<User> users=ApiFactory.getAcsService().getUsersByDepartmentId(depts.get(i).getId());
        	fillCell(depts.get(i),users,sheet);
        }
        //导出无部门人员
        List<User> users=ApiFactory.getAcsService().getUsersWithoutDepartment();
        fillCell(null,users,sheet);
        wb.write(fileOut);
    }catch(IOException exception){
    	logger.debug(exception.getStackTrace());
	} 
}

private static void fillCell(Department dept,List<User> users,HSSFSheet sheet){
	String deptName = "";
	if(dept!=null){
		//处理部门名称,如:办公室/后勤
		deptName=dept.getName();
		while(dept.getParent()!=null){
			dept=dept.getParent();
			deptName=dept.getName()+"/"+deptName;
		}
	}
	for(User user:users){
		if(user.getLoginName().contains(".systemAdmin")||
				user.getLoginName().contains(".securityAdmin")||
				user.getLoginName().contains(".auditAdmin")) continue;
		HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
		HSSFCell celli0 = rowi.createCell(0);
		celli0.setCellValue(deptName);
        HSSFCell celli1 = rowi.createCell(1);
       celli1.setCellValue(user.getName());
        HSSFCell celli2 = rowi.createCell(2);
        celli2.setCellValue(user.getLoginName());
        HSSFCell celli3 = rowi.createCell(3);
        if(user.getTelephone()==null){celli3.setCellValue("");}else{celli3.setCellValue(user.getTelephone());}
        HSSFCell celli4 = rowi.createCell(4);
        if(user.getSex()==null){celli4.setCellValue("");}else{celli4.setCellValue(user.getSex()?"男":"女");}
        HSSFCell celli5 = rowi.createCell(5);
        if(user.getEmail()==null){celli5.setCellValue("");}else{celli5.setCellValue(user.getEmail());}
        HSSFCell celli6 = rowi.createCell(6);
        if(user.getWeight()==null){celli6.setCellValue("");}else{celli6.setCellValue(user.getWeight());}
        HSSFCell celli7 = rowi.createCell(7);
        if(user.getMailSize()==null){celli7.setCellValue("");}else{celli7.setCellValue(user.getMailSize());}
        HSSFCell celli8 = rowi.createCell(8);
        if(user.getSecretGrade()==null){celli8.setCellValue("一般");}else{celli8.setCellValue(getGrade(user.getSecretGrade()));}
        HSSFCell celli9 = rowi.createCell(9);
        if(user.getMailboxDeploy()==null){celli9.setCellValue("");}else{celli9.setCellValue(getDeploy(user.getMailboxDeploy()));}
	}
}

private static String getDeploy(MailboxDeploy deploy){
	switch (deploy) {
	case INSIDE:
		return "内网";
	case EXTERIOR:
		return "外网";
	default:
		return "";
	}
}

private static String getGrade(SecretGrade grade){
	switch (grade) {
	case COMMON:
		return "一般";
	case CENTRE:
		return "核心";
	case MAJOR:
		return "重要";
	default:
		return "一般";
	}
}

}
