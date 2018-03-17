package com.xl.repository.impl;

import com.xl.entity.THngyAdminInfo;
import com.xl.repository.AdminRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRepositoryImpl implements AdminRepository {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return this.sessionFactory.openSession();
    }

    @Override
    public THngyAdminInfo load(Long id) {
        return getSession().get(THngyAdminInfo.class,id);
    }

    @Override
    public THngyAdminInfo get(Long id) {
        return  getSession().get(THngyAdminInfo.class,id);
    }

    @Override
    public List<THngyAdminInfo> findAll() {
        return null;
    }

    @Override
    public void persist(THngyAdminInfo entity) {

    }

    @Override
    public Long save(THngyAdminInfo entity) {
        return (long)getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(THngyAdminInfo entity) {
        getSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void flush() {
        getSession().flush();
    }
}
