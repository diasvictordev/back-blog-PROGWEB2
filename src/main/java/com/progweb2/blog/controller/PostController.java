package com.progweb2.blog.controller;

import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.Body;
import com.progweb2.blog.model.Post;
import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.service.PostService;
import com.progweb2.blog.service.UsuarioService;
import com.progweb2.blog.service.exceptions.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/post")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> listarPosts(){
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listarPostPorId(@PathVariable Long id){
        Post post = postService.getPostById(id);
        return ResponseEntity.ok().body(post);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Post>> getPostsByUsuario(@PathVariable("id") Long id) {
            List<Post> posts = postService.getPostByUsuario(id);
            return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<?> postar(@RequestBody Post post){
        Post postagem = postService.postar(post.getUsuario().getId(), post);
        return ResponseEntity.ok().body(postagem);
    }

    @PostMapping("/ortografia")
    public ResponseEntity<?> corrigirOrtografia(@RequestBody String post){
        return ResponseEntity.ok(postService.corrigirTexto(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.ok().body("Post excluído");
    }

}
