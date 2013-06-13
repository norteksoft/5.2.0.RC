package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.StickyNote;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class StickyNoteDao extends HibernateDao<StickyNote, Long> {
	public StickyNote getStickyNoteById(Long userId, Long companyId) {
		return this.findUnique("from StickyNote sn where sn.userId=? and sn.companyId=?",userId, companyId);
	}
}
