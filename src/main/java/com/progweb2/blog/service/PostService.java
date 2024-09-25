package com.progweb2.blog.service;

import com.progweb2.blog.model.Post;
import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.repository.PostRepository;
import com.progweb2.blog.repository.UsuarioRepository;
import com.progweb2.blog.service.exceptions.RegraDeNegocioException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.*;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

    public Post postar(Long id, Post post){
        Usuario usuario =  usuarioRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o usuário!")
        );
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate hoje = now.toLocalDate();
        LocalDateTime tempoAtual = now.toLocalDateTime();
        Post postagem = new Post();
        postagem.setTitulo(post.getTitulo());
        postagem.setPost(post.getPost());
        postagem.setData(hoje);
        postagem.setTempo(tempoAtual);
        postagem.setUsuario(usuario);
        return postRepository.save(postagem);
    }

    public Post getPostById(Long id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o post!")
        );
        return post;
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public List<Post> getPostByUsuario(Long id){
        Usuario usuario =  usuarioRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o usuário!")
        );
        return postRepository.findByUsuarioId(id);
    }

    public void deletePost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post não encontrado!"));
        postRepository.delete(post);
    }
}
