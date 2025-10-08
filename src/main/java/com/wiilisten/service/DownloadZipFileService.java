package com.wiilisten.service;

import com.wiilisten.entity.CallerProfile;

import java.util.zip.ZipOutputStream;

public interface DownloadZipFileService extends BaseService<CallerProfile, Long> {
    public void addFileToZip(ZipOutputStream zos, String filePath, String fileType,
                             Long userId, String firstName, String lastName);

    public String sanitizeName(String name);

    public String getFileExtension(String fileName);

}
