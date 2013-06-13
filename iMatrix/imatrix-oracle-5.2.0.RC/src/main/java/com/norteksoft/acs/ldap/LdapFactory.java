package com.norteksoft.acs.ldap;

import com.norteksoft.acs.entity.sysSetting.LdapType;
import com.norteksoft.acs.ldap.impl.ApacheDsService;
import com.norteksoft.acs.ldap.impl.DominoService;
import com.norteksoft.acs.ldap.impl.WindowsAdService;

public class LdapFactory {

	public static LdapService getLdapService(LdapType type, String username, String password, String url){
		switch (type) {
		case APACHE: return new ApacheDsService("uid="+username+",ou=system", password, url);
		case DOMINO: return new DominoService("cn="+username, password, url);
		case WINDOWS_AD: return new WindowsAdService(username, password, url);
		}
		return null;
	}
	
}
