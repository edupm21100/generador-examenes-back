package com.eduardo.examen_backend.usuarios;

import com.eduardo.examen_backend.auth.PasswordDTO;
import com.eduardo.examen_backend.roles.RolDTO;

import java.util.List;

/**
 * Servicio principal para la gestión de la lógica de negocio de los Usuarios.
 * Define las operaciones CRUD y la gestión de contraseñas y roles asignados.
 * * @author Eduardo
 * @version 1.0
 */
public interface UsuarioService {
    
    /**
     * Registra un nuevo usuario en el sistema.
     * Encripta la contraseña y asigna un rol por defecto si no se especifica.
     *
     * @param usuarioDTO Objeto con los datos de registro del usuario.
     * @return El usuario guardado mapeado a DTO.
     */
    UsuarioDTO save(UsuarioDTO usuarioDTO);
    
    /**
     * Recupera el listado completo de todos los usuarios registrados.
     *
     * @return Lista de DTOs con la información de los usuarios.
     */
    List<UsuarioDTO> findAll();
    
    /**
     * Busca un usuario específico por su identificador único.
     *
     * @param idUsuario Identificador numérico del usuario en la base de datos.
     * @return El DTO del usuario encontrado.
     */
    UsuarioDTO findById(Integer idUsuario);
    
    /**
     * Actualiza los datos generales de un usuario existente.
     * No modifica contraseñas ni roles.
     *
     * @param usuarioDTO DTO con los datos actualizados. Debe contener el ID válido.
     * @return El usuario actualizado.
     */
    UsuarioDTO update(UsuarioDTO usuarioDTO);
    
    /**
     * Asigna un nuevo rol a un usuario existente.
     *
     * @param idUsuarioTarget ID del usuario al que se le añadirá el rol.
     * @param idRolNuevo ID del rol a asignar.
     * @return DTO del usuario con sus roles actualizados.
     */
    UsuarioDTO anhadirRol(Integer idUsuarioTarget, Integer idRolNuevo);
    
    /**
     * Cambia la contraseña del usuario actualmente autenticado tras validar la anterior.
     *
     * @param correoLogueado Correo extraído del token JWT del usuario actual.
     * @param passwordDTO Objeto que contiene la contraseña actual y la nueva.
     * @return El DTO del usuario tras efectuar el cambio.
     */
    UsuarioDTO changeContrasenha(String correoLogueado, PasswordDTO passwordDTO);
    
    /**
     * Realiza un borrado lógico del usuario invirtiendo su estado de actividad.
     *
     * @param idUsuario ID del usuario a dar de baja o alta.
     * @return El usuario con su nuevo estado actualizado.
     */
    UsuarioDTO desactivateUser(Integer idUsuario);
    
    /**
     * Consulta todos los roles que tiene asignados un usuario en particular.
     *
     * @param idUsuario ID del usuario a consultar.
     * @return Lista de roles asociados a ese usuario.
     */
    List<RolDTO> findRolByUsuario(Integer idUsuario);
    
    /**
     * Elimina la relación entre un usuario y un rol específico.
     *
     * @param idUsuarioTarget ID del usuario al que se le quitará el rol.
     * @param idRolEliminar ID del rol a retirar.
     * @return Objeto confirmando la operación.
     */
    UsuarioRolDTO removeRol(Integer idUsuarioTarget, Integer idRolEliminar);
}