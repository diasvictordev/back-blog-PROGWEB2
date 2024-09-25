package com.progweb2.blog.controller;

import com.progweb2.blog.model.Comment;
import com.progweb2.blog.model.Post;
import com.progweb2.blog.service.CommentService;
import com.progweb2.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comentario")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> listarComentarios(){
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok().body(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listarComentarioPorId(@PathVariable Long id){
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok().body(comment);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getCommentsByPost(@PathVariable("id") Long id) {
        List<Comment> comments = commentService.getCommentsByPost(id);
        return ResponseEntity.ok(comments);
    }
    @PostMapping
    public ResponseEntity<?> comentar(@RequestBody Comment comment){
        Comment comentario = commentService.comentar(comment.getUsuario().getId(), comment.getPost().getId(),comment);
        return ResponseEntity.ok().body(comentario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        commentService.deleteComment(id);
        return ResponseEntity.ok().body("Comentário excluído");
    }
}
