package com.jesusvazquez.springcloud.msvc.cursos.controllers;

import com.jesusvazquez.springcloud.msvc.cursos.models.Usuario;
import com.jesusvazquez.springcloud.msvc.cursos.models.entity.Curso;
import com.jesusvazquez.springcloud.msvc.cursos.services.CursoService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> curso = cursoService.buscarByIdConUsuarios(id);  //cursoService.buscarById(id);

        if(curso.isPresent()) {
            return ResponseEntity.ok(curso.get());
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/")
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return validarCamposParametros(bindingResult);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(curso));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso,
                                    BindingResult bindingResult,
                                    @PathVariable Long id) {
        if(bindingResult.hasErrors()) {
            return validarCamposParametros(bindingResult);
        }
        Optional<Curso> cursoEditar = cursoService.buscarById(id);

        if(cursoEditar.isPresent()) {
            Curso cursoBd = cursoEditar.get();
            cursoBd.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(cursoBd));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Curso> curso = cursoService.buscarById(id);
        if(curso.isPresent()) {
            cursoService.eliminar(curso.get().getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario,
                                            @PathVariable Long cursoId) {
        Optional<Usuario>  o;
        try {
            o = cursoService.asignarUsuario(usuario,cursoId);
        } catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("mensaje", "No existe el usuario " +
                            "por id o error en la comunicacion: " + e.getMessage()));
        }


        if(o.isPresent()) {
            return  ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();

    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario,
                                            @PathVariable Long cursoId) {
        Optional<Usuario>  o;
        try {
            o = cursoService.crearUsuario(usuario,cursoId);
        } catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("mensaje", "No se pudo crear el usuario " +
                            "o error en la comunicacion: " + e.getMessage()));
        }
        if(o.isPresent()) {
            return  ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario,
                                            @PathVariable Long cursoId) {
        Optional<Usuario>  o;
        try {
            o = cursoService.eliminarUsuario(usuario,cursoId);
        } catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("mensaje", "No existe el usuario por id  " +
                            "o error en la comunicacion: " + e.getMessage()));
        }

        if(o.isPresent()) {
            return  ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id) {
        cursoService.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validarCamposParametros(BindingResult bindingResult) {
        Map<String, String> errores = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errores.put(error.getField(), "El campo " + error.getField() + " "+error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
