package com.jesusvazquez.springcloud.msvc.usuarios.services;

import com.jesusvazquez.springcloud.msvc.usuarios.models.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    public List<Usuario> listar();
    public Optional<Usuario> buscarById(Long id);
    public Usuario guardar(Usuario usuario);
    public void eliminar(Long id);
    public Optional<Usuario> buscarPorEmail(String email);
    public List<Usuario> listarPorIds(Iterable<Long> ids);
}
