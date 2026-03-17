package com.example.demo;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.entitys.Operador;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.LineaPedidoAdicionalRepository;
import com.example.demo.repository.LineaPedidoRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.PedidoRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            ComidaRepository comidaRepo,
            CategoriaRepository catRepo,
            ClienteRepository clienteRepo,
            AdicionalRepository adicionalRepo,
            OperadorRepository operadorRepo,
            DomiciliarioRepository domiciliarioRepo,
            AdministradorRepository adminRepo,
            CarritoRepository carritoRepo,
            LineaPedidoRepository lineaPedidoRepo,
            LineaPedidoAdicionalRepository lineaPedidoAdicionalRepo,
            PedidoRepository pedidoRepo
    ) {
        return args -> {

            // ==========================================
            // 1. CATEGORÍAS
            // ==========================================
            Categoria clasicas  = catRepo.save(new Categoria("Clásicas"));
            Categoria especiales = catRepo.save(new Categoria("Especiales"));
            Categoria picantes  = catRepo.save(new Categoria("Picantes"));
            Categoria bebidas   = catRepo.save(new Categoria("Bebidas"));
            Categoria postres   = catRepo.save(new Categoria("Postres"));

            // ==========================================
            // 2. ADICIONALES
            // ==========================================
            Adicional quesoExtra     = adicionalRepo.save(new Adicional("Queso extra",        3000.0, true));
            Adicional pepperoniExtra = adicionalRepo.save(new Adicional("Pepperoni extra",    3500.0, true));
            Adicional jalapenos      = adicionalRepo.save(new Adicional("Jalapeños",          2500.0, true));
            Adicional salsaBBQ       = adicionalRepo.save(new Adicional("Salsa BBQ",          2000.0, true));
            Adicional helado         = adicionalRepo.save(new Adicional("Helado de vainilla", 3000.0, true));

            clasicas.getAdicionales().addAll(List.of(quesoExtra, pepperoniExtra));
            especiales.getAdicionales().addAll(List.of(quesoExtra, salsaBBQ));
            picantes.getAdicionales().addAll(List.of(jalapenos));
            postres.getAdicionales().addAll(List.of(helado));
            catRepo.saveAll(List.of(clasicas, especiales, picantes, bebidas, postres));

            // ==========================================
            // 3. PRODUCTOS
            // ==========================================
            comidaRepo.save(new Comida("Margherita Monza",    "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.",                     48900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400", true,  clasicas));
            comidaRepo.save(new Comida("Pepperoni Paddock",   "Salsa de tomate y mozzarella italiano crujiente.",                                                 52900, "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400", true,  clasicas));
            comidaRepo.save(new Comida("Cuatro Quesos GP",    "Mozzarella, gorgonzola, parmesano y queso de cabra sobre base de crema.",                         56900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=400", true,  clasicas));
            comidaRepo.save(new Comida("BBQ Pit Stop",        "Pollo ahumado, cebolla caramelizada, pimiento rojo y salsa BBQ artesanal.",                       58900, "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400", true,  especiales));
            comidaRepo.save(new Comida("Trufa Pole Position", "Base de crema de trufa negra, champiñones salteados, rúcula fresca y parmesano.",                 67900, "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400", true,  especiales));
            comidaRepo.save(new Comida("Volcán Rojo",         "Jalapeños frescos, chorizo picante, salsa sriracha y queso pepper jack.",                         54900, "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?w=400", true,  picantes));
            comidaRepo.save(new Comida("Diavola Racing",      "Salame piccante importado, chili, aceitunas negras y mozzarella ahumada.",                        57900, "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?w=400", true,  picantes));
            comidaRepo.save(new Comida("Limonada Pit Lane",   "Limonada natural con menta fresca. Refrescante como una vuelta rápida.",                          12900, "https://images.unsplash.com/photo-1621263764928-df1444c5e859?w=400", true,  bebidas));
            comidaRepo.save(new Comida("Cola del Podio",      "Gaseosa importada servida en vaso helado con hielo.",                                             8900,  "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400", true,  bebidas));
            comidaRepo.save(new Comida("Tiramisu Trophy",     "Clásico tiramisú italiano con espresso, mascarpone y cacao en polvo.",                            18900, "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400", true,  postres));
            comidaRepo.save(new Comida("Panna Cotta F1",      "Panna cotta de vainilla con coulis de frutos rojos y menta.",                                     16900, "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400", true,  postres));

            // ==========================================
            // 4. CLIENTES
            // ==========================================
            clienteRepo.save(new Cliente("Pablo",    "García",    "PabloGarcia21@gmail.com",        "pablo123",  "123456",    "Cra 7 #40-62, Bogotá",        "3001234567"));
            clienteRepo.save(new Cliente("María",    "Gómez",     "maria.gomez@email.com",          "maria123",  "maria2024", "Cl 45 #12-30, Medellín",      "3019876543"));
            clienteRepo.save(new Cliente("Andrés",   "Martínez",  "andres.martinez@email.com",      "andres123", "andres789", "Av 68 #23-10, Cali",          "3024567890"));
            clienteRepo.save(new Cliente("Laura",    "Ramírez",   "laura.ramirez@email.com",        "laura123",  "lauraPass", "Cra 15 #88-21, Barranquilla", "3106543210"));
            clienteRepo.save(new Cliente("Camilo",   "Torres",    "camilo.torres@email.com",        "camilito20","camilo123", "Cl 100 #19-50, Bucaramanga",  "3157891234"));
            clienteRepo.save(new Cliente("Valentina","López",     "valentina.lopez@email.com",      "vale456",   "valePass1", "Cra 50 #32-15, Pereira",      "3201234567"));
            clienteRepo.save(new Cliente("Santiago", "Hernández", "santiago.hernandez@email.com",   "santi789",  "santiClave","Cl 72 #10-45, Cartagena",     "3112345678"));
            clienteRepo.save(new Cliente("Daniela",  "Castro",    "daniela.castro@email.com",       "dani321",   "daniSecure","Av 30 #15-80, Manizales",     "3009876543"));
            clienteRepo.save(new Cliente("Sebastián","Morales",   "sebastian.morales@email.com",    "sebas007",  "sebasKey",  "Cra 25 #60-12, Santa Marta",  "3178901234"));
            clienteRepo.save(new Cliente("Carolina", "Díaz",      "carolina.diaz@email.com",        "caro2024",  "caroPass",  "Cl 85 #42-30, Ibagué",        "3145678901"));

            // ==========================================
            // 5. OPERADORES
            // ==========================================
            operadorRepo.save(new Operador("Carlos Ruiz",    "op1", "123"));
            operadorRepo.save(new Operador("Laura Sánchez",  "op2", "123"));
            operadorRepo.save(new Operador("Andrés Mora",    "op3", "123"));
            operadorRepo.save(new Operador("Sofía Vargas",   "op4", "123"));
            operadorRepo.save(new Operador("Juan Prada",     "op5", "123"));

            // ==========================================
            // 6. DOMICILIARIOS
            // ==========================================
            domiciliarioRepo.save(new Domiciliario("Luis Ramírez",   "1001001", "3001111111", true));
            domiciliarioRepo.save(new Domiciliario("Pedro Suárez",   "1002002", "3002222222", true));
            domiciliarioRepo.save(new Domiciliario("Miguel Ángel",   "1003003", "3003333333", true));
            domiciliarioRepo.save(new Domiciliario("Camila Reyes",   "1004004", "3004444444", true));
            domiciliarioRepo.save(new Domiciliario("Valeria Torres", "1005005", "3005555555", true));

            // ==========================================
            // 7. ADMINISTRADORES
            // ==========================================
            adminRepo.save(new Administrador("admin1", "123"));
            adminRepo.save(new Administrador("admin2", "123"));
            adminRepo.save(new Administrador("admin3", "123"));
            adminRepo.save(new Administrador("admin4", "123"));
            adminRepo.save(new Administrador("admin5", "123"));

            // ==========================================
            // 8. PEDIDOS DE EJEMPLO
            // ==========================================
            List<Cliente> clientes = clienteRepo.findAll();
            List<Comida> comidas   = comidaRepo.findAll();
            List<Domiciliario> domiciliarios = domiciliarioRepo.findAll();

            for (int i = 0; i < 3; i++) {
                Pedido pedido = new Pedido();
                pedido.setCliente(clientes.get(i));
                pedido.setEstado(EstadoPedido.RECIBIDO);
                pedido.setFechaCreacion(LocalDateTime.now());
                pedidoRepo.save(pedido);

                LineaPedido linea = new LineaPedido();
                linea.setPedido(pedido);
                linea.setComida(comidas.get(i));
                linea.setCantidad(1);
                lineaPedidoRepo.save(linea);

                LineaPedidoAdicional lpa = new LineaPedidoAdicional();
                lpa.setLineaPedido(linea);
                lpa.setAdicional(quesoExtra);
                lineaPedidoAdicionalRepo.save(lpa);

                pedido.setEstado(EstadoPedido.COCINANDO);
                pedido.setDomiciliario(domiciliarios.get(i));
                domiciliarios.get(i).setDisponible(false);
                domiciliarioRepo.save(domiciliarios.get(i));

                pedido.setEstado(EstadoPedido.ENVIADO);
                pedido.setEstado(EstadoPedido.ENTREGADO);
                pedido.setFechaEntrega(LocalDateTime.now());
                domiciliarios.get(i).setDisponible(true);
                domiciliarioRepo.save(domiciliarios.get(i));
                pedidoRepo.save(pedido);
            }

            System.out.println("¡Semáforo en verde! Base de datos de LaVueltaRapida inicializada correctamente.");
        };
    }
}
