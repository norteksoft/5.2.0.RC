package com.norteksoft.acs.web.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;

/**
 * Implements the Single Sign Out protocol. It handles registering the session
 * and destroying the session.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public final class SingleSignOutFilter extends AbstractConfigurationFilter {

	/**
	 * The name of the artifact parameter. This is used to capture the session
	 * identifier.
	 */
	private String artifactParameterName = "ticket";

//	private static SessionMappingStorage SESSION_MAPPING_STORAGE = new HashMapBackedSessionMappingStorage();
	private static SessionMappingStorage SESSION_MAPPING_STORAGE = new SessionStorage();
	private static Log log = LogFactory.getLog(SingleSignOutFilter.class);

	public void init(final FilterConfig filterConfig) throws ServletException {
		if (!isIgnoreInitConfiguration()) {
			setArtifactParameterName(getPropertyFromInitParams(filterConfig, "artifactParameterName", "ticket"));
		}
		init();
	}

	public void init() {
		CommonUtils.assertNotNull(this.artifactParameterName, "artifactParameterName cannot be null.");
		CommonUtils.assertNotNull(SESSION_MAPPING_STORAGE, "sessionMappingStorage cannote be null.");
	}

	public void setArtifactParameterName(final String artifactParameterName) {
		this.artifactParameterName = artifactParameterName;
	}

	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;

		final String logoutRequest = request.getParameter("logoutRequest");

		if (CommonUtils.isNotBlank(logoutRequest)) {

			if (log.isTraceEnabled()) {
				log.trace("Logout request=[" + logoutRequest + "]");
			}

			final String sessionIdentifier = XmlUtils.getTextForElement(logoutRequest, "SessionIndex");

			if (CommonUtils.isNotBlank(sessionIdentifier)) {
				final HttpSession session = SESSION_MAPPING_STORAGE.removeSessionByMappingId(sessionIdentifier);

				if (session != null) {
					String sessionID = session.getId();

					if (log.isDebugEnabled()) {
						log.debug("Invalidating session [" + sessionID
								+ "] for ST [" + sessionIdentifier + "]");
					}

					try {
						session.invalidate();
					} catch (final IllegalStateException e) {
						log.debug(e, e);
					}
				}
				return;
			}
		} else {
			final String artifact = request.getParameter(this.artifactParameterName);
			final HttpSession session = request.getSession();

			if (log.isDebugEnabled() && session != null) {
				log.debug("Storing session identifier for " + session.getId());
			}
			if (CommonUtils.isNotBlank(artifact)) {
				try {
					SESSION_MAPPING_STORAGE.removeBySessionById(session.getId());
				} catch (final Exception e) {
					// ignore if the session is already marked as invalid.
					// Nothing we can do!
				}
				SESSION_MAPPING_STORAGE.addSessionById(artifact, session);
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void setSessionMappingStorage(final SessionMappingStorage storage) {
		SESSION_MAPPING_STORAGE = storage;
	}

	public static SessionMappingStorage getSessionMappingStorage() {
		return SESSION_MAPPING_STORAGE;
	}
	
	public void destroy() {  }
	
	final static class SessionStorage implements SessionMappingStorage {
		
	    private final Map<String, HttpSession> MANAGED_SESSIONS = new HashMap<String, HttpSession>();
	    private final Map<String, String> ID_TO_SESSION_KEY_MAPPING = new HashMap<String, String>();

	    private final Log log = LogFactory.getLog(getClass());

		public synchronized void addSessionById(String mappingId, HttpSession session) {
	        ID_TO_SESSION_KEY_MAPPING.put(session.getId(), mappingId);
	        MANAGED_SESSIONS.put(mappingId, session);

		}
		
		protected synchronized String getSTBySessionId(String sessionId){
			return ID_TO_SESSION_KEY_MAPPING.get(sessionId);
		}

		public synchronized void removeBySessionById(String sessionId) {
	        if (log.isDebugEnabled()) {
	            log.debug("Attempting to remove Session=[" + sessionId + "]");
	        }

	        final String key = ID_TO_SESSION_KEY_MAPPING.get(sessionId);

	        if (log.isDebugEnabled()) {
	            if (key != null) {
	                log.debug("Found mapping for session.  Session Removed.");
	            } else {
	                log.debug("No mapping for session found.  Ignoring.");
	            }
	        }
	        MANAGED_SESSIONS.remove(key);
	        ID_TO_SESSION_KEY_MAPPING.remove(sessionId);
		}

		public synchronized HttpSession removeSessionByMappingId(String mappingId) {
			final HttpSession session = MANAGED_SESSIONS.get(mappingId);
	        if (session != null) {
	        	removeBySessionById(session.getId());
	        }
	        return session;
		}
		
		Collection<HttpSession> values(){
			return MANAGED_SESSIONS.values();
		}
	}
}
