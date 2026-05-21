package com.example.demo.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    public Administrador() {}

    public Administrador(Usuario usuario) {
        this.usuario = usuario;
    }

    public Administrador(String username, String password) {
        this.usuario = new Usuario(username, password, null);
    }

    public Administrador(long id, String username, String password) {
        this.id = id;
        this.usuario = new Usuario(username, password, null);
    }

    @JsonProperty("usuario")
    public String getUsuarioUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }

    public String getContrasena() {
        return usuario != null ? usuario.getPassword() : null;
    }
}
