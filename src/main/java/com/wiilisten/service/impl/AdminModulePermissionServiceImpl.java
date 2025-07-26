package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.AdminModulePermission;
import com.wiilisten.entity.Administration;
import com.wiilisten.service.AdminModulePermissionService;

import jakarta.annotation.PostConstruct;

@Repository
public class AdminModulePermissionServiceImpl extends BaseServiceImpl<AdminModulePermission, Long>
		implements AdminModulePermissionService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getAdminModulePermissionRepository();
	}

	@Override
	public Page<AdminModulePermission> findByActiveTrue(Pageable pageable) {
		return getDaoFactory().getAdminModulePermissionRepository().findByActiveTrue(pageable);
	}

	@Override
	public AdminModulePermission findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getAdminModulePermissionRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public Boolean existsByAdministrationAndCanAddTrue(Administration administration) {
		return getDaoFactory().getAdminModulePermissionRepository().existsByAdministrationAndCanAddTrue(administration);
	}

	@Override
	public Boolean existsByAdministrationAndCanUpdateTrue(Administration administration) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndCanUpdateTrue(administration);
	}

	@Override
	public Boolean existsByAdministrationAndCanViewTrue(Administration administration) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndCanViewTrue(administration);
	}

	@Override
	public Boolean existsByAdministrationAndCanDeleteTrue(Administration administration) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndCanDeleteTrue(administration);
	}

	@Override
	public List<AdminModulePermission> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getAdminModulePermissionRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public List<AdminModulePermission> findByAdministrationIdAndActiveTrueOrderByIdDesc(Long id) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.findByAdministrationIdAndActiveTrueOrderByIdDesc(id);
	}

	@Override
	public AdminModulePermission findByAdministrationAndAdminModuleIdAndActiveTrue(Administration administration,
			Long id) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.findByAdministrationAndAdminModuleIdAndActiveTrue(administration, id);
	}

	@Override
	public Boolean existsByAdministrationAndAdminModuleIdAndCanAddTrue(Administration administration, Long id) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndAdminModuleIdAndCanAddTrue(administration, id);
	}

	@Override
	public Boolean existsByAdministrationAndAdminModuleIdAndCanUpdateTrue(Administration administration, Long id) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndAdminModuleIdAndCanUpdateTrue(administration, id);
	}

	@Override
	public Boolean existsByAdministrationAndAdminModuleIdAndCanViewTrue(Administration administration, Long id) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndAdminModuleIdAndCanViewTrue(administration, id);
	}

	@Override
	public Boolean existsByAdministrationAndAdminModuleIdAndCanDeleteTrue(Administration administration, Long id) {
		return getDaoFactory().getAdminModulePermissionRepository()
				.existsByAdministrationAndAdminModuleIdAndCanDeleteTrue(administration, id);
	}

	@Override
	public List<AdminModulePermission> findByAdministrationAndActiveTrueOrderByIdDesc(Administration administration) {
		return getDaoFactory().getAdminModulePermissionRepository().findByAdministrationAndActiveTrueOrderByIdDesc(administration);
	}

}
