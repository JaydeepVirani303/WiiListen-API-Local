package com.wiilisten.service;

import com.wiilisten.entity.CallerProfile;

import java.util.zip.ZipOutputStream;

public interface DownloadZipFileService extends BaseService<CallerProfile, Long> {
    public void processFileForZip(ZipOutputStream zos, String fileUrl, String filePrefix, Long userId);

    public byte[] downloadFileFromUrl(String fileUrl);

    public String getFileExtension(String fileUrl);

}
