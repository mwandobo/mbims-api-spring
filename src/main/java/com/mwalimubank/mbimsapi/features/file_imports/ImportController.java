package com.mwalimubank.mbimsapi.features.file_imports;

import com.mwalimubank.mbimsapi.features.exports.ExportFormatEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping(value = "/{key}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportService.ImportResult> importFile(
            @PathVariable String key,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean dryRun
    ) {
        ImportService.ImportResult result = importService.importFile(key, file, dryRun);
        // 422 when any row failed, so the client can show the error list
        HttpStatus status = result.errors().isEmpty() ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(result);
    }

    @GetMapping("/{key}/template")
    public ResponseEntity<byte[]> template(@PathVariable String key,
                                           @RequestParam(defaultValue = "xlsx") String format) {
        ExportFormatEnum fmt = ExportFormatEnum.from(format);
        if (fmt == ExportFormatEnum.PDF) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Template format must be csv or xlsx");
        }
        byte[] bytes = importService.template(key, fmt == ExportFormatEnum.XLSX);

        return ResponseEntity.ok()
                .contentType(fmt.mediaType)
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(key + "-template." + fmt.extension).build().toString())
                .body(bytes);
    }
}