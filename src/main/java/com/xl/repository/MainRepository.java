package com.xl.repository;

import org.hibernate.Session;

import java.util.List;

public interface MainRepository {
    Session getSession();
    Object singleQuery(Object [] objects,String hql);
    Object singleQuery(String hql);
    List<Object>  simpleQuery(Object[] objects,String hql);
    List<Object[]> complexQuery(Object[] objects,String hql);
}
