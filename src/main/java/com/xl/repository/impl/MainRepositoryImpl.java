package com.xl.repository.impl;

import com.xl.repository.MainRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class MainRepositoryImpl implements MainRepository{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Session getSession() {
        return this.sessionFactory.openSession();
    }

    @Override
    public Object singleQuery(Object[] objects, String hql) {
        Query query = getSession().createQuery(hql);
        for (int i = 0; i < objects.length; i++) {
            query.setParameter(i,objects[i]);
        }
        return query.uniqueResult();
    }

    @Override
    public Object singleQuery(String hql) {
        return getSession().createQuery(hql).uniqueResult();
    }

    @Override
    public List<Object> simpleQuery(Object[] objects, String hql) {
        Query query = getSession().createQuery(hql);
        for (int i = 0; i < objects.length; i++) {
            query.setParameter(i,objects[i]);
        }
        return query.list();
    }

    @Override
    public List<Object[]> complexQuery(Object[] objects, String hql) {
        hql ="select count (*) from THngyWorkTask ";
        Query query = getSession().createQuery(hql);
        for (int i = 0; i < objects.length; i++) {
            query.setParameter(i,objects[i]);
        }
        return query.list();
    }
}
