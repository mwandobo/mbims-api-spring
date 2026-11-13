package com.mwalimubank.mbimsapi.features.exports;

import com.mwalimubank.mbimsapi.features.exports.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/exports")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;


    @GetMapping("/{key}/{format}")
    public void export(
            @PathVariable String key,
            @PathVariable String format,
            @RequestParam(required = false) String filterKey,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            jakarta.servlet.http.HttpServletResponse response
    ) throws java.io.IOException {
        ExportFormatEnum fmt = ExportFormatEnum.from(format);
        byte[] bytes = exportService.export(key, fmt, filterKey, sortBy, sortDirection);
        String filename = key + "-" + LocalDate.now() + "." + fmt.extension;

        response.setContentType(fmt.mediaType.toString());
        response.setContentLength(bytes.length);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(filename).build().toString());
        response.getOutputStream().write(bytes);
        response.flushBuffer();
    }
}