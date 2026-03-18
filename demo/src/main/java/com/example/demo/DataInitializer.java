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
            // 1.1 ADICIONALES
            // ==========================================

            // CLASICAS
            Adicional quesoExtra = adicionalRepo.save(new Adicional("Queso extra", 3000.0, true));
            Adicional pepperoniExtra = adicionalRepo.save(new Adicional("Pepperoni extra", 3500.0, true));
            Adicional jamonExtra = adicionalRepo.save(new Adicional("Jamón extra", 3000.0, true));
            Adicional champinones = adicionalRepo.save(new Adicional("Champiñones", 2500.0, true));
            Adicional aceitunas = adicionalRepo.save(new Adicional("Aceitunas negras", 2500.0, true));
            Adicional maiz = adicionalRepo.save(new Adicional("Maíz dulce", 2000.0, true));
            Adicional pina = adicionalRepo.save(new Adicional("Piña", 2500.0, true));
            Adicional bordeQueso = adicionalRepo.save(new Adicional("Borde relleno de queso", 4000.0, true));
            Adicional salsaExtra = adicionalRepo.save(new Adicional("Salsa extra", 1500.0, true));

            // ESPECIALES
            Adicional tocineta = adicionalRepo.save(new Adicional("Tocineta", 3500.0, true));
            Adicional pollo = adicionalRepo.save(new Adicional("Pollo desmechado", 3500.0, true));
            Adicional carne = adicionalRepo.save(new Adicional("Carne molida", 3500.0, true));
            Adicional bordeCrema = adicionalRepo.save(new Adicional("Borde relleno queso crema", 4500.0, true));
            Adicional salsaBBQ = adicionalRepo.save(new Adicional("Salsa BBQ", 2000.0, true));
            Adicional salsaAjo = adicionalRepo.save(new Adicional("Salsa de ajo", 2000.0, true));

            // PICANTES
            Adicional jalapenos = adicionalRepo.save(new Adicional("Jalapeños", 2500.0, true));
            Adicional aji = adicionalRepo.save(new Adicional("Ají picante", 1500.0, true));
            Adicional chorizoPicante = adicionalRepo.save(new Adicional("Chorizo picante", 3500.0, true));
            Adicional salsaPicante = adicionalRepo.save(new Adicional("Salsa picante", 2000.0, true));

            // BEBIDAS
            Adicional hielo = adicionalRepo.save(new Adicional("Hielo extra", 0.0, true));
            Adicional limon = adicionalRepo.save(new Adicional("Rodaja de limón", 500.0, true));
            Adicional vaso = adicionalRepo.save(new Adicional("Vaso adicional", 0.0, true));

            // POSTRES
            Adicional heladoVainilla = adicionalRepo.save(new Adicional("Helado de vainilla", 3000.0, true));
            Adicional heladoChocolate = adicionalRepo.save(new Adicional("Helado de chocolate", 3000.0, true));
            Adicional salsaChocolate = adicionalRepo.save(new Adicional("Salsa de chocolate", 1500.0, true));
            Adicional salsaCaramelo = adicionalRepo.save(new Adicional("Salsa de caramelo", 1500.0, true));
            Adicional cremaBatida = adicionalRepo.save(new Adicional("Crema batida", 2000.0, true));
            Adicional fresas = adicionalRepo.save(new Adicional("Fresas", 2500.0, true));

            // CLASICAS
            clasicas.getAdicionales().addAll(List.of(
                    quesoExtra, pepperoniExtra, jamonExtra, champinones,
                    aceitunas, maiz, pina, bordeQueso, salsaExtra
            ));

            // ESPECIALES
            especiales.getAdicionales().addAll(List.of(
                    quesoExtra, pepperoniExtra, tocineta, pollo,
                    carne, bordeQueso, bordeCrema, salsaBBQ, salsaAjo
            ));

            // PICANTES
            picantes.getAdicionales().addAll(List.of(
                    jalapenos, aji, chorizoPicante, salsaPicante,
                    quesoExtra, tocineta, bordeQueso
            ));

            // BEBIDAS
            bebidas.getAdicionales().addAll(List.of(
                    hielo, limon, vaso
            ));

            // POSTRES
            postres.getAdicionales().addAll(List.of(
                    heladoVainilla, heladoChocolate,
                    salsaChocolate, salsaCaramelo,
                    cremaBatida, fresas
            ));

            catRepo.save(clasicas);
            catRepo.save(especiales);
            catRepo.save(picantes);
            catRepo.save(bebidas);
            catRepo.save(postres);

            // ==========================================
            // 2. CLÁSICAS
            // ==========================================
            comidaRepo.save(new Comida("Margherita Monza", "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.", 48900, "https://images.unsplash.com/photo-1600891964599-f61ba0e24092", true, clasicas));
            comidaRepo.save(new Comida("Pepperoni Paddock", "Salsa de tomate, mozzarella y pepperoni italiano crujiente.", 54900, "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true, clasicas));
            comidaRepo.save(new Comida("Napolitana Nürburgring", "Tomate, mozzarella, anchoas, aceitunas negras y orégano.", 57900, "https://cocina-casera.com/wp-content/uploads/2023/06/pizza-napolitana.jpeg", true, clasicas));
            comidaRepo.save(new Comida("Cuatro Quesos Qualy", "Mozzarella, gorgonzola, parmesano y provolone.", 59900, "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47", true, clasicas));
            comidaRepo.save(new Comida("Hawaiana Hungaroring", "Jamón ahumado, piña asada y mozzarella.", 54900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true, clasicas));
            comidaRepo.save(new Comida("Vegetariana Valencia", "Pimientos, champiñones, cebolla morada, aceitunas y mozzarella.", 56900, "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e", true, clasicas));
            comidaRepo.save(new Comida("Bianca Barcelona", "Base blanca, ricotta, mozzarella y espinaca fresca.", 57900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, clasicas));
            comidaRepo.save(new Comida("Prosciutto Pole Position", "Prosciutto italiano, rúcula fresca y parmesano.", 64900, "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, clasicas));
            comidaRepo.save(new Comida("Suprema Silverstone", "Pepperoni, jamón, champiñones y pimientos.", 59900, "https://recetinas.com/wp-content/uploads/2022/06/pizza-suprema.jpg", true, clasicas));
            comidaRepo.save(new Comida("Clásica Circuito Central", "Salsa de tomate, doble mozzarella y orégano especial.", 51900, "https://images.unsplash.com/photo-1590947132387-155cc02f3212", true, clasicas));
            comidaRepo.save(new Comida("Bacon Brake Point", "Tocineta ahumada al maple, extra mozzarella y salsa de tomate San Marzano.", 58900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true, clasicas));
            // ==========================================
            // 3. ESPECIALES
            // ==========================================
            comidaRepo.save(new Comida("Turbo Trufa GP", "Salsa blanca, mozzarella, aceite de trufa y champiñones.", 74900, "https://www.calfruitos.com/img/posts/9/0/l/pizza-de-ricotta-tofona-i-poma-1694516318.jpg", true, especiales));
            comidaRepo.save(new Comida("Carbonara Chicane", "Salsa cremosa, panceta crujiente, parmesano y huevo central.", 71900, "https://comedera.com/wp-content/uploads/sites/9/2022/04/pizza-carbonara.jpg", true, especiales));
            comidaRepo.save(new Comida("BBQ Bahrain Boost", "Salsa BBQ, pollo grillado y cebolla caramelizada.", 68900, "https://images.unsplash.com/photo-1541745537411-b8046dc6d66c", true, especiales));
            comidaRepo.save(new Comida("Pesto Pit Stop", "Base pesto, mozzarella, tomates cherry y burrata fresca.", 70900, "https://www.bonella.com.ec/-/media/Project/Upfield/Brands/Rama/Rama-EC/Assets/Recipes/sync-img/0174e966-37d0-44ee-9ff2-035d9038799e.jpg", true, especiales));
            comidaRepo.save(new Comida("Deluxe DRS", "Carne premium, mozzarella, cebolla crispy y salsa especial.", 75900, "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true, especiales));
            comidaRepo.save(new Comida("Overcut de Salmón", "Salmón ahumado premium, crema de eneldo, alcaparras y base crujiente.", 78900, "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, especiales));
            comidaRepo.save(new Comida("Fungi Force Feedback", "Mezcla de hongos silvestres, aceite de ajo negro y tomillo fresco.", 69900, "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47", true, especiales));
            // ==========================================
            // 4. PICANTES
            // ==========================================
            comidaRepo.save(new Comida("Diablo Drag Zone", "Salsa picante, pepperoni, jalapeños y chile seco.", 69900, "https://sharemastro.com/wp-content/uploads/SOFMAS21003_April_May_SocialApril_2_DiabloPizza_1170x618.jpg", true, picantes));
            comidaRepo.save(new Comida("Red Flag Fire", "Salsa arrabbiata, salami picante y guindillas.", 71900, "https://i.ytimg.com/vi/st1N9wmyTbk/maxresdefault.jpg", true, picantes));
            comidaRepo.save(new Comida("México Monaco Heat", "Chorizo picante, jalapeños frescos y salsa roja intensa.", 68900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true, picantes));
            comidaRepo.save(new Comida("Inferno Imola", "Salsa picante extrema, carne sazonada y chile habanero.", 72900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, picantes));
            comidaRepo.save(new Comida("Scuderia Spice", "Pepperoni picante, pimientos asados y salsa especial.", 69900, "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, picantes));
            comidaRepo.save(new Comida("Suzuka Spice S-Curves", "Salsa de curry rojo tailandés, pollo marinado y chiles ojo de pájaro.", 72900, "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true, picantes));
            comidaRepo.save(new Comida("Interlagos Rain Master", "Carne desmechada picante, cebolla morada encurtida y jalapeños rojos.", 73900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, picantes));
            // ==========================================
            // 5. BEBIDAS
            // ==========================================
            comidaRepo.save(new Comida("Podium Cola", "Refresco clásico.", 8900, "https://www.shutterstock.com/image-photo/full-size-cocacola-plastic-bottle-600nw-2642734713.jpg", true, bebidas));
            comidaRepo.save(new Comida("Energy Drink Apex", "Bebida energética premium.", 12900, "https://images.squarespace-cdn.com/content/v1/60cb1bed3b0cf9305037362d/72803b44-751c-4aa7-9f5b-bd4fb5dddb8e/Hero+AdobeStock_505305338_Editorial_Use_Only.png", true, bebidas));
            comidaRepo.save(new Comida("Limonada Lap", "Limonada natural fresca.", 10900, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTIZKj7hitzLIYc6TZ6KwFCSRkgXv77cOkkDA&s", true, bebidas));
            comidaRepo.save(new Comida("Agua Grid", "Agua mineral.", 7900, "https://thumbs.dreamstime.com/b/crystalclear-sparkling-mineral-water-bottle-refreshing-siberian-scene-vibrant-blue-background-experience-invigorating-356031142.jpg", true, bebidas));
            comidaRepo.save(new Comida("Iced Tea Telemetry", "Té frío artesanal.", 10900, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_V47jVOZzy80l7K1MaampaYRnEMLn9YuceQ&s", true, bebidas));
            comidaRepo.save(new Comida("Smooth Operator Shake", "Malteada cremosa de vainilla con trozos de galleta artesanal.", 15900, "https://images.unsplash.com/photo-1572490122747-3968b75cc699", true, bebidas));
            comidaRepo.save(new Comida("Pit Wall Coffee", "Espresso doble con un toque de caramelo salado para mantener la alerta.", 9900, "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04", true, bebidas));
            // ==========================================
            // 6. POSTRES
            // ==========================================
            comidaRepo.save(new Comida("Tiramisú Trophy", "Clásico italiano con café y mascarpone.", 18900, "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9", true, postres));
            comidaRepo.save(new Comida("Brownie Burnout", "Brownie caliente con helado de vainilla.", 20900, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c", true, postres));
            comidaRepo.save(new Comida("Cheesecake Circuit", "Cheesecake cremoso con frutos rojos.", 18900, "https://images.unsplash.com/photo-1551024601-bec78aea704b", true, postres));
            comidaRepo.save(new Comida("Lava Cake Launch", "Pastel de chocolate con centro líquido.", 21900, "https://www.verybestbaking.com/sites/g/files/jgfbjl326/files/styles/large/public/recipe-thumbnail/105677-2020_06_23T12_02_56_mrs_ImageRecipes_147148lrg.jpg", true, postres));
            comidaRepo.save(new Comida("Panna Cotta Pitlane", "Panna cotta con salsa de frutos del bosque.", 17900, "https://images.unsplash.com/photo-1565958011703-44f9829ba187", true, postres));
            comidaRepo.save(new Comida("Chassis Chocolate Cake", "Bizcocho denso de chocolate 70% cacao con estructura de ganache.", 22900, "https://images.unsplash.com/photo-1578985545062-69928b1d9587", true, postres));
            comidaRepo.save(new Comida("Aero Apple Tart", "Tarta de manzana fina con láminas dispuestas aerodinámicamente.", 19900, "https://images.unsplash.com/photo-1568571780765-9276ac8b75a2", true, postres));
            comidaRepo.save(new Comida("Monaco Macarons (Set de 3)", "Macarons de pistacho, frambuesa y limón, elegantes como el paddock.", 24900, "https://www.hola.com/horizon/landscape/f8694228cbe2-macarons-t.jpg", true, postres));

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
            // 5. CARRITOS (uno por cliente)
            // ==========================================
            clienteRepo.findAll().forEach(c -> {
                Carrito carrito = new Carrito(c);
                carrito.setActivo(false);
                carritoRepo.save(carrito);
            });

            // ==========================================
            // 6. OPERADORES
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
