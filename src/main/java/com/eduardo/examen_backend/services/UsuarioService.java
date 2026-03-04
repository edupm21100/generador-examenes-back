package com.eduardo.examen_backend.services;

import java.util.HashSet;
import java.util.List;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

@Service
public class UsuarioService {
        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final ModelMapper modelMapper;

        public UsuarioService(UsuarioRepository usuarioRepository,
                        RolRepository rolRepository,
                        ModelMapper modelMapper) {
                this.usuarioRepository = usuarioRepository;
                this.rolRepository = rolRepository;
                this.modelMapper = modelMapper;
        }

        // GUARDAR USUARIO
        public UsuarioDTO save(UsuarioDTO usuarioDTO) {
                if (usuarioRepository.existsByCorreoUsuario(usuarioDTO.getCorreoUsuario())) {
                        throw new DuplicateException("El correo " + usuarioDTO.getCorreoUsuario() + " ya está en uso");
                }
                Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
                Integer idRolDefinitivo = (usuarioDTO.getIdRol() != null) ? usuarioDTO.getIdRol() : 3;

                Rol deafultRol = rolRepository.findById(idRolDefinitivo).orElseThrow(
                                () -> new NotFoundException("Error: El rol con ID " + idRolDefinitivo + " no existe"));
                Set<Rol> asignedRol = new HashSet<>();
                asignedRol.add(deafultRol);
                usuario.setRoles(asignedRol);

                return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
        }

        // LISTAR USUARIOS
        public List<UsuarioDTO> findAll() {
                return usuarioRepository.findByActivoTrue().stream().map(
                                usuario -> modelMapper.map(usuario, UsuarioDTO.class)).toList();
        }

        // OBTENER USUARIO POR SU ID
        public UsuarioDTO findById(Integer idUsuario) {
                return usuarioRepository.findById(idUsuario)
                                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                                .orElseThrow(() -> new NotFoundException("Id: " + idUsuario + " no encontrado"));
        }

        // ACTUALIZAR USUARIO SALVO CONTRASEÑA, ROLES Y ACTIVO
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
                return modelMapper.map(usuarioRepository.save(usuarioBD), UsuarioDTO.class);
        }

        // AÑADIR ROL A USUARIO SI ES ADMIN
        public UsuarioDTO anhadirRol(Integer idUsuarioTarget, Integer idRolNuevo, Integer idAdmin) {
                Usuario admin = usuarioRepository.findById(idAdmin).orElseThrow(
                                () -> new NotFoundException("Error: El usuario administrador no existe"));
                boolean isAdmin = admin.getRoles().stream()
                                .anyMatch(rol -> rol.getIdRol() == 1);

                if (!isAdmin) {
                        // (Nota: En el futuro con Spring Security esto lanzará un 403 Forbidden,
                        // peropor ahora BadRequest es perfecto)
                        throw new BadRequestException("Acceso denegado: No tienes permisos de Administrador");
                }
                Usuario usuarioTarget = usuarioRepository.findById(idUsuarioTarget).orElseThrow(
                                () -> new NotFoundException("Error: El usuario a modificar no existe"));
                Rol nuevoRol = rolRepository.findById(idRolNuevo).orElseThrow(
                                () -> new NotFoundException("Error: El rol especificado no existe"));
                usuarioTarget.getRoles().add(nuevoRol);
                return modelMapper.map(usuarioRepository.save(usuarioTarget), UsuarioDTO.class);
        }

        // CAMBIAR CONTRASEÑA
        public UsuarioDTO changeContrasenha(Integer idUsuario, PasswordDTO passwordDTO) {
                Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(
                                () -> new NotFoundException("El usuario que realiza la petición no existe"));
                if (!usuario.getContrasenhaUsuario().equals(passwordDTO.getOldPassword())) {
                        throw new BadRequestException("Contraseña inválida");
                }
                usuario.setContrasenhaUsuario(passwordDTO.getNewPassword());
                Usuario usuarioGuardado = usuarioRepository.save(usuario);
                return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
        }

        // BORRADO LÓGICO
        public UsuarioDTO desactivateUser(Integer idUsuario) {
                Usuario usuario = usuarioRepository.findById(idUsuario)
                                .orElseThrow(() -> new NotFoundException(
                                                "Usuario no encontrado para activar/desactivar"));
                usuario.setActivo(!usuario.isActivo());
                return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
        }

        // LISTAR ROLES EN FUNCIÓN DEL USUARIO
        public List<RolDTO> findRolByUsuario(Integer idUsuario) {
                Usuario usuario = usuarioRepository.findById(idUsuario)
                                .orElseThrow(() -> new NotFoundException(
                                                "El usuario con ID " + idUsuario + " no existe"));
                return usuario.getRoles().stream()
                                .map(rol -> modelMapper.map(rol, RolDTO.class))
                                .toList();
        }

        // ELIMINAR ROL DE UN USUARIO
        public UsuarioRolDTO removeRol(Integer idUsuarioTarget, Integer idRolEliminar, Integer idAdmin) {
                Usuario admin = usuarioRepository.findById(idAdmin)
                                .orElseThrow(() -> new NotFoundException("Error: El usuario administrador no existe"));
                boolean isAdmin = admin.getRoles().stream()
                                .anyMatch(rol -> rol.getIdRol() == 1);

                if (!isAdmin) {
                        throw new BadRequestException("Acceso denegado: No tienes permisos de Administrador");
                }
                Usuario usuarioTarget = usuarioRepository.findById(idUsuarioTarget)
                                .orElseThrow(() -> new NotFoundException("Error: El usuario a modificar no existe"));

                Rol rolARemover = rolRepository.findById(idRolEliminar)
                                .orElseThrow(() -> new NotFoundException("Error: El rol especificado no existe"));
                usuarioTarget.getRoles().remove(rolARemover);
                usuarioRepository.save(usuarioTarget);

                UsuarioRolDTO datoMostrado = new UsuarioRolDTO();
                datoMostrado.setIdUsuario(idUsuarioTarget);
                datoMostrado.setIdRol(idRolEliminar);

                return datoMostrado;
        }
}
