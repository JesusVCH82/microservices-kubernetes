package com.jesusvazquez.springcloud.msvc.cursos.repositories;

import com.jesusvazquez.springcloud.msvc.cursos.models.entity.Curso;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CursoRepository  extends CrudRepository<Curso, Long> {

    @Modifying // Para las operaciones distintas a consultar, se le debe agregar esta anotacion para que haga el cambio
    @Query("delete from CursoUsuario cu where cu.usuarioId = ?1")
    public void eliminarCursoUsuarioPorId(Long id);

}
