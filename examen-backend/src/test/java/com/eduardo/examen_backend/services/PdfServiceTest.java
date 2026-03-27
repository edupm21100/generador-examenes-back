package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eduardo.examen_backend.shared.exceptions.PdfGenerationException;
import com.eduardo.examen_backend.shared.services.PdfServiceImpl;

import freemarker.template.Configuration;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @Mock
    private Configuration freemarkerConfig;

    @InjectMocks
    private PdfServiceImpl pdfService;

    @Test
    void generarPdfDesdeHtml_CuandoFallaLaPlantilla_DeberiaLanzarPdfGenerationException() throws Exception {
        when(freemarkerConfig.getTemplate(anyString())).thenThrow(new IOException("Plantilla no encontrada"));

        Map<String, Object> variablesVacias = new HashMap<>();

        assertThrows(PdfGenerationException.class,
                () -> pdfService.generarPdfDesdeHtml("plantilla_inexistente", variablesVacias));
    }
}