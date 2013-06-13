package ${packageName}.dao;
import ${entityPath};

import java.util.List;
import org.springframework.stereotype.Repository;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ${entityName}Dao extends HibernateDao<${entityName}, Long> {
		
	public Page<${entityName}> list(Page<${entityName}> page){
		return findPage(page, "from ${entityName} t");
	}
	
	public List<${entityName}> getAll${entityName}(){
		return find("from ${entityName} t");
	}

    public Page<${entityName}> search(Page<${entityName}> page) {
        return searchPageByHql(page, "from ${entityName} t ");
    }
}
