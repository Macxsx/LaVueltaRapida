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
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @JsonIgnore
    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @JsonProperty("usuario")
    public String getUsuarioUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }

    public Operador() {}

    public Operador(String nombre, Usuario usuario) {
        this.nombre = nombre;
        this.usuario = usuario;
    }

    public Operador(String nombre, String username, String password) {
        this.nombre = nombre;
        this.usuario = new Usuario(username, password, null);
    }

    public Operador(Long id, String nombre, String username, String password) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = new Usuario(username, password, null);
    }

    public String getContrasena() {
        return usuario != null ? usuario.getPassword() : null;
    }
}
