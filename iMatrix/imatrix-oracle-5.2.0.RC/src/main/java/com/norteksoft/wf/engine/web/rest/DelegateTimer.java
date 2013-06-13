package com.norteksoft.wf.engine.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.norteksoft.wf.base.utils.TimerUtils;

@Component
@Path("/wf")
public class DelegateTimer {

	@Autowired
	private TimerUtils timerUtils;
	
	@POST
	@Path("/delegate")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response userAuthentication(@FormParam("runAsUser")String identity) {
		try {
			synchronized(this){
				timerUtils.run();
			}
		} catch (Exception e) {
			return Response.status(201).entity(e.getMessage()).build();
		}
		return Response.status(201).entity(" wf timer ok ").build();
	}
}
