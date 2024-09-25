package com.progweb2.blog.model;

import com.progweb2.blog.model.dto.RolesEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario", schema = "blog")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "papel")
    private RolesEnum papel;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "comentario", cascade = CascadeType.ALL)
    private List<Comment> comentarios = new ArrayList<>();
}
