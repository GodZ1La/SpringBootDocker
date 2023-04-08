package org.aguzman.springcloud.msvc.cursos.services;

import org.aguzman.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.aguzman.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CursoServiceImpl implements CursoService{

    private final CursoRepository repository;
    private final UsuarioClientRest client;

    public CursoServiceImpl(CursoRepository repository, UsuarioClientRest client) {
        this.repository = repository;
        this.client = client;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        return repository.findById(id).map(curso -> {
            if (!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios().stream().map(CursoUsuario::getUsuarioId).toList();
                List<Usuario> usuarios = client.obtenerAlumnoPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
            return curso;
        });
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        return repository.findById(cursoId)
                .map(curso -> {
                    Usuario usuarioMsvc = client.detalle(usuario.getId());
                    CursoUsuario cursoUsuario = new CursoUsuario();
                    cursoUsuario.setUsuarioId(usuarioMsvc.getId());
                    curso.agregarCursoUsuario(cursoUsuario);
                    repository.save(curso);
                    return Optional.of(usuarioMsvc);
                })
                .orElse(Optional.empty());
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        return repository.findById(cursoId).map(curso -> {
            Usuario usuarioMsvc = client.crear(usuario);
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.agregarCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usuarioMsvc);
        }).orElse(Optional.empty());
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        return repository.findById(cursoId)
                .map(curso -> {
                    Usuario usuarioMsvc = client.detalle(usuario.getId());
                    CursoUsuario cursoUsuario = new CursoUsuario();
                    cursoUsuario.setUsuarioId(usuarioMsvc.getId());
                    curso.eliminarCursoUsuario(cursoUsuario);
                    repository.save(curso);
                    return Optional.of(usuarioMsvc);
                })
                .orElse(Optional.empty());
    }
}