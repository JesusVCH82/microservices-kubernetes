package com.jesusvazquez.springcloud.msvc.cursos.services;

import com.jesusvazquez.springcloud.msvc.cursos.clients.UsuarioClientRest;
import com.jesusvazquez.springcloud.msvc.cursos.models.Usuario;
import com.jesusvazquez.springcloud.msvc.cursos.models.entity.Curso;
import com.jesusvazquez.springcloud.msvc.cursos.models.entity.CursoUsuario;
import com.jesusvazquez.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements  CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioClientRest usuarioClientRest;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) cursoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarById(Long id) {
        return cursoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarByIdConUsuarios(Long id) {
        Optional<Curso> o = cursoRepository.findById(id);
        if(o.isPresent()) {
            Curso curso = o.get();

            //Para entender esta expresion de Stream y Map es que para Stream convertimos una variable iterable en un flujo
            //y para el map lo que hacemos es extraer un campo y se asigna a la variable
            //en este caso cursoUsuario es la variable que se estara agregando a List<Long> ids
            // y el valor que tomara es el de cursoUsuario.getUsuarioId(), ya al fibal solo se hace un casteo a tipo list

            if(!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios()
                        .stream()
                        .map(cursoUsuario -> cursoUsuario.getUsuarioId())
                        .collect(Collectors.toList());
                List<Usuario> usuarios = usuarioClientRest.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        cursoRepository.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = cursoRepository.findById(cursoId);
        if(o.isPresent()) {
            Usuario usuarioMicroservicio = usuarioClientRest.detalle(usuario.getId());
            Curso curso = o.get();

            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicroservicio.getId());
            curso.addCursoUsuario(cursoUsuario);

            cursoRepository.save(curso);
            return Optional.of(usuarioMicroservicio);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = cursoRepository.findById(cursoId);
        if(o.isPresent()) {
            Usuario usuarioMicroservicio = usuarioClientRest.crearUsuario(usuario);
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicroservicio.getId());
            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMicroservicio);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> o = cursoRepository.findById(cursoId);
        if(o.isPresent()) {
            Usuario usuarioMicroservicio = usuarioClientRest.detalle(usuario.getId());
            Curso curso = o.get();

            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicroservicio.getId());
            curso.removeCursoUsuario(cursoUsuario);

            cursoRepository.save(curso);
            return Optional.of(usuarioMicroservicio);
        }
        return Optional.empty();
    }
}
