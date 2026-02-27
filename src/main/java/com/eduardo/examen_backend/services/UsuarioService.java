package com.eduardo.examen_backend.services;

import java.util.HashSet;
import java.util.List;

import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.exception.BadRequestException;
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

    public UsuarioDTO save(UsuarioDTO usuarioDTO) {
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);

        Integer idRolDefinitivo = (usuarioDTO.getIdRol() != null) ? usuarioDTO.getIdRol() : 1;
        // ASIGNAMOS POR EL ID DEL ROL QUE SE LE PASE
        Rol deafultRol = rolRepository.findById(idRolDefinitivo).orElseThrow(
                () -> new RuntimeException("Error al asignar un ROL"));
        Set<Rol> asignedRol = new HashSet<>();
        asignedRol.add(deafultRol);
        usuario.setRoles(asignedRol);

        return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
    }

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream().map(
                usuario -> modelMapper.map(usuario, UsuarioDTO.class)).toList();
    }

    // LISTAR USUARIOS EN FUNCIÓN DEL ROL
    public List<UsuarioDTO> findByRol(Integer idRol) {
        List<Usuario> filteredUsuarios = usuarioRepository.findByRolesIdRol(idRol);

        return filteredUsuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .toList();
    }

    public Optional<UsuarioDTO> findById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).map(
                usuarioDB -> modelMapper.map(usuarioDB, UsuarioDTO.class));
    }

    public Optional<UsuarioDTO> update(UsuarioDTO usuarioDTO) {
        // DTO USUARIO
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);

        return usuarioRepository.findById(usuario.getIdUsuario()).map(
                usuarioBD -> {
                    usuarioBD.setNombreUsuario(usuario.getNombreUsuario());
                    usuarioBD.setApellidoUsuario(usuario.getApellidoUsuario());
                    usuarioBD.setCorreoUsuario(usuario.getCorreoUsuario());
                    usuarioBD.setContrasenhaUsuario(usuario.getContrasenhaUsuario());
                    usuarioBD.setActivo(usuario.isActivo());
                    return modelMapper.map(usuarioRepository.save(usuarioBD), UsuarioDTO.class);
                });
    }

    // CAMBIAR O AÑADIR ROL
    public UsuarioDTO changeRol(Integer idUsuarioTarget, Integer idRolNuevo, Integer idAdmin) {
        Usuario admin = usuarioRepository.findById(idAdmin).orElseThrow(
                () -> new RuntimeException("Error: El usuario administrador no existe"));
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(rol -> rol.getIdRol() == 1);

        if (!isAdmin) {
            throw new BadRequestException("Acceso denegado: No tienes permisos de Administrador");
        }
        Usuario usuarioTarget = usuarioRepository.findById(idUsuarioTarget).orElseThrow(
                () -> new RuntimeException("Error: El usuario a modificar no existe"));
        Rol nuevoRol = rolRepository.findById(idRolNuevo).orElseThrow(
                () -> new RuntimeException("Error: El rol especificado no existe"));
        Set<Rol> rolesActualizados = new HashSet<>();
        rolesActualizados.add(nuevoRol);
        usuarioTarget.setRoles(rolesActualizados);

        Usuario usuarioGuardado = usuarioRepository.save(usuarioTarget);
        return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
    }

    // CAMBIAR CONTRASEÑA
    public UsuarioDTO changeContrasenha(Integer idUsuario, String contrasenhaNueva, String contrasenhaVieja) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(
                () -> new RuntimeException("El usuario que realiza la petición no existe"));
        if (usuario.getContrasenhaUsuario().equals(contrasenhaVieja)) {
            usuario.setContrasenhaUsuario(contrasenhaNueva);
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
        } else {
            throw new BadRequestException("Contraseña invalida");
        }
    }

    public boolean deleteById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).map(
                usuario -> {
                    usuarioRepository.delete(usuario);
                    return true;
                }).orElse(false);
    }

}
