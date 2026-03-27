package com.eduardo.examen_backend.shared.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.eduardo.examen_backend.shared.exceptions.PdfGenerationException;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfServiceImpl implements PdfService {

    private final Configuration freemarkerConfig;

    public PdfServiceImpl(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public byte[] generarPdfDesdeHtml(String templateName, Map<String, Object> variables) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName + ".ftlh");

            String htmlRenderizado = FreeMarkerTemplateUtils.processTemplateIntoString(template, variables);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            
            renderer.setDocumentFromString(htmlRenderizado);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException("Error al procesar la plantilla o generar el PDF: " + templateName);
        }
    }
}