package com.wiilisten.service;

import com.wiilisten.entity.ListenerProfile;

import java.io.IOException;
import java.util.List;

public interface ListenerReportService extends BaseService<ListenerProfile, Long> {
    public List<ListenerProfile> getAllListeners();

    public byte[] getReportOfAllListener() throws IOException;

}
