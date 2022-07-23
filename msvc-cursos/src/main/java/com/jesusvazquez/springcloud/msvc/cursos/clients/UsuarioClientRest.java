package com.jesusvazquez.springcloud.msvc.cursos.clients;

import com.jesusvazquez.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Esta anotacion sirve para saber al cliente al que va a consumir pasandole el nombre del microservicio
//que se encuentra en el properties, y tambien como parametro la ruta del microservicio
@FeignClient(name = "msvc-usuarios", url="localhost:8001")
public interface UsuarioClientRest {

    //Los metodos son declarativos, ya que solo apuntando a la url y enviando
    // los parametros del metodo, obtenemos la respuesta del servicio

    @GetMapping("/{id}")
    Usuario detalle(@PathVariable Long id);

    @PostMapping("/")
    Usuario crearUsuario(@RequestBody Usuario usuario);

    //Se utiliza Iterable en lugar de List porque en Feign Client causa problemas
    @GetMapping("/usuarios-por-curso")
    List<Usuario> obtenerAlumnosPorCurso(@RequestParam Iterable<Long> ids);
}
