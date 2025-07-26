package com.wiilisten.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.wiilisten.repo.BaseRepository;
import com.wiilisten.service.BaseService;
import com.wiilisten.utils.DaoFactory;

import lombok.Getter;

@Getter
public class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {
	
	@Autowired
    private DaoFactory daoFactory;

//    @Autowired
//    private ServiceRegistry serviceRegistry;

	protected BaseRepository<T, ID> baseRepository;

    @Override
    public void delete(T t) {
        baseRepository.delete(t);
    }

    @Override
    public void deleteById(ID id) {
        baseRepository.deleteById(id);
    }

    @Override
    public List<T> findAll() {
        return (List<T>) baseRepository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return (List<T>) baseRepository.findAll(sort);
    }

    @Override
    public T saveORupdate(T t) {
    	return baseRepository.save(t);
    }

    @Override
    public T findOne(ID id) {
        Optional<T> findById = baseRepository.findById(id);
        return findById.isPresent() ? findById.get() : null;
    }
	

}
