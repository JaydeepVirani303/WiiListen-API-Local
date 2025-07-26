package com.wiilisten.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.AdminModulePermission;
import com.wiilisten.entity.Administration;

public interface AdminModulePermissionService extends BaseService<AdminModulePermission, Long> {

	Page<AdminModulePermission> findByActiveTrue(Pageable pageable);

	List<AdminModulePermission> findByAdministrationIdAndActiveTrueOrderByIdDesc(Long id);

	AdminModulePermission findByIdAndActiveTrue(Long id);

	Boolean existsByAdministrationAndCanAddTrue(Administration administration);

	Boolean existsByAdministrationAndCanUpdateTrue(Administration administration);

	Boolean existsByAdministrationAndCanViewTrue(Administration administration);

	Boolean existsByAdministrationAndCanDeleteTrue(Administration administration);

	List<AdminModulePermission> findByActiveTrueOrderByIdDesc();

	AdminModulePermission findByAdministrationAndAdminModuleIdAndActiveTrue(Administration administration, Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanAddTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanUpdateTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanViewTrue(Administration administration,Long id);

	Boolean existsByAdministrationAndAdminModuleIdAndCanDeleteTrue(Administration administration,Long id);
	
	List<AdminModulePermission> findByAdministrationAndActiveTrueOrderByIdDesc(Administration administration);

}
