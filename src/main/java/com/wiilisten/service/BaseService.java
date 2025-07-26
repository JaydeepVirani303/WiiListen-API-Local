package com.wiilisten.service;

import org.springframework.data.domain.Sort;
import java.io.Serializable;
import java.util.List;

public interface BaseService<T, ID extends Serializable> {
	
	/**
     * This <code>delete</code> method is used for delete single record.
     *
     * @param <<T>>
     * @param t
     * @return
     */
    void delete(T t);

    /**
     * This <code>deleteById</code> method is used for delete record by id.
     *
     * @param id
     * @return
     */
    void deleteById(ID id);

    /**
     * This <code>getAll</code> method is used for get all records.
     *
     * @param <<T>>
     * @return
     */
    List<T> findAll();

    /**
     * This <code>getAll</code> method is used for get all records with sorting.
     *
     * @param <<T>>
     * @param sort
     * @return
     */
    List<T> findAll(Sort sort);

    /**
     * This <code>findOne</code> method is used for find single record by id.
     *
     * @param <<T>>
     * @param long1
     * @return
     */
    T findOne(ID id);

    /**
     * This <code>update</code> method is used for update record.
     *
     * @param <<T>>
     * @param t
     * @return
     */
    T saveORupdate(T t);

}
