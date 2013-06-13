package com.norteksoft.acs.web.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jasig.cas.client.session.SessionMappingStorage;

import com.norteksoft.acs.web.filter.SingleSignOutFilter;

public class SingleSignOutHttpSessionListener implements HttpSessionListener {

	private SessionMappingStorage SESSION_MAPPING_STORAGE;
	
    public void sessionCreated(final HttpSessionEvent event) {
        // nothing to do at the moment
    }

    public void sessionDestroyed(final HttpSessionEvent event) {
    	if (SESSION_MAPPING_STORAGE == null) {
    		SESSION_MAPPING_STORAGE = getSessionMappingStorage();
    	}
        final HttpSession session = event.getSession();
              
        SESSION_MAPPING_STORAGE.removeBySessionById(session.getId());
    }

    /**
     * Obtains a {@link SessionMappingStorage} object. Assumes this method will always return the same
     * instance of the object.  It assumes this because it generally lazily calls the method.
     * 
     * @return the SessionMappingStorage
     */
    protected static SessionMappingStorage getSessionMappingStorage() {
    	return SingleSignOutFilter.getSessionMappingStorage();
    }
}
