package com.progweb2.blog.controller;

import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.model.dto.AuthRequestDTO;
import com.progweb2.blog.model.dto.AuthResponseDTO;
import com.progweb2.blog.repository.UsuarioRepository;
import com.progweb2.blog.security.TokenService;
import com.progweb2.blog.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    public UsuarioController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping
    public ResponseEntity<?> listarUsuarios(){
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok().body(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listarUsuarioPorId(@PathVariable Long id){
        Usuario usuario = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok().body(usuario);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario){
        Usuario usuarioaCadastrar = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.ok().body(usuarioaCadastrar);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequestDTO data){
        Usuario user = this.usuarioRepository.findByEmail(data.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(data.senha(), user.getSenha())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDTO(token, user.getId()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credenciais incorretas. Tente logar novamente!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok().body("Conta excluída");
    }

}
