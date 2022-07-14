package com.jesusvazquez.springcloud.msvc.cursos.controllers;

import com.jesusvazquez.springcloud.msvc.cursos.models.entity.Curso;
import com.jesusvazquez.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarById(@PathVariable Long id) {
        Optional<Curso> curso = cursoService.buscarById(id);

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

    private ResponseEntity<?> validarCamposParametros(BindingResult bindingResult) {
        Map<String, String> errores = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errores.put(error.getField(), "El campo " + error.getField() + " "+error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
