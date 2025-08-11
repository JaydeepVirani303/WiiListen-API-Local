package com.wiilisten.service;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.response.FileDownloadResponseDto;

import java.io.IOException;
import java.util.List;

public interface CallerReportService extends BaseService<CallerProfile, Long> {
    public List<CallerProfile> getAllCallers();

    public FileDownloadResponseDto getReportOfAllCaller() throws IOException;
}
