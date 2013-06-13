package com.norteksoft.product.util;


public class ThreadParameters{
		private Long userId;
		private Long companyId;
		private Long pageSize;
		private Integer pageNumber;
		private String companyCode;
		private String userName;
		private String companyName;
		private String password;
		private String honorificTitle;
		private String loginName;
		private Long systemId;
		
		public ThreadParameters() {
			super();
		}
		
		public ThreadParameters(Long companyId) {
			super();
			this.companyId = companyId;
		}
		
		public ThreadParameters(String loginName) {
			super();
			this.loginName = loginName;
		}
		
		public ThreadParameters(Long companyId,Long userId) {
			super();
			this.companyId = companyId;
			this.userId = userId;
		}

		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public Long getCompanyId() {
			return companyId;
		}
		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}
		public Long getPageSize() {
			return pageSize;
		}
		public void setPageSize(Long pageSize) {
			this.pageSize = pageSize;
		}
		public Integer getPageNumber() {
			return pageNumber;
		}
		public void setPageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getCompanyCode() {
			return companyCode;
		}

		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getCompanyName() {
			return companyName;
		}

		public String getPassword() {
			return password;
		}

		public String getHonorificTitle() {
			return honorificTitle;
		}

		public String getLoginName() {
			return loginName;
		}
		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}

		public Long getSystemId() {
			return systemId;
		}

		public void setSystemId(Long systemId) {
			this.systemId = systemId;
		}
		
	}