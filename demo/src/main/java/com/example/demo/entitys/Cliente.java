package com.example.demo.entitys;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 30)
    private String username;
    @Column(name = "contrasena", nullable = false)
    private String password;
    private String direccion;
    private String telefono;

    @OneToOne
    @JoinColumn(name = "cliente")
    private Carrito carrito;

    public Cliente() {
    }

    public Cliente(String name, String apellido, String email, String username, String password, String direccion, String telefono) {
        this.name = name;
        this.apellido = apellido;
        this.email = email;
        this.username = username;
        this.password = password;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Cliente(String name, String apellido, String email, String username, String password, String direccion, String telefono, Carrito carrito) {
        this.name = name;
        this.apellido = apellido;
        this.email = email;
        this.username = username;
        this.password = password;
        this.direccion = direccion;
        this.telefono = telefono;
        this.carrito = carrito;
    }

    public Cliente(Long id, String name, String apellido, String email, String username, String password, String direccion, String telefono, Carrito carrito) {
        this.id = id;
        this.name = name;
        this.apellido = apellido;
        this.email = email;
        this.username = username;
        this.password = password;
        this.direccion = direccion;
        this.telefono = telefono;
        this.carrito = carrito;
    }
}
