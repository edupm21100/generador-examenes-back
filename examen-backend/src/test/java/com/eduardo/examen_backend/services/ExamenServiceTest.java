package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import com.eduardo.examen_backend.examenes.Examen;
import com.eduardo.examen_backend.examenes.ExamenDTO;
import com.eduardo.examen_backend.examenes.ExamenRepository;
import com.eduardo.examen_backend.examenes.ExamenServiceImpl;
import com.eduardo.examen_backend.examenes.categorias.Categoria;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaRepository;
import com.eduardo.examen_backend.roles.Rol;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class ExamenServiceTest {

    @Mock
    private ExamenRepository examenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PreguntaRepository preguntaRepository;

    @InjectMocks
    private ExamenServiceImpl examenService;

    @Test
    void cambiarEstadoActivoTestOK() {
        Integer idExamen = 1;
        String correoLogueado = "profesor@test.com";

        // Preparar Rol
        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");

        // Preparar Usuario (Dueño)
        Usuario profeDueño = new Usuario();
        profeDueño.setIdUsuario(10);
        profeDueño.setCorreoUsuario(correoLogueado);
        profeDueño.setRoles(Set.of(rolProfesor));

        // Preparar Examen (Activo)
        Examen examenBD = new Examen();
        examenBD.setIdExamen(idExamen);
        examenBD.setActivo(true);
        examenBD.setProfesor(profeDueño);

        when(examenRepository.findById(idExamen)).thenReturn(Optional.of(examenBD));
        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(profeDueño));
        when(examenRepository.save(any(Examen.class))).thenReturn(examenBD);

        ExamenDTO resultado = examenService.cambiarEstadoActivo(idExamen, correoLogueado);

        assertNotNull(resultado, "El resultado no debería ser nulo");
        assertFalse(examenBD.isActivo(), "El examen debería haberse desactivado");
        verify(examenRepository, times(1)).save(examenBD);
    }

    @Test
    void cambiarEstadoActivoIntrusoMAL() {
        Integer idExamen = 1;
        String correoIntruso = "intruso@test.com";

        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");

        // El dueño real
        Usuario profeDueño = new Usuario();
        profeDueño.setIdUsuario(10);

        // El que intenta modificar
        Usuario profeIntruso = new Usuario();
        profeIntruso.setIdUsuario(99);
        profeIntruso.setCorreoUsuario(correoIntruso);
        profeIntruso.setRoles(Set.of(rolProfesor));

        Examen examenBD = new Examen();
        examenBD.setIdExamen(idExamen);
        examenBD.setProfesor(profeDueño); // Diferente ID

        when(examenRepository.findById(idExamen)).thenReturn(Optional.of(examenBD));
        when(usuarioRepository.findByCorreoUsuario(correoIntruso)).thenReturn(Optional.of(profeIntruso));

        AuthorizationDeniedException excepcion = assertThrows(AuthorizationDeniedException.class, () -> {
            examenService.cambiarEstadoActivo(idExamen, correoIntruso);
        });

        assertEquals("Acceso Denegado: Solo el creador del examen o un Administrador pueden modificarlo.",
                excepcion.getMessage());
        verify(examenRepository, never()).save(any(Examen.class));
    }

    @Test
    void buscarExamenInexistenteMAL() {
        when(examenRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            examenService.cambiarEstadoActivo(999, "cualquier@test.com");
        });

        verify(usuarioRepository, never()).findByCorreoUsuario(any());
    }

    @Test
    void obtenerTodosTestOK() {
        Examen examenBD = new Examen();
        examenBD.setIdExamen(1);

        Usuario profe = new Usuario();
        profe.setIdUsuario(1);
        examenBD.setProfesor(profe);

        when(examenRepository.findAll()).thenReturn(List.of(examenBD));

        List<ExamenDTO> resultado = examenService.obtenerTodos();
        assertFalse(resultado.isEmpty());
        verify(examenRepository, times(1)).findAll();
    }

    @Test
    void crearExamenTestOK() {
        String correoLogueado = "profe@test.com";
        ExamenDTO dtoEntrada = new ExamenDTO();
        dtoEntrada.setTitulo("Examen 1");

        Usuario profe = new Usuario();
        profe.setIdUsuario(1);
        profe.setCorreoUsuario(correoLogueado);

        Examen examenGuardado = new Examen();
        examenGuardado.setIdExamen(10);
        examenGuardado.setTitulo("Examen 1");
        examenGuardado.setProfesor(profe);

        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(profe));
        when(examenRepository.save(any(Examen.class))).thenReturn(examenGuardado);

        ExamenDTO resultado = examenService.crearExamen(dtoEntrada, correoLogueado);

        assertNotNull(resultado);
        assertEquals(10, resultado.getIdExamen());
        verify(examenRepository, times(1)).save(any(Examen.class));
    }

    @Test
    void anhadirPreguntasTestOK() {
        Categoria catFalsa = new Categoria();
        catFalsa.setIdCategoria(1);
        catFalsa.setNombre("General");

        Integer idExamen = 1;
        String correoLogueado = "profe@test.com";
        List<Integer> idsPreguntas = List.of(5, 6);

        // Dueño
        Rol rol = new Rol();
        rol.setNombreRol("PROFESOR");
        Usuario profe = new Usuario();
        profe.setIdUsuario(1);
        profe.setRoles(Set.of(rol));

        Examen examenBD = new Examen();
        examenBD.setIdExamen(idExamen);
        examenBD.setProfesor(profe);

        Pregunta p1 = new Pregunta();
        p1.setIdPregunta(5);
        p1.setActivo(true);
        p1.setCategoria(catFalsa);
        p1.setOpciones(List.of());

        Pregunta p2 = new Pregunta();
        p2.setIdPregunta(6);
        p2.setActivo(true);
        p2.setCategoria(catFalsa);
        p2.setOpciones(List.of());

        List<Pregunta> preguntasBD = List.of(p1, p2);

        when(examenRepository.findById(idExamen)).thenReturn(Optional.of(examenBD));
        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(profe));
        when(preguntaRepository.findAllById(idsPreguntas)).thenReturn(preguntasBD);
        when(examenRepository.save(any(Examen.class))).thenReturn(examenBD);

        ExamenDTO resultado = examenService.anhadirPreguntas(idExamen, idsPreguntas, correoLogueado);

        assertNotNull(resultado);
        verify(examenRepository, times(1)).save(examenBD);
    }
}