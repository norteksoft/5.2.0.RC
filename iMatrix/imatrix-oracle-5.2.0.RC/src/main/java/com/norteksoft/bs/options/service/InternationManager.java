package com.norteksoft.bs.options.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.InternationDao;
import com.norteksoft.bs.options.dao.InternationOptionDao;
import com.norteksoft.bs.options.dao.OptionDao;
import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.bs.options.entity.InternationOption;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.MemCachedUtils;

@Service
@Transactional
public class InternationManager {
	@Autowired
	private InternationDao internationDao;
	@Autowired
	private InternationOptionDao internationOptionDao;
	@Autowired
	private OptionDao optionDao;
	public Internation getInternation(Long id){
		return internationDao.get(id);
	}
	
	public void getInternations(Page<Internation> page){
		internationDao.getInternations(page);
	}
	
	public void deleteInternations(String ids){
		String[] idList=ids.split(",");
		for(String id:idList){
			if(StringUtils.isNotEmpty(id)){
				Internation inter=getInternation(Long.parseLong(id));
				internationDao.delete(inter);
				MemCachedUtils.delete(inter.getCompanyId()+"_"+inter.getCode());
			}
		}
	}
	public void save(Internation internation){
		internationDao.save(internation);
	}
	public void saveInternation(Internation internation,String oraginalInterCode){
		//当编码被修改后，删除原缓存中的值
		if(!oraginalInterCode.equals(internation.getCode()))MemCachedUtils.delete(internation.getCompanyId()+"_"+oraginalInterCode);
		internationDao.save(internation);
		List<Object> list=JsonParser.getFormTableDatas(InternationOption.class);
		for(Object obj:list){
			InternationOption inter=(InternationOption)obj;
			inter.setInternation(internation);
			internationOptionDao.save(inter);
		}
	}
	
	public void getInternationOptions(Page<InternationOption> page,Long interId){
		internationOptionDao.getInternationOptions(page, interId);
	}
	 /**
	  * 验证编号是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	public boolean isInternationExist(String code,Long id){
		Internation inter=internationDao.getInternationByCode(code);
		if(inter==null){
			return false;
		}else{
			if(id==null)return true;
			if(inter.getId().equals(id)){
				return false;
			}else{
				return true;
			}
		}
	}
	
	public void initAllInternations(){
		List<Internation> inters= internationDao.getAllInternations();
		for(Internation inter:inters){
			Map<String,String> interOpts=new HashMap<String, String>();
			List<InternationOption> opts=inter.getInternationOptions();
			for(InternationOption opt:opts){
				Option langu=optionDao.get(opt.getCategory());
				interOpts.put(langu.getValue(),opt.getValue());
			}
			MemCachedUtils.add(inter.getCompanyId()+"_"+inter.getCode(),interOpts);
		}
	}
	public List<Internation> getInternations(){
		return internationDao.getInternations();
	}
	public List<InternationOption> getInternationOptions(Long interId){
		return internationOptionDao.getInternationOptions(interId);
	}
	public Internation getInternationByCode(String code){
		return internationDao.getInternationByCode(code);
	}
	public InternationOption getInternationOptionByInfo(Long category,String categoryName,String value,String internationCode){
		return internationOptionDao.getInternationOptionByInfo(category, categoryName,value, internationCode);
	}
	
	public void saveInternationOption(InternationOption interOpt){
		internationOptionDao.save(interOpt);
	}
	
	public void deleteInternationOption(Long inOptionId){
		internationOptionDao.delete(inOptionId);
	}
}
