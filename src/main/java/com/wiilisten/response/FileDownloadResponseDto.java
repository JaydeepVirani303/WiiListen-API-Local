package com.wiilisten.response;

import lombok.*;

@Getter
@Builder
@Setter
@Data
@NoArgsConstructor
public class FileDownloadResponseDto {

    private String fileName;
    private String downloadTime;
    private String message;

    public FileDownloadResponseDto(String fileName, String downloadTime, String message) {
        this.fileName = fileName;
        this.downloadTime = downloadTime;
        this.message = message;
    }

}
