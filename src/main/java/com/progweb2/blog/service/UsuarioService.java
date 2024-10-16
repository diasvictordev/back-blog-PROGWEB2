package com.progweb2.blog.service;


import com.progweb2.blog.model.Comment;
import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.model.dto.AuthRequestDTO;
import com.progweb2.blog.model.dto.AuthResponseDTO;
import com.progweb2.blog.model.dto.RolesEnum;
import com.progweb2.blog.repository.CommentRepository;
import com.progweb2.blog.repository.UsuarioRepository;
import com.progweb2.blog.security.TokenService;
import com.progweb2.blog.service.exceptions.RegraDeNegocioException;
import org.aspectj.lang.reflect.UnlockSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;


    private final PasswordEncoder passwordEncoder;

    public UsuarioService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public Usuario cadastrarUsuario(Usuario usuario){
        Usuario newUser = new Usuario();
        String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getSenha());
        newUser.setSenha(encryptedPassword);
        newUser.setEmail(usuario.getEmail());
        newUser.setNome(usuario.getNome());
        newUser.setDescricao(usuario.getDescricao());
        newUser.setPapel(RolesEnum.USER);
        return usuarioRepository.save(newUser);
    }

    public Usuario getUsuarioById(Long id){
       Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o usuário!")
        );
        return usuario;
    }

    public List<Usuario> getAllUsuarios(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioByEmail(String email){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario;
    }

    public void deleteUsuario(Long id){
        Usuario usuario = getUsuarioById(id);
        usuarioRepository.delete(usuario);
    }

    public AuthResponseDTO login(AuthRequestDTO data) {
        Usuario user = this.usuarioRepository.findByEmail(data.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(data.senha(), user.getSenha())) {
            String token = this.tokenService.generateToken(user);
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(token, user.getId());
            return authResponseDTO;
        }
        else{
            return null;
        }
    }



}
