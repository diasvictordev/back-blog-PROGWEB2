package com.progweb2.blog.service;

import com.progweb2.blog.model.Comment;
import com.progweb2.blog.model.Post;
import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.repository.CommentRepository;
import com.progweb2.blog.repository.PostRepository;
import com.progweb2.blog.repository.UsuarioRepository;
import com.progweb2.blog.service.exceptions.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Comment comentar(Long idUsuario, Long idPost, Comment comment){
        Usuario usuario =  usuarioRepository.findById(idUsuario).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o usuário!")
        );
        Post post = postRepository.findById(idPost).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o post!")
        );
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate hoje = now.toLocalDate();
        LocalDateTime tempoAtual = now.toLocalDateTime();
        Comment comentario = new Comment();
        comentario.setComentario(comment.getComentario());
        comentario.setPost(post);
        comentario.setData(hoje);
        comentario.setTempo(tempoAtual);
        comentario.setUsuario(usuario);
        return commentRepository.save(comentario);
    }

    public Comment getCommentById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o comentário!")
        );
        return comment;
    }

    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByPost(Long id){
        Post posts =  postRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o post!")
        );
        return commentRepository.findByPostId(id);
    }
    public void deleteComment(Long id){
      Comment comment = getCommentById(id);
      commentRepository.delete(comment);
    }

}
