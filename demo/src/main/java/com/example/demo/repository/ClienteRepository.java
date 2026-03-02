package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.demo.entitys.Cliente;

@Repository
public class ClienteRepository {
    
    private final Map<Integer, Cliente> clientes = new HashMap<>();

    public ClienteRepository() {
        clientes.put(1, new Cliente(
        1,
        "Pablo",
        "García",
        "PabloGarcia21@gmail.com",
        "pablo123",
        "123456",
        "Cra 7 #40-62, Bogotá",
        "3001234567"
    ));

        clientes.put(2, new Cliente(
                2,
                "María",
                "Gómez",
                "maria.gomez@email.com",
                "maria123",
                "maria2024",
                "Cl 45 #12-30, Medellín",
                "3019876543"
        ));

        clientes.put(3, new Cliente(
                3,
                "Andrés",
                "Martínez",
                "andres.martinez@email.com",
                "andres123",
                "andres789",
                "Av 68 #23-10, Cali",
                "3024567890"
        ));

        clientes.put(4, new Cliente(
                4,
                "Laura",
                "Ramírez",
                "laura.ramirez@email.com",
                "laura123",
                "lauraPass",
                "Cra 15 #88-21, Barranquilla",
                "3106543210"
        ));

        clientes.put(5, new Cliente(
                5,
                "Camilo",
                "Torres",
                "camilo.torres@email.com",
                "camilito20",
                "camilo123",
                "Cl 100 #19-50, Bucaramanga",
                "3157891234"
        ));
    }
        
    public Cliente findById(Integer id) {
        return clientes.get(id);
    }

    public Collection<Cliente> findAll() {
        return clientes.values();

    }

    public Integer count() {
        return clientes.size();
    }
    public void save(Cliente cliente) {
        if(cliente.getId()== null){
        Integer tam = count();
        Integer lastid = clientes.get(tam).getId();
        cliente.setId(lastid + 1);
        clientes.put(cliente.getId(), cliente);
        }
        else{
            clientes.put(cliente.getId(), cliente);
        }
    }
    
    public void deleteById(Integer id) {
        clientes.remove(id);
    }

    public Cliente findByUsername(String username) {
        return clientes.values().stream()
                .filter(cliente -> cliente.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
