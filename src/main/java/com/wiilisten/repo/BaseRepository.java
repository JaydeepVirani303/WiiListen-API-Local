 package com.wiilisten.repo;

import java.io.Serializable;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T,ID extends Serializable> extends DataTablesRepository<T, ID> {

}
