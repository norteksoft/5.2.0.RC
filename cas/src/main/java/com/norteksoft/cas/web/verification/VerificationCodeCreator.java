package com.norteksoft.cas.web.verification;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class VerificationCodeCreator implements Controller, InitializingBean {

	private ImageCaptchaService jcaptchaService;

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		String captchaId = request.getSession().getId();
		BufferedImage challenge=jcaptchaService.getImageChallengeForID(captchaId,request.getLocale());
		JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(jpegOutputStream);

		jpegEncoder.encode(challenge);
		byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0L);
		response.setContentType("image/jpeg");
		ServletOutputStream responseOutputStream = response.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
		return null;
	}

	public void afterPropertiesSet() throws Exception {}
	
	public void setJcaptchaService(ImageCaptchaService jcaptchaService) {
		this.jcaptchaService = jcaptchaService;
	}
}
