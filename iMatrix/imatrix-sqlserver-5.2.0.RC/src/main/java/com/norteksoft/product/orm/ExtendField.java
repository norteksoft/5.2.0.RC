package com.norteksoft.product.orm;

import java.util.Date;

import javax.persistence.Embeddable;

/**
 * 默认的备用字段 
 * <br><code>String</code>字段10个
 * <br><code>Date</code>字段5个
 * <br><code>Double</code>字段5个
 * @author wurong
 *
 */
@Embeddable
public class ExtendField {

	protected String string1;
	protected String string2;
	protected String string3;
	protected String string4;
	protected String string5;

	protected Date date1;
	protected Date date2;

	protected Double double1;
	protected Double double2;
	protected Double double3;

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String getString2() {
		return string2;
	}

	public void setString2(String string2) {
		this.string2 = string2;
	}

	public String getString3() {
		return string3;
	}

	public void setString3(String string3) {
		this.string3 = string3;
	}

	public String getString4() {
		return string4;
	}

	public void setString4(String string4) {
		this.string4 = string4;
	}

	public String getString5() {
		return string5;
	}

	public void setString5(String string5) {
		this.string5 = string5;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Double getDouble1() {
		return double1;
	}

	public void setDouble1(Double double1) {
		this.double1 = double1;
	}

	public Double getDouble2() {
		return double2;
	}

	public void setDouble2(Double double2) {
		this.double2 = double2;
	}

	public Double getDouble3() {
		return double3;
	}

	public void setDouble3(Double double3) {
		this.double3 = double3;
	}

}
