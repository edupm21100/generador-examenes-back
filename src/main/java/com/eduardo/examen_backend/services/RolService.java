package com.eduardo.examen_backend.services;

import java.util.List;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;

/**
 * Servicio para la gestión de los roles de acceso (perfiles) de la aplicación.
 * Define las operaciones para crear, consultar y administrar el estado de los roles.
 * * @author Eduardo
 * @version 1.0
 */
public interface RolService {

    /**
     * Crea y persiste un nuevo rol en el sistema.
     *
     * @param rolDTO Objeto con el nombre y detalles del nuevo rol.
     * @return El rol creado mapeado a DTO.
     */
    RolDTO save(RolDTO rolDTO);

    /**
     * Recupera el catálogo completo de roles disponibles en el sistema.
     *
     * @return Lista de todos los roles.
     */
    List<RolDTO> findAll();

    /**
     * Busca un rol específico por su identificador único.
     *
     * @param idRol Identificador numérico del rol.
     * @return El DTO del rol encontrado.
     */
    RolDTO findById(Integer idRol);

    /**
     * Actualiza la información de un rol existente.
     *
     * @param rolDTO DTO con los datos actualizados del rol.
     * @return El rol actualizado.
     */
    RolDTO update(RolDTO rolDTO);

    /**
     * Realiza un borrado lógico (desactivación/activación) de un rol.
     * Un rol inactivo no debería poder asignarse a nuevos usuarios.
     *
     * @param idRol ID del rol a modificar.
     * @return El rol con su estado actualizado.
     */
    RolDTO desactivateRol(Integer idRol);

    /**
     * Obtiene una lista de todos los usuarios que tienen asignado un rol específico.
     * Útil para auditorías o comprobación de permisos masivos.
     *
     * @param idRol ID del rol por el cual filtrar a los usuarios.
     * @return Lista de usuarios que poseen el rol indicado.
     */
    List<UsuarioDTO> findUsuariosByRol(Integer idRol);
}