package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.AdminModulePermission;
import com.wiilisten.entity.Administration;

@Repository
public interface AdminModulePermissionRepository extends BaseRepository<AdminModulePermission, Long> {

	Page<AdminModulePermission> findByActiveTrue(Pageable pageable);
	
	List<AdminModulePermission> findByAdministrationIdAndActiveTrueOrderByIdDesc(Long id);

	AdminModulePermission findByIdAndActiveTrue(Long id);
	
	AdminModulePermission findByAdministrationAndAdminModuleIdAndActiveTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndCanAddTrue(Administration administration);

	Boolean existsByAdministrationAndCanUpdateTrue(Administration administration);

	Boolean existsByAdministrationAndCanViewTrue(Administration administration);

	Boolean existsByAdministrationAndCanDeleteTrue(Administration administration);
	
	List<AdminModulePermission> findByActiveTrueOrderByIdDesc();
	
	Boolean existsByAdministrationAndAdminModuleIdAndCanAddTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanUpdateTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanViewTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanDeleteTrue(Administration administration,Long id);
	
	List<AdminModulePermission> findByAdministrationAndActiveTrueOrderByIdDesc(Administration administration);

}
