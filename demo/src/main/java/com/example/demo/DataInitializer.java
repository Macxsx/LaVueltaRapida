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
import com.example.demo.entitys.MetodoPago;
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
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("default")
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
            PedidoRepository pedidoRepo,
            PasswordEncoder encoder
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
            comidaRepo.save(new Comida("Margherita Monza", "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.", 48900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303592/Margherita_Monza_zbrnpy.png", true, clasicas));
            comidaRepo.save(new Comida("Pepperoni Paddock", "Salsa de tomate, mozzarella y pepperoni italiano crujiente.", 54900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303592/Pepperoni_Paddock_mmial0.png", true, clasicas));
            comidaRepo.save(new Comida("Napolitana Nürburgring", "Tomate, mozzarella, anchoas, aceitunas negras y orégano.", 57900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303592/Napolitana_N%C3%BCrburgring_slqfhu.png", true, clasicas));
            comidaRepo.save(new Comida("Cuatro Quesos Qualy", "Mozzarella, gorgonzola, parmesano y provolone.", 59900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303591/Cuatro_Quesos_Qualy_rt7fto.png", true, clasicas));
            comidaRepo.save(new Comida("Hawaiana Hungaroring", "Jamón ahumado, piña asada y mozzarella.", 54900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303591/Hawaiana_Hungaroring_usmcnr.png", true, clasicas));
            comidaRepo.save(new Comida("Vegetariana Valencia", "Pimientos, champiñones, cebolla morada, aceitunas y mozzarella.", 56900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303591/Vegetariana_Valencia_hrj9qx.png", true, clasicas));
            comidaRepo.save(new Comida("Bianca Barcelona", "Base blanca, ricotta, mozzarella y espinaca fresca.", 57900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303591/Bianca_Barcelona_h4wlba.png", true, clasicas));
            comidaRepo.save(new Comida("Prosciutto Pole Position", "Prosciutto italiano, rúcula fresca y parmesano.", 64900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303591/Prosciutto_Pole_Position_dgzw1l.png", true, clasicas));
            comidaRepo.save(new Comida("Suprema Silverstone", "Pepperoni, jamón, champiñones y pimientos.", 59900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303590/Suprema_Silverstone_udkmhg.png", true, clasicas));
            comidaRepo.save(new Comida("Clásica Circuito Central", "Salsa de tomate, doble mozzarella y orégano especial.", 51900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303591/Cl%C3%A1sica_Circuito_Central_vnwjbx.png", true, clasicas));
            comidaRepo.save(new Comida("Bacon Brake Point", "Tocineta ahumada al maple, extra mozzarella y salsa de tomate San Marzano.", 58900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777303590/Bacon_Brake_Point_i5rbp8.png", true, clasicas));
            // ==========================================
            // 3. ESPECIALES
            // ==========================================
            comidaRepo.save(new Comida("Turbo Trufa GP", "Salsa blanca, mozzarella, aceite de trufa y champiñones.", 74900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307155/Turbo_Trufa_GP_t8fc3q.png", true, especiales));
            comidaRepo.save(new Comida("Carbonara Chicane", "Salsa cremosa, panceta crujiente, parmesano y huevo central.", 71900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307154/Carbonara_Chicane_ugh8zh.png", true, especiales));
            comidaRepo.save(new Comida("BBQ Bahrain Boost", "Salsa BBQ, pollo grillado y cebolla caramelizada.", 68900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307153/BBQ_Bahrain_Boost_sqnuve.png", true, especiales));
            comidaRepo.save(new Comida("Pesto Pit Stop", "Base pesto, mozzarella, tomates cherry y burrata fresca.", 70900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307154/Pesto_Pit_Stop_zx3ibd.png", true, especiales));
            comidaRepo.save(new Comida("Deluxe DRS", "Carne premium, mozzarella, cebolla crispy y salsa especial.", 75900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307153/Deluxe_DRS_ra23xs.png", true, especiales));
            comidaRepo.save(new Comida("Overcut de Salmón", "Salmón ahumado premium, crema de eneldo, alcaparras y base crujiente.", 78900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307152/Overcut_de_Salm%C3%B3n_bajmj1.png", true, especiales));
            comidaRepo.save(new Comida("Fungi Force Feedback", "Mezcla de hongos silvestres, aceite de ajo negro y tomillo fresco.", 69900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307152/Fungi_Force_Feedback_rweqkv.png", true, especiales));
            // ==========================================
            // 4. PICANTES
            // ==========================================
            comidaRepo.save(new Comida("Diablo Drag Zone", "Salsa picante, pepperoni, jalapeños y chile seco.", 69900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307191/Diablo_Drag_Zone_ujzqeo.png", true, picantes));
            comidaRepo.save(new Comida("Red Flag Fire", "Salsa arrabbiata, salami picante y guindillas.", 71900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307190/Red_Flag_Fire_z6n21b.png", true, picantes));
            comidaRepo.save(new Comida("México Monaco Heat", "Chorizo picante, jalapeños frescos y salsa roja intensa.", 68900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307189/M%C3%A9xico_Monaco_Heat_flcwwc.png", true, picantes));
            comidaRepo.save(new Comida("Inferno Imola", "Salsa picante extrema, carne sazonada y chile habanero.", 72900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307189/Inferno_Imola_tgc8ub.png", true, picantes));
            comidaRepo.save(new Comida("Scuderia Spice", "Pepperoni picante, pimientos asados y salsa especial.", 69900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307188/Scuderia_Spice_lhe5yj.png", true, picantes));
            comidaRepo.save(new Comida("Suzuka Spice S-Curves", "Salsa de curry rojo tailandés, pollo marinado y chiles ojo de pájaro.", 72900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307187/Suzuka_Spice_S-Curves_jbblg2.png", true, picantes));
            comidaRepo.save(new Comida("Interlagos Rain Master", "Carne desmechada picante, cebolla morada encurtida y jalapeños rojos.", 73900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777307186/Interlagos_Rain_Master_fxgpmk.png", true, picantes));
            // ==========================================
            // 5. BEBIDAS
            // ==========================================
            comidaRepo.save(new Comida("Podium Cola", "Refresco clásico.", 8900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310648/Podium_Cola_vgtmqk.png", true, bebidas));
            comidaRepo.save(new Comida("Energy Drink Apex", "Bebida energética premium.", 12900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310646/Energy_Drink_Apex_q0xqlg.png", true, bebidas));
            comidaRepo.save(new Comida("Limonada Lap", "Limonada natural fresca.", 10900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310645/Limonada_Lap_wxetkv.png", true, bebidas));
            comidaRepo.save(new Comida("Agua Grid", "Agua mineral.", 7900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310645/Agua_Grid_i5quku.png", true, bebidas));
            comidaRepo.save(new Comida("Iced Tea Telemetry", "Té frío artesanal.", 10900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310645/Iced_Tea_Telemetry_ojwagy.png", true, bebidas));
            comidaRepo.save(new Comida("Smooth Operator Shake", "Malteada cremosa de vainilla con trozos de galleta artesanal.", 15900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310644/Smooth_Operator_Shake_vh7unw.png", true, bebidas));
            comidaRepo.save(new Comida("Pit Wall Coffee", "Espresso doble con un toque de caramelo salado para mantener la alerta.", 9900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777310644/Pit_Wall_Coffee_jkfm4o.png", true, bebidas));
            // ==========================================
            // 6. POSTRES
            // ==========================================
            comidaRepo.save(new Comida("Tiramisú Trophy", "Clásico italiano con café y mascarpone.", 18900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313144/Tiramis%C3%BA_Trophy_k2cqxj.png", true, postres));
            comidaRepo.save(new Comida("Brownie Burnout", "Brownie caliente con helado de vainilla.", 20900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313143/Brownie_Burnout_vns6gd.png", true, postres));
            comidaRepo.save(new Comida("Cheesecake Circuit", "Cheesecake cremoso con frutos rojos.", 18900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313143/Cheesecake_Circuit_ltqhb6.png", true, postres));
            comidaRepo.save(new Comida("Lava Cake Launch", "Pastel de chocolate con centro líquido.", 21900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313143/Lava_Cake_Launch_yqbdwp.png", true, postres));
            comidaRepo.save(new Comida("Panna Cotta Pitlane", "Panna cotta con salsa de frutos del bosque.", 17900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313143/Panna_Cotta_Pitlane_xivr6d.png", true, postres));
            comidaRepo.save(new Comida("Chassis Chocolate Cake", "Bizcocho denso de chocolate 70% cacao con estructura de ganache.", 22900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313142/Chassis_Chocolate_Cake_qujuy3.png", true, postres));
            comidaRepo.save(new Comida("Aero Apple Tart", "Tarta de manzana fina con láminas dispuestas aerodinámicamente.", 19900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313142/Aero_Apple_Tart_tctmrj.png", true, postres));
            comidaRepo.save(new Comida("Monaco Macarons (Set de 3)", "Macarons de pistacho, frambuesa y limón, elegantes como el paddock.", 24900, "https://res.cloudinary.com/diuxjivje/image/upload/v1777313142/Monaco_Macarons_gshgmt.png", true, postres));

            // ==========================================
            // 4. CLIENTES
            // ==========================================
            clienteRepo.save(new Cliente("Pablo",    "García",    "oboetube123@gmail.com",        "pablo123",  encoder.encode("123456"),    "Cra 7 #40-62, Bogotá",        "3001234567"));
            clienteRepo.save(new Cliente("María",    "Gómez",     "maria.gomez@email.com",          "maria123",  encoder.encode("maria2024"), "Cl 45 #12-30, Medellín",      "3019876543"));
            clienteRepo.save(new Cliente("Andrés",   "Martínez",  "andres.martinez@email.com",      "andres123", encoder.encode("andres789"), "Av 68 #23-10, Cali",          "3024567890"));
            clienteRepo.save(new Cliente("Laura",    "Ramírez",   "laura.ramirez@email.com",        "laura123",  encoder.encode("lauraPass"), "Cra 15 #88-21, Barranquilla", "3106543210"));
            clienteRepo.save(new Cliente("Camilo",   "Torres",    "camilo.torres@email.com",        "camilito20",encoder.encode("camilo123"), "Cl 100 #19-50, Bucaramanga",  "3157891234"));
            clienteRepo.save(new Cliente("Valentina","López",     "valentina.lopez@email.com",      "vale456",   encoder.encode("valePass1"), "Cra 50 #32-15, Pereira",      "3201234567"));
            clienteRepo.save(new Cliente("Santiago", "Hernández", "santiago.hernandez@email.com",   "santi789",  encoder.encode("santiClave"),"Cl 72 #10-45, Cartagena",     "3112345678"));
            clienteRepo.save(new Cliente("Daniela",  "Castro",    "daniela.castro@email.com",       "dani321",   encoder.encode("daniSecure"),"Av 30 #15-80, Manizales",     "3009876543"));
            clienteRepo.save(new Cliente("Sebastián","Morales",   "sebastian.morales@email.com",    "sebas007",  encoder.encode("sebasKey"),  "Cra 25 #60-12, Santa Marta",  "3178901234"));
            clienteRepo.save(new Cliente("Carolina", "Díaz",      "carolina.diaz@email.com",        "caro2024",  encoder.encode("caroPass"),  "Cl 85 #42-30, Ibagué",        "3145678901"));

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
            String pw123 = encoder.encode("123");
            operadorRepo.save(new Operador("Carlos Ruiz",       "op1",  pw123));
            operadorRepo.save(new Operador("Laura Sánchez",     "op2",  pw123));
            operadorRepo.save(new Operador("Andrés Mora",       "op3",  pw123));
            operadorRepo.save(new Operador("Sofía Vargas",      "op4",  pw123));
            operadorRepo.save(new Operador("Juan Prada",        "op5",  pw123));
            operadorRepo.save(new Operador("Natalia Cárdenas",  "op6",  pw123));
            operadorRepo.save(new Operador("Ricardo Peña",      "op7",  pw123));
            operadorRepo.save(new Operador("Valentina Cruz",    "op8",  pw123));
            operadorRepo.save(new Operador("Felipe Gómez",      "op9",  pw123));
            operadorRepo.save(new Operador("Mariana Ospina",    "op10", pw123));
            operadorRepo.save(new Operador("Diego Salcedo",     "op11", pw123));
            operadorRepo.save(new Operador("Isabela Ríos",      "op12", pw123));
            operadorRepo.save(new Operador("Tomás Bejarano",    "op13", pw123));
            operadorRepo.save(new Operador("Lucía Montoya",     "op14", pw123));
            operadorRepo.save(new Operador("Esteban Guerrero",  "op15", pw123));
            operadorRepo.save(new Operador("Paula Herrera",     "op16", pw123));
            operadorRepo.save(new Operador("Julián Acosta",     "op17", pw123));
            operadorRepo.save(new Operador("Camila Nieto",      "op18", pw123));
            operadorRepo.save(new Operador("Alejandro Duque",   "op19", pw123));
            operadorRepo.save(new Operador("Sara Quintero",     "op20", pw123));

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
            adminRepo.save(new Administrador("admin1", pw123));
            adminRepo.save(new Administrador("admin2", pw123));
            adminRepo.save(new Administrador("admin3", pw123));
            adminRepo.save(new Administrador("admin4", pw123));
            adminRepo.save(new Administrador("admin5", pw123));

            // ==========================================
            // 8. PEDIDOS DE EJEMPLO (20 pedidos)
            // ==========================================
            List<Cliente> clientes = clienteRepo.findAll();
            List<Comida> comidas   = comidaRepo.findAll();
            List<Domiciliario> domiciliarios = domiciliarioRepo.findAll();
            List<Adicional> adicionales = adicionalRepo.findAll();

            int[][] pedidoConfig = {
                // { clienteIdx, comidaIdx, adicionalIdx, estadoOrdinal, minutosAtras, domIdx }
                // estadoOrdinal: 0=RECIBIDO, 1=COCINANDO, 2=ENVIADO, 3=ENTREGADO
                {0, 0,  0, 3, 90, 0},
                {1, 3,  1, 3, 75, 1},
                {2, 6,  2, 3, 60, 2},
                {3, 10, 3, 3, 50, 3},
                {4, 13, 4, 3, 45, 4},
                {5, 16, 5, 3, 40, 0},
                {6, 19, 6, 3, 35, 1},
                {7, 22, 7, 3, 30, 2},
                {8, 25, 8, 3, 25, 3},
                {9, 28, 9, 3, 20, 4},
                {0, 2,  0, 2, 15, 0},
                {1, 5,  1, 2, 12, 1},
                {2, 8,  2, 2, 10, 2},
                {3, 11, 3, 1, 8,  3},
                {4, 14, 4, 1, 6,  4},
                {5, 17, 5, 1, 5,  0},
                {6, 20, 6, 0, 3, -1},
                {7, 23, 7, 0, 2, -1},
                {8, 26, 8, 0, 1, -1},
                {9, 29, 9, 0, 0, -1}
            };

            EstadoPedido[] estados = EstadoPedido.values();

            for (int[] cfg : pedidoConfig) {
                int clienteIdx   = cfg[0];
                int comidaIdx    = cfg[1] % comidas.size();
                int adicionalIdx = cfg[2] % adicionales.size();
                EstadoPedido estado = estados[cfg[3]];
                long minutosAtras = cfg[4];
                int domIdx = cfg[5];

                Pedido pedido = new Pedido();
                pedido.setCliente(clientes.get(clienteIdx));
                pedido.setEstado(estado);
                pedido.setFechaCreacion(LocalDateTime.now().minusMinutes(minutosAtras));
                pedido.setMetodoPago(MetodoPago.EFECTIVO);

                if (estado == EstadoPedido.ENTREGADO) {
                    pedido.setFechaEntrega(LocalDateTime.now().minusMinutes(minutosAtras / 3));
                    pedido.setEstadoPago("APROBADO");
                    pedido.setFechaPago(LocalDateTime.now().minusMinutes(minutosAtras / 3));
                }
                // Invariante: solo los pedidos en estado ENVIADO pueden tener
                // domiciliario asignado, y ese domiciliario queda ocupado.
                if (estado == EstadoPedido.ENVIADO && domIdx >= 0) {
                    Domiciliario d = domiciliarios.get(domIdx);
                    d.setDisponible(false);
                    domiciliarioRepo.save(d);
                    pedido.setDomiciliario(d);
                }

                pedidoRepo.save(pedido);

                LineaPedido linea = new LineaPedido();
                linea.setPedido(pedido);
                linea.setComida(comidas.get(comidaIdx));
                linea.setCantidad(1 + (comidaIdx % 3));
                lineaPedidoRepo.save(linea);

                LineaPedidoAdicional lpa = new LineaPedidoAdicional();
                lpa.setLineaPedido(linea);
                lpa.setAdicional(adicionales.get(adicionalIdx));
                lineaPedidoAdicionalRepo.save(lpa);
            }

            System.out.println("¡Semáforo en verde! Base de datos de LaVueltaRapida inicializada correctamente.");
        };
    }
}
