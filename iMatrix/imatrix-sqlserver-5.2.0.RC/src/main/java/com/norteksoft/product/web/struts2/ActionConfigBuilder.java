package com.norteksoft.product.web.struts2;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.PackageBasedActionConfigBuilder;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class ActionConfigBuilder extends PackageBasedActionConfigBuilder{
	private static final Log LOG = LogFactory.getLog(ActionConfigBuilder.class);
	
	@Inject
	public ActionConfigBuilder(Configuration configuration, Container container, ObjectFactory objectFactory,
            @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
            @Inject("struts.convention.default.parent.package") String defaultParentPackage) {
		super(configuration, container, objectFactory, redirectToSlash,
				defaultParentPackage);
	}

	@Override
	protected List<String> determineActionNamespace(Class<?> actionClass) {
		String className = actionClass.getName();
		String[] arr = className.split("[\\\\.]");
		List<String> namespaces =  super.determineActionNamespace(actionClass);
		for(int i = 0; i < namespaces.size(); i++){
			namespaces.set(i, "/"+arr[2]+namespaces.get(i));
		}
		LOG.debug(" *** 扫描到的Action: ["+className+"], nameSpace:"+namespaces);
		return namespaces;
	}

}
