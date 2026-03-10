package com.eduardo.examen_backend.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.dto.PasswordDTO;
import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.exceptions.BadRequestException;
import com.eduardo.examen_backend.exceptions.DuplicateException;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.RolRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {
        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final ModelMapper modelMapper;
        private final PasswordEncoder passwordEncoder;

        public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                        RolRepository rolRepository,
                        ModelMapper modelMapper,
                        PasswordEncoder passwordEncoder) {
                this.usuarioRepository = usuarioRepository;
                this.rolRepository = rolRepository;
                this.modelMapper = modelMapper;
                this.passwordEncoder = passwordEncoder;
        }

        // DEVOLVER INFORMACIÓN DEL USUARIO QUE HACE LA PETICIÓN
        private String getUsuarioAccion() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                        return auth.getName(); // Devuelve el correo guardado en el token
                }
                return "Sistema/Anónimo";
        }

        // GUARDAR USUARIO
        @Override
        @Transactional
        public UsuarioDTO save(UsuarioDTO usuarioDTO) {
                if (usuarioRepository.existsByCorreoUsuario(usuarioDTO.getCorreoUsuario())) {
                        throw new DuplicateException("El correo ya está en uso");
                }
                Usuario userToSave = modelMapper.map(usuarioDTO, Usuario.class);
                String encodedPassword = passwordEncoder.encode(userToSave.getContrasenhaUsuario());
                userToSave.setContrasenhaUsuario(encodedPassword);
                Integer idRolAsignar = (usuarioDTO.getIdRol() != null) ? usuarioDTO.getIdRol() : 3;

                Rol rol = rolRepository.findById(idRolAsignar)
                                .orElseThrow(() -> new NotFoundException("El rol especificado (" + idRolAsignar
                                                + ") no existe en la base de datos"));
                userToSave.getRoles().add(rol);

                Usuario usuarioGuardado = usuarioRepository.save(userToSave);

                log.info("Nuevo usuario registrado con éxito. Correo: {} | ID asignado: {}",
                                usuarioGuardado.getCorreoUsuario(), usuarioGuardado.getIdUsuario());

                return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
        }

        // LISTAR USUARIOS
        @Override
        public List<UsuarioDTO> findAll() {
                log.info("[Autor: {}] Ha consultado la lista completa de usuarios activos.", getUsuarioAccion());
                return usuarioRepository.findByActivoTrue().stream().map(
                                usuario -> modelMapper.map(usuario, UsuarioDTO.class)).toList();
        }

        // OBTENER USUARIO POR SU ID
        @Override
        public UsuarioDTO findById(Integer idUsuario) {
                log.info("[Autor: {}] Ha consultado los detalles del usuario con ID {}.", getUsuarioAccion(),
                                idUsuario);
                return usuarioRepository.findById(idUsuario)
                                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                                .orElseThrow(() -> new NotFoundException("Id: " + idUsuario + " no encontrado"));
        }

        // ACTUALIZAR USUARIO SALVO CONTRASEÑA, ROLES Y ACTIVO
        @Override
        @Transactional
        public UsuarioDTO update(UsuarioDTO usuarioDTO) {
                Usuario usuarioBD = usuarioRepository.findById(usuarioDTO.getIdUsuario())
                                .orElseThrow(() -> new NotFoundException("El usuario a modificar no existe"));
                if (!usuarioBD.getCorreoUsuario().equals(usuarioDTO.getCorreoUsuario()) &&
                                usuarioRepository.existsByCorreoUsuario(usuarioDTO.getCorreoUsuario())) {
                        throw new DuplicateException("El correo " + usuarioDTO.getCorreoUsuario()
                                        + " ya está en uso por otro usuario");
                }
                usuarioBD.setNombreUsuario(usuarioDTO.getNombreUsuario());
                usuarioBD.setApellidoUsuario(usuarioDTO.getApellidoUsuario());
                usuarioBD.setCorreoUsuario(usuarioDTO.getCorreoUsuario());
                log.info("[Autor: {}] Usuario actualizado con éxito. ID modificado: {}",
                                getUsuarioAccion(), usuarioBD.getIdUsuario());
                return modelMapper.map(usuarioRepository.save(usuarioBD), UsuarioDTO.class);
        }

        // AÑADIR ROL A USUARIO SI ES ADMIN
        @Override
        @Transactional
        public UsuarioDTO anhadirRol(Integer idUsuarioTarget, Integer idRolNuevo) {
                Usuario usuarioTarget = usuarioRepository.findById(idUsuarioTarget).orElseThrow(
                                () -> new NotFoundException("Error: El usuario a modificar no existe"));
                Rol nuevoRol = rolRepository.findById(idRolNuevo).orElseThrow(
                                () -> new NotFoundException("Error: El rol especificado no existe"));
                if (usuarioTarget.getRoles().contains(nuevoRol)) {
                        throw new BadRequestException("El usuario ya tiene asignado este rol");
                }
                usuarioTarget.getRoles().add(nuevoRol);

                log.info("[Autor: {}] Ha añadido el rol '{}' (ID: {}) al usuario objetivo con ID {}.",
                                getUsuarioAccion(), nuevoRol.getNombreRol(), idRolNuevo, idUsuarioTarget);

                return modelMapper.map(usuarioRepository.save(usuarioTarget), UsuarioDTO.class);
        }

        // CAMBIAR CONTRASEÑA
        @Override
        @Transactional
        public UsuarioDTO changeContrasenha(String correoLogueado, PasswordDTO dto) {
                Usuario usuarioBD = usuarioRepository.findByCorreoUsuario(correoLogueado)
                                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
                if (!passwordEncoder.matches(dto.getOldPassword(), usuarioBD.getContrasenhaUsuario())) {
                        throw new BadRequestException("La contraseña antigua no es correcta");
                }
                usuarioBD.setContrasenhaUsuario(passwordEncoder.encode(dto.getNewPassword()));

                log.info("[Autor: {}] Ha cambiado su propia contraseña exitosamente.", getUsuarioAccion());
                return modelMapper.map(usuarioRepository.save(usuarioBD), UsuarioDTO.class);
        }

        // BORRADO LÓGICO
        @Override
        @Transactional
        public UsuarioDTO desactivateUser(Integer idUsuario) {
                Usuario usuario = usuarioRepository.findById(idUsuario)
                                .orElseThrow(() -> new NotFoundException(
                                                "Usuario no encontrado para activar/desactivar"));
                usuario.setActivo(!usuario.isActivo());

                String estado = usuario.isActivo() ? "REACTIVADO" : "DESACTIVADO";
                log.info("[Autor: {}] Ha {} al usuario objetivo con ID {}.",
                                getUsuarioAccion(), estado, idUsuario);

                return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
        }

        // LISTAR ROLES EN FUNCIÓN DEL USUARIO
        @Override
        public List<RolDTO> findRolByUsuario(Integer idUsuario) {
                Usuario usuario = usuarioRepository.findById(idUsuario)
                                .orElseThrow(() -> new NotFoundException(
                                                "El usuario con ID " + idUsuario + " no existe"));
                log.info("[Autor: {}] Ha consultado la lista de roles del usuario con ID {}.", getUsuarioAccion(),
                                idUsuario);
                return usuario.getRoles().stream()
                                .map(rol -> modelMapper.map(rol, RolDTO.class))
                                .toList();
        }

        // ELIMINAR ROL DE UN USUARIO
        @Override
        @Transactional
        public UsuarioRolDTO removeRol(Integer idUsuarioTarget, Integer idRolEliminar) {
                Usuario usuarioTarget = usuarioRepository.findById(idUsuarioTarget)
                                .orElseThrow(() -> new NotFoundException("Error: El usuario a modificar no existe"));
                Rol rolARemover = rolRepository.findById(idRolEliminar)
                                .orElseThrow(() -> new NotFoundException("Error: El rol especificado no existe"));
                if (!usuarioTarget.getRoles().contains(rolARemover)) {
                        throw new BadRequestException("El usuario no posee el rol que intentas eliminar");
                }
                if (usuarioTarget.getRoles().size() == 1) {
                        throw new BadRequestException("No puedes dejar a un usuario sin roles. Asígnale otro primero.");
                }
                usuarioTarget.getRoles().remove(rolARemover);
                usuarioRepository.save(usuarioTarget);

                UsuarioRolDTO datoMostrado = new UsuarioRolDTO();

                datoMostrado.setIdUsuario(idUsuarioTarget);
                datoMostrado.setIdRol(idRolEliminar);

                log.info("[Autor: {}] Ha eliminado el rol '{}' (ID: {}) del usuario objetivo con ID {}.",
                                getUsuarioAccion(), rolARemover.getNombreRol(), idRolEliminar, idUsuarioTarget);

                return datoMostrado;
        }
}
