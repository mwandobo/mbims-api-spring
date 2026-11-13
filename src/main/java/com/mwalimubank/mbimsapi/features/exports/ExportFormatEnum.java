package com.mwalimubank.mbimsapi.features.exports;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

public enum ExportFormatEnum {
    CSV("csv", MediaType.parseMediaType("text/csv;charset=UTF-8")),
    XLSX("xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    PDF("pdf", MediaType.APPLICATION_PDF);

    public final String extension;
    public final MediaType mediaType;

    ExportFormatEnum(String extension, MediaType mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public static ExportFormatEnum from(String value) {
        for (ExportFormatEnum f : values()) {
            if (f.extension.equalsIgnoreCase(value)) return f;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported export format: " + value);
    }
}
