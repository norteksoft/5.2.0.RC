package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Message;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class MessageInfoDao extends HibernateDao<Message, Long> {
}
