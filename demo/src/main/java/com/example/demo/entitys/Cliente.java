package com.example.demo.entitys;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String name;

    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private String direccion;
    private String telefono;

    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", unique = true)
    @JsonIgnoreProperties("password")
    private Usuario usuario;

    public Cliente() {}

    public Cliente(String name, String apellido, String email, Usuario usuario,
                   String direccion, String telefono) {
        this.name = name;
        this.apellido = apellido;
        this.email = email;
        this.usuario = usuario;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Cliente(String name, String apellido, String email,
                   String username, String password,
                   String direccion, String telefono) {
        this.name = name;
        this.apellido = apellido;
        this.email = email;
        this.usuario = new Usuario(username, password, null);
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public String getUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }

    public void setUsername(String username) {
        if (this.usuario == null) this.usuario = new Usuario();
        this.usuario.setUsername(username);
    }

    public String getPassword() {
        return usuario != null ? usuario.getPassword() : null;
    }

    public void setPassword(String password) {
        if (this.usuario == null) this.usuario = new Usuario();
        this.usuario.setPassword(password);
    }

    public boolean isActivo() {
        return usuario != null && usuario.isActivo();
    }

    public void setActivo(boolean activo) {
        if (usuario != null) usuario.setActivo(activo);
    }
}
