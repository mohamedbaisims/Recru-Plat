package com.example.backend.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private AiService aiService;

    @PostMapping("/analyser")
    public Map<String, Object> analyserCV(@RequestParam("file") MultipartFile file) {

        String text = pdfService.extractText(file);

        return aiService.analyser(text);
    }
}