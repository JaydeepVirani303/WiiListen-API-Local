package com.wiilisten.service;

import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.response.FileDownloadResponseDto;

import java.io.IOException;
import java.util.List;

public interface ListenerReportService extends BaseService<ListenerProfile, Long> {
    public List<ListenerProfile> getAllListeners();

    public FileDownloadResponseDto getReportOfAllListener() throws IOException;

}
