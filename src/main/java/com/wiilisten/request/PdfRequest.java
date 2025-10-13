package com.wiilisten.request;

import lombok.Data;

@Data
public class PdfRequest {
    private String fileUrl;
    private String password;
}
