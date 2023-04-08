package org.aguzman.springcloud.msvc.cursos.controllers;

import feign.FeignException;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.services.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> optional = service.porIdConUsuarios(id); //service.porId(id);
        if (optional.isPresent()) {
            return new ResponseEntity<>(optional.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult resultado) {
        if(resultado.hasErrors()){
            validar(resultado);
        }
        Curso cursoDb = service.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso,BindingResult resultado, @PathVariable Long id) {
        if(resultado.hasErrors()){
            validar((resultado));
        }
        Optional<Curso> o = service.porId(id);
        if (o.isPresent()) {
            Curso cursoDb = o.get();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Curso> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminar(o.get().getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> optional;
        try{
            optional = service.asignarUsuario(usuario, cursoId);
        }catch (FeignException e){
            return new ResponseEntity(Collections.singletonMap("mensaje", "No existe el usuario por el Id" +
                    " o error en la comunicacion" + e.getMessage()), HttpStatus.NOT_FOUND);
        }
        return getResponseEntity(optional);
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> optional;
        try{
            optional = service.crearUsuario(usuario, cursoId);
        }catch (FeignException e){
            return new ResponseEntity(Collections.singletonMap("mensaje", "No se pudo crear el usuario" +
                    " o error en la comunicacion" + e.getMessage()), HttpStatus.NOT_FOUND);
        }
        return getResponseEntity(optional);
    }

    @PutMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> optional;
        try{
            optional = service.eliminarUsuario(usuario, cursoId);
        }catch (FeignException e){
            return new ResponseEntity(Collections.singletonMap("mensaje", "No existe el usuario por el Id" +
                    " o error en la comunicacion" + e.getMessage()), HttpStatus.NOT_FOUND);
        }
        return getResponseEntity(optional);
    }
    private static ResponseEntity<Object>validar(BindingResult resultado) {
        Map<String, String> errores = new HashMap<>();
        resultado.getFieldErrors().forEach(err -> errores.put(err.getField(),
                "El campo " + err.getField() + " " +err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    private static ResponseEntity getResponseEntity(Optional<Usuario> optional) {
        return optional.map(usuario -> new ResponseEntity(usuario, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }
}