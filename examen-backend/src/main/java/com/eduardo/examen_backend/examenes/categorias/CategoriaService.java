package com.eduardo.examen_backend.examenes.categorias;

import java.util.List;

/**
 * Interfaz que define las operaciones de negocio para la gestión de Categorías (Temáticas).
 * Las categorías permiten organizar las preguntas de los exámenes por bloques temáticos.
 */
public interface CategoriaService {

    /**
     * Recupera todas las categorías registradas en el sistema.
     * * @return Lista de objetos {@link CategoriaDTO}.
     */
    List<CategoriaDTO> obtenerTodas();

    /**
     * Busca una categoría específica mediante su identificador único.
     * * @param id Identificador numérico de la categoría.
     * @return Objeto {@link CategoriaDTO} con los datos de la categoría.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si el ID no existe.
     */
    CategoriaDTO obtenerPorId(Integer id);

    /**
     * Crea una nueva categoría en la base de datos.
     * * @param categoriaDTO Datos de la nueva categoría a crear.
     * @return Objeto {@link CategoriaDTO} con la categoría persistida y su ID autogenerado.
     * @throws com.eduardo.examen_backend.shared.exceptions.DuplicateException si el nombre ya está en uso.
     */
    CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO);

    /**
     * Actualiza la información de una categoría existente.
     * * @param id Identificador de la categoría a modificar.
     * @param categoriaDTO Nuevos datos a aplicar.
     * @return Objeto {@link CategoriaDTO} actualizado.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si el ID no existe.
     * @throws com.eduardo.examen_backend.shared.exceptions.DuplicateException si el nuevo nombre colisiona con otra categoría.
     */
    CategoriaDTO actualizarCategoria(Integer id, CategoriaDTO categoriaDTO);

    /**
     * Elimina físicamente una categoría de la base de datos.
     * * @param id Identificador de la categoría a eliminar.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si el ID no existe.
     */
    void eliminarCategoria(Integer id);
}