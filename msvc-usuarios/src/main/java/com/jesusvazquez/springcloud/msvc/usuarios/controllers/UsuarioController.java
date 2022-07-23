package com.jesusvazquez.springcloud.msvc.usuarios.controllers;

import com.jesusvazquez.springcloud.msvc.usuarios.models.entity.Usuario;
import com.jesusvazquez.springcloud.msvc.usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar(){
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalleUsuario(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = usuarioService.buscarById(id);

        if(usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Para las validaciones que se agregaron a la entity en Usuarios
     *  se utiliza @Valid para anotar el parametro que se validara y
     *  BindingResult se utiliza para saber si existieron errores y en que campos
     *  surgieron. Nota: El Binding debe de colocarse despues del parametro a validar
     */
    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario,
                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return validarCamposParametros(bindingResult);
        }

        if(!usuario.getEmail().isEmpty()  && usuarioService.buscarPorEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().
                    body(Collections.
                            singletonMap("error","Ya existe un usuario con este correo electronico!" ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario,
                                    BindingResult bindingResult,
                                    @PathVariable Long id
                                   ) {

        if(bindingResult.hasErrors()) {
            return validarCamposParametros(bindingResult);
        }

        Optional<Usuario> usuarioEditar = usuarioService.buscarById(id);
        if(usuarioEditar.isPresent()) {
            Usuario usuarioBd = usuarioEditar.get();

            if(!usuario.getEmail().isEmpty()  && !usuario.getEmail().equalsIgnoreCase(usuarioBd.getEmail()) &&
                    usuarioService.buscarPorEmail(usuario.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().
                        body(Collections.
                                singletonMap("error","Ya existe un usuario con este correo electronico!" ));
            }

            usuarioBd.setNombre(usuario.getNombre());
            usuarioBd.setEmail(usuario.getEmail());
            usuarioBd.setPassword(usuario.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuarioBd));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarById(id);

        if(usuario.isPresent()) {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(usuarioService.listarPorIds(ids));
    }

    private ResponseEntity<?> validarCamposParametros(BindingResult bindingResult) {
        Map<String, String> errores = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errores.put(error.getField(), "El campo " + error.getField() + " "+error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
