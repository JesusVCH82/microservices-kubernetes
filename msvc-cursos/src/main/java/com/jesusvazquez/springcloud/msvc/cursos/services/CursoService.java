package com.jesusvazquez.springcloud.msvc.cursos.services;

import com.jesusvazquez.springcloud.msvc.cursos.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {

    public List<Curso> listar();
    public Optional<Curso> buscarById(Long id);
    public Curso guardar(Curso curso);
    public void eliminar(Long id);
}
