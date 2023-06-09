package org.aguzman.springcloud.msvc.cursos.clients;

import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-usuarios", url = "localhost:8001")
public interface UsuarioClientRest {
    /* Aqui solo consumimos al microservicio usuario y le vamos a enviar el id
    * Por que aqui solo es el CLIENTE HTTP */
    @GetMapping("/{id}")
    Usuario detalle(@PathVariable Long id);

    @PostMapping("/")
    Usuario crear(@RequestBody Usuario usuario);
    @GetMapping("/usuarios-por-curso")
    List<Usuario> obtenerAlumnoPorCurso(@RequestParam Iterable<Long> ids);
}
