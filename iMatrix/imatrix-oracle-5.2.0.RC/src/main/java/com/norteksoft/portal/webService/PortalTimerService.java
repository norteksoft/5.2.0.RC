package com.norteksoft.portal.webService;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.norteksoft.portal.entity.Message;
import com.norteksoft.portal.service.MessageInfoManager;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Component//spring注入
@Path("/portal")
public class PortalTimerService{
	@Autowired
	private MessageInfoManager messageManager;
	
	@POST
	@Path("/setMessageState")
	@Produces("text/plain;charset=UTF-8")
	@Consumes("text/plain;charset=UTF-8")
	public Response setMessageState(@FormParam("messageId")String messageId)throws Exception{
		Message message= messageManager.getMessage(Long.valueOf(messageId));
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(message.getCompanyId());
		parameters.setLoginName(message.getCreator());
		parameters.setUserName(message.getCreatorName());
		ParameterUtils.setParameters(parameters);
		messageManager.setMessageState(message,false);
		return Response.status(200).entity("ok").build();
	}
}
