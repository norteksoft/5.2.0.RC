package com.norteksoft.mms.form.format;

import com.norteksoft.mms.form.format.impl.CurrencyChina;
import com.norteksoft.mms.form.format.impl.CurrencyUSA;
import com.norteksoft.mms.form.format.impl.CustomFunction;
import com.norteksoft.mms.form.format.impl.DateChinaMD;
import com.norteksoft.mms.form.format.impl.DateChinaYM;
import com.norteksoft.mms.form.format.impl.DateChinaYMD;
import com.norteksoft.mms.form.format.impl.DateMD;
import com.norteksoft.mms.form.format.impl.DateYM;
import com.norteksoft.mms.form.format.impl.DateYMD;
import com.norteksoft.mms.form.format.impl.Number;
import com.norteksoft.mms.form.format.impl.Percent;
import com.norteksoft.mms.form.format.impl.Time;
import com.norteksoft.mms.form.format.impl.TimeChina;
import com.norteksoft.mms.form.format.impl.TimeChinaHM;
import com.norteksoft.mms.form.format.impl.TimeChinaHMS;
import com.norteksoft.mms.form.format.impl.TimeHM;
import com.norteksoft.mms.form.format.impl.TimeHMS;

public class FormatSettingFactory {
	
	public static FormatSetting getFormatSetting(String format){
		FormatSetting formatSetting=null;
		if(format.indexOf("$#,##")!=-1){
			formatSetting=new CurrencyUSA();
		 }else if(format.indexOf("￥#,##")!=-1){
			 formatSetting=new CurrencyChina();
		 }else if(format.indexOf("%")!=-1){
			 formatSetting=new Percent();
		 }else if(format.equals("yyyy-m-d")){
			 formatSetting=new DateYMD();
		 }else if(format.equals("yyyy-m-d hh:mm:ss")){
			 formatSetting=new Time();
		 }else if(format.equals("yyyy-m")){
			 formatSetting=new DateYM();
		 }else if(format.equals("m-d")){
			 formatSetting=new DateMD();
		 }else if(format.equals("yyyy年m月d日")){
			 formatSetting=new DateChinaYMD();
		 }else if(format.equals("yyyy$年m月d日hh时mm分ss秒")){
			 formatSetting=new TimeChina();
		 }else if(format.equals("yyyy年m月")){
			 formatSetting=new DateChinaYM();
		 }else if(format.equals("m月d日")){
			 formatSetting=new DateChinaMD();
		 }else if(format.equals("h:mm")){
			 formatSetting=new TimeHM();
		 }else if(format.equals("h:mm:ss")){
			 formatSetting=new TimeHMS();
		 }else if(format.equals("h时mm分")){
			 formatSetting=new TimeChinaHM();
		 }else if(format.equals("h时mm分ss秒")){
			 formatSetting=new TimeChinaHMS();
		 }else if(format.indexOf("func:")!=-1){
			 formatSetting=new CustomFunction();
		 }else{
			 formatSetting=new Number();
		 }
		return formatSetting;
	}

}
