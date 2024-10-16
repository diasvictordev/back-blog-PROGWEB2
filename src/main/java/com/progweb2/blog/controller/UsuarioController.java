package com.progweb2.blog.controller;

import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.model.dto.AuthRequestDTO;
import com.progweb2.blog.model.dto.AuthResponseDTO;
import com.progweb2.blog.repository.UsuarioRepository;
import com.progweb2.blog.security.TokenService;
import com.progweb2.blog.service.UsuarioService;
import com.progweb2.blog.service.exceptions.RegraDeNegocioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
        Optional<Usuario> usuarioEmail = usuarioService.getUsuarioByEmail(usuario.getEmail());
        usuarioEmail.ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail já criado!");
        });

        Usuario usuarioaCadastrar = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.ok().body(usuarioaCadastrar);
    }


    @PostMapping(path = "/login",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(description = "Método utilizado para realizar a inclusão de um entidade",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Entidade Incluida",
                            useReturnTypeSchema = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegraDeNegocioException.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de Negócio",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegraDeNegocioException.class)))
            })
    public ResponseEntity login(@RequestBody AuthRequestDTO data){
        try {
            AuthResponseDTO responseDTO = usuarioService.login(data);
            if (responseDTO != null) {
                return ResponseEntity.ok(responseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciais incorretas. Tente logar novamente!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok().body("Conta excluída");
    }

}
