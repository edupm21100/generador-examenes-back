package com.eduardo.examen_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.repositories.RolRepository;

@Service
public class RolService {
    private RolRepository rolRepository;

        public RolService(RolRepository rolRepository){
        this.rolRepository = rolRepository;
    }

    public Rol save(Rol rol){
        return rolRepository.save(rol);
    }

    public List<Rol> findAll(){
        return rolRepository.findAll();
    }

    public Rol getById(Integer id_rol){
        return rolRepository.findById(id_rol).get();
    }

    public void deleteById(Integer id_rol){
        rolRepository.deleteById(id_rol);
    }

    public Rol update(Rol rol){
        Optional<Rol> rolBBDD = rolRepository.findById(rol.getId_rol());
        Rol rolBD = rolBBDD.get();

        rolBD.setNombre_rol(rol.getNombre_rol());
        rolBD.setActivo(rol.isActivo());

        return rolRepository.save(rolBD);
    }
}
