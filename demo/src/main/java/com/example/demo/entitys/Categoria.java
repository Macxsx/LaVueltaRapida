package com.example.demo.entitys;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;



@Data
@Entity
public class Categoria {

    @Id
    //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // nullable, unique, length 
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "categoria_adicional",
        joinColumns = @JoinColumn(name = "categoria_id"),
        inverseJoinColumns = @JoinColumn(name = "adicional_id")
    )
   private List<Adicional> adicionales = new ArrayList<>();
    

    // Constructor vacío (Obligatorio para JPA)
    public Categoria() {
    }

    // Constructor completo
    public Categoria(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Categoria(String name) {
        this.name = name;
    }

   
}