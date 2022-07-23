package com.jesusvazquez.springcloud.msvc.cursos.services;

import com.jesusvazquez.springcloud.msvc.cursos.models.Usuario;
import com.jesusvazquez.springcloud.msvc.cursos.models.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    public List<Curso> listar();
    public Optional<Curso> buscarById(Long id);
    public Optional<Curso> buscarByIdConUsuarios(Long id);
    public Curso guardar(Curso curso);
    public void eliminar(Long id);
    public void eliminarCursoUsuarioPorId(Long id);
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);
}
