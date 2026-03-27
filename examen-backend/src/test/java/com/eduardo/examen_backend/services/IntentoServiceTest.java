package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.eduardo.examen_backend.examenes.Examen;
import com.eduardo.examen_backend.examenes.ExamenRepository;
import com.eduardo.examen_backend.examenes.intentos.Intento;
import com.eduardo.examen_backend.examenes.intentos.IntentoDTO;
import com.eduardo.examen_backend.examenes.intentos.IntentoRepository;
import com.eduardo.examen_backend.examenes.intentos.IntentoServiceImpl;
import com.eduardo.examen_backend.examenes.intentos.RespuestaAlumnoDTO;
import com.eduardo.examen_backend.examenes.opciones.Opcion;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.shared.exceptions.BadRequestException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.shared.services.PdfService;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class IntentoServiceTest {

    @Mock
    private IntentoRepository intentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ExamenRepository examenRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private IntentoServiceImpl intentoService;

    @Mock
    private PdfService pdfService;

    @Test
    void realizarExamenTestOK_CalculoNotaCorrecto() {
        String correoLogueado = "alumno@test.com";

        // 1. Preparar Alumno
        Usuario alumno = new Usuario();
        alumno.setIdUsuario(1);
        alumno.setCorreoUsuario(correoLogueado);

        // 2. Preparar Examen y Preguntas (2 preguntas, cada una vale 5 puntos)
        Opcion op1Correcta = Opcion.builder().idOpcion(10).esCorrecta(true).build();
        Opcion op2Incorrecta = Opcion.builder().idOpcion(11).esCorrecta(false).build();
        Pregunta p1 = Pregunta.builder().idPregunta(1).activo(true).opciones(List.of(op1Correcta, op2Incorrecta)).build();

        Opcion op3Correcta = Opcion.builder().idOpcion(20).esCorrecta(true).build();
        Opcion op4Incorrecta = Opcion.builder().idOpcion(21).esCorrecta(false).build();
        Pregunta p2 = Pregunta.builder().idPregunta(2).activo(true).opciones(List.of(op3Correcta, op4Incorrecta)).build();

        Examen examenActivo = Examen.builder().idExamen(100).activo(true).preguntas(Set.of(p1, p2)).build();

        // 3. Preparar el JSON de entrada (Acierta la P1, falla la P2 -> Nota esperada: 5.0)
        RespuestaAlumnoDTO res1 = new RespuestaAlumnoDTO();
        res1.setIdPregunta(1);
        res1.setIdOpcionSeleccionada(10);

        RespuestaAlumnoDTO res2 = new RespuestaAlumnoDTO();
        res2.setIdPregunta(2);
        res2.setIdOpcionSeleccionada(21);

        IntentoDTO dtoEntrada = new IntentoDTO();
        dtoEntrada.setIdExamen(100);
        dtoEntrada.setRespuestas(List.of(res1, res2));

        // 4. Mocks
        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(alumno));
        when(examenRepository.findById(100)).thenReturn(Optional.of(examenActivo));
        
        ArgumentCaptor<Intento> captor = ArgumentCaptor.forClass(Intento.class);
        when(intentoRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        
        IntentoDTO dtoSalida = new IntentoDTO();
        dtoSalida.setNota(5.0);

        // 5. Ejecutar
        IntentoDTO resultado = intentoService.realizarExamen(correoLogueado, dtoEntrada);

        // 6. Validar
        assertNotNull(resultado);
        assertEquals(5.0, captor.getValue().getNota(), "La nota matemática debería ser exactamente 5.0");
        verify(intentoRepository, times(1)).save(any(Intento.class));
    }

    @Test
    void realizarExamenInactivoMAL() {
        String correoLogueado = "alumno@test.com";
        Usuario alumno = new Usuario();

        Examen examenInactivo = Examen.builder().idExamen(100).activo(false).build(); // 👈 Inactivo

        IntentoDTO dtoEntrada = new IntentoDTO();
        dtoEntrada.setIdExamen(100);

        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(alumno));
        when(examenRepository.findById(100)).thenReturn(Optional.of(examenInactivo));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            intentoService.realizarExamen(correoLogueado, dtoEntrada);
        });

        assertEquals("El examen no está activo actualmente.", excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void realizarExamenTrampaDuplicadosMAL() {
        String correoLogueado = "alumno@test.com";
        Usuario alumno = new Usuario();

        Pregunta p1 = Pregunta.builder().idPregunta(1).activo(true).build();
        Examen examenActivo = Examen.builder().idExamen(100).activo(true).preguntas(Set.of(p1)).build();

        RespuestaAlumnoDTO res1 = new RespuestaAlumnoDTO(); res1.setIdPregunta(1);
        RespuestaAlumnoDTO res2 = new RespuestaAlumnoDTO(); res2.setIdPregunta(1);

        IntentoDTO dtoEntrada = new IntentoDTO();
        dtoEntrada.setIdExamen(100);
        dtoEntrada.setRespuestas(List.of(res1, res2));

        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(alumno));
        when(examenRepository.findById(100)).thenReturn(Optional.of(examenActivo));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            intentoService.realizarExamen(correoLogueado, dtoEntrada);
        });

        assertEquals("Intento de fraude detectado: Se ha enviado más de una respuesta para la pregunta ID 1", excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void realizarExamenUsuarioInexistenteMAL() {
        IntentoDTO dtoEntrada = new IntentoDTO();
        when(usuarioRepository.findByCorreoUsuario("fantasma@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            intentoService.realizarExamen("fantasma@test.com", dtoEntrada);
        });
        verify(examenRepository, never()).findById(any());
    }

    @Test
    void generarReporteIntentoPdf_ConPermisos_DeberiaLlamarAPdfService() {
        Usuario alumno = new Usuario();
        alumno.setCorreoUsuario("alumno@test.com");
        
        Intento intento = new Intento();
        intento.setUsuario(alumno);
        
        when(intentoRepository.findById(1)).thenReturn(Optional.of(intento));
        when(usuarioRepository.findByCorreoUsuario("alumno@test.com")).thenReturn(Optional.of(alumno));
        when(pdfService.generarPdfDesdeHtml(eq("reporte_intento"), anyMap())).thenReturn("PDF".getBytes());

        byte[] resultado = intentoService.generarReporteIntentoPdf(1, "alumno@test.com");

        assertNotNull(resultado);
        verify(pdfService).generarPdfDesdeHtml(eq("reporte_intento"), anyMap());
    }
}