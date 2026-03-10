package com;

import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Comida;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ComidaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ComidaRepository comidaRepo, CategoriaRepository catRepo, ClienteRepository clienteRepo) {
        return args -> {
            // ==========================================
            // 1. CREACIÓN DE CATEGORÍAS
            // ==========================================
            Categoria clasicas = catRepo.save(new Categoria(1L, "Clásicas"));
            Categoria especiales = catRepo.save(new Categoria(2L, "Especiales"));
            Categoria picantes = catRepo.save(new Categoria(3L, "Picantes"));
            Categoria bebidas = catRepo.save(new Categoria(4L, "Bebidas"));
            Categoria postres = catRepo.save(new Categoria(5L, "Postres"));

            // ==========================================
            // 2. 🍕 CLÁSICAS (1–10)
            // ==========================================
            comidaRepo.save(new Comida(1L, "Margherita Monza", "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.", 48900, "https://images.unsplash.com/photo-1600891964599-f61ba0e24092", true, clasicas));
            comidaRepo.save(new Comida(2L, "Pepperoni Paddock", "Salsa de tomate, mozzarella y pepperoni italiano crujiente.", 54900, "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true, clasicas));
            comidaRepo.save(new Comida(3L, "Napolitana Nürburgring", "Tomate, mozzarella, anchoas, aceitunas negras y orégano.", 57900, "https://cocina-casera.com/wp-content/uploads/2023/06/pizza-napolitana.jpeg", true, clasicas));
            comidaRepo.save(new Comida(4L, "Cuatro Quesos Qualy", "Mozzarella, gorgonzola, parmesano y provolone.", 59900, "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47", true, clasicas));
            comidaRepo.save(new Comida(5L, "Hawaiana Hungaroring", "Jamón ahumado, piña asada y mozzarella.", 54900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true, clasicas));
            comidaRepo.save(new Comida(6L, "Vegetariana Valencia", "Pimientos, champiñones, cebolla morada, aceitunas y mozzarella.", 56900, "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e", true, clasicas));
            comidaRepo.save(new Comida(7L, "Bianca Barcelona", "Base blanca, ricotta, mozzarella y espinaca fresca.", 57900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, clasicas));
            comidaRepo.save(new Comida(8L, "Prosciutto Pole Position", "Prosciutto italiano, rúcula fresca y parmesano.", 64900, "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, clasicas));
            comidaRepo.save(new Comida(9L, "Suprema Silverstone", "Pepperoni, jamón, champiñones y pimientos.", 59900, "https://recetinas.com/wp-content/uploads/2022/06/pizza-suprema.jpg", true, clasicas));
            comidaRepo.save(new Comida(10L, "Clásica Circuito Central", "Salsa de tomate, doble mozzarella y orégano especial.", 51900, "https://images.unsplash.com/photo-1590947132387-155cc02f3212", true, clasicas));
            comidaRepo.save(new Comida(35L, "Bacon Brake Point", "Tocineta ahumada al maple, extra mozzarella y salsa de tomate San Marzano.", 58900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true, clasicas));
            // ==========================================
            // 3. ⭐ ESPECIALES (11–15)
            // ==========================================
            comidaRepo.save(new Comida(11L, "Turbo Trufa GP", "Salsa blanca, mozzarella, aceite de trufa y champiñones.", 74900, "https://www.calfruitos.com/img/posts/9/0/l/pizza-de-ricotta-tofona-i-poma-1694516318.jpg", true, especiales));
            comidaRepo.save(new Comida(12L, "Carbonara Chicane", "Salsa cremosa, panceta crujiente, parmesano y huevo central.", 71900, "https://comedera.com/wp-content/uploads/sites/9/2022/04/pizza-carbonara.jpg", true, especiales));
            comidaRepo.save(new Comida(13L, "BBQ Bahrain Boost", "Salsa BBQ, pollo grillado y cebolla caramelizada.", 68900, "https://images.unsplash.com/photo-1541745537411-b8046dc6d66c", true, especiales));
            comidaRepo.save(new Comida(14L, "Pesto Pit Stop", "Base pesto, mozzarella, tomates cherry y burrata fresca.", 70900, "https://www.bonella.com.ec/-/media/Project/Upfield/Brands/Rama/Rama-EC/Assets/Recipes/sync-img/0174e966-37d0-44ee-9ff2-035d9038799e.jpg", true, especiales));
            comidaRepo.save(new Comida(15L, "Deluxe DRS", "Carne premium, mozzarella, cebolla crispy y salsa especial.", 75900, "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true, especiales));
            
            comidaRepo.save(new Comida(31L, "Overcut de Salmón", "Salmón ahumado premium, crema de eneldo, alcaparras y base crujiente.", 78900, "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, especiales));
            comidaRepo.save(new Comida(32L, "Fungi Force Feedback", "Mezcla de hongos silvestres, aceite de ajo negro y tomillo fresco.",69900, "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47", true, especiales));
            // ==========================================
            // 4. 🌶️ PICANTES (16–20)
            // ==========================================
            comidaRepo.save(new Comida(16L, "Diablo Drag Zone", "Salsa picante, pepperoni, jalapeños y chile seco.", 69900, "https://sharemastro.com/wp-content/uploads/SOFMAS21003_April_May_SocialApril_2_DiabloPizza_1170x618.jpg", true, picantes));
            comidaRepo.save(new Comida(17L, "Red Flag Fire", "Salsa arrabbiata, salami picante y guindillas.", 71900, "https://i.ytimg.com/vi/st1N9wmyTbk/maxresdefault.jpg", true, picantes));
            comidaRepo.save(new Comida(18L, "México Monaco Heat", "Chorizo picante, jalapeños frescos y salsa roja intensa.", 68900, "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true, picantes));
            comidaRepo.save(new Comida(19L, "Inferno Imola", "Salsa picante extrema, carne sazonada y chile habanero.", 72900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, picantes));
            comidaRepo.save(new Comida(20L, "Scuderia Spice", "Pepperoni picante, pimientos asados y salsa especial.", 69900, "https://images.unsplash.com/photo-1513104890138-7c749659a591", true, picantes));
            comidaRepo.save(new Comida(33L, "Suzuka Spice S-Curves", "Salsa de curry rojo tailandés, pollo marinado y chiles ojo de pájaro.", 72900, "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true, picantes));
            comidaRepo.save(new Comida(34L, "Interlagos Rain Master", "Carne desmechada picante, cebolla morada encurtida y jalapeños rojos.", 73900, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true, picantes));
            
            // ==========================================
            // 5. 🥤 BEBIDAS (21–25)
            // ==========================================
            comidaRepo.save(new Comida(21L, "Podium Cola", "Refresco clásico.", 8900, "https://www.shutterstock.com/image-photo/full-size-cocacola-plastic-bottle-600nw-2642734713.jpg", true, bebidas));
            comidaRepo.save(new Comida(22L, "Energy Drink Apex", "Bebida energética premium.", 12900, "https://images.squarespace-cdn.com/content/v1/60cb1bed3b0cf9305037362d/72803b44-751c-4aa7-9f5b-bd4fb5dddb8e/Hero+AdobeStock_505305338_Editorial_Use_Only.png", true, bebidas));
            comidaRepo.save(new Comida(23L, "Limonada Lap", "Limonada natural fresca.", 10900, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTIZKj7hitzLIYc6TZ6KwFCSRkgXv77cOkkDA&s", true, bebidas));
            comidaRepo.save(new Comida(24L, "Agua Grid", "Agua mineral.", 7900, "https://thumbs.dreamstime.com/b/crystalclear-sparkling-mineral-water-bottle-refreshing-siberian-scene-vibrant-blue-background-experience-invigorating-356031142.jpg", true, bebidas));
            comidaRepo.save(new Comida(25L, "Iced Tea Telemetry", "Té frío artesanal.", 10900, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_V47jVOZzy80l7K1MaampaYRnEMLn9YuceQ&s", true, bebidas));
            comidaRepo.save(new Comida(36L, "Smooth Operator Shake", "Malteada cremosa de vainilla con trozos de galleta artesanal.", 15900, "https://images.unsplash.com/photo-1572490122747-3968b75cc699", true, bebidas));
            comidaRepo.save(new Comida(37L, "Pit Wall Coffee", "Espresso doble con un toque de caramelo salado para mantener la alerta.", 9900, "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04", true, bebidas));
            // ==========================================
            // 6. 🍰 POSTRES (26–30)
            // ==========================================
            comidaRepo.save(new Comida(26L, "Tiramisú Trophy", "Clásico italiano con café y mascarpone.", 18900, "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9", true, postres));
            comidaRepo.save(new Comida(27L, "Brownie Burnout", "Brownie caliente con helado de vainilla.", 20900, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c", true, postres));
            comidaRepo.save(new Comida(28L, "Cheesecake Circuit", "Cheesecake cremoso con frutos rojos.", 18900, "https://images.unsplash.com/photo-1551024601-bec78aea704b", true, postres));
            comidaRepo.save(new Comida(29L, "Lava Cake Launch", "Pastel de chocolate con centro líquido.", 21900, "https://www.verybestbaking.com/sites/g/files/jgfbjl326/files/styles/large/public/recipe-thumbnail/105677-2020_06_23T12_02_56_mrs_ImageRecipes_147148lrg.jpg", true, postres));
            comidaRepo.save(new Comida(30L, "Panna Cotta Pitlane", "Panna cotta con salsa de frutos del bosque.", 17900, "https://images.unsplash.com/photo-1565958011703-44f9829ba187", true, postres));
            comidaRepo.save(new Comida(38L, "Chassis Chocolate Cake", "Bizcocho denso de chocolate 70% cacao con estructura de ganache.", 22900, "https://images.unsplash.com/photo-1578985545062-69928b1d9587", true, postres));
            comidaRepo.save(new Comida(39L, "Aero Apple Tart", "Tarta de manzana fina con láminas dispuestas aerodinámicamente.", 19900, "https://images.unsplash.com/photo-1568571780765-9276ac8b75a2", true, postres));
            comidaRepo.save(new Comida(40L, "Monaco Macarons (Set de 3)", "Macarons de pistacho, frambuesa y limón, elegantes como el paddock.", 24900, "https://images.unsplash.com/photo-1569864358642-9d1619702661", true, postres));
            
            // ==========================================
            // 7. CLIENTES (1-10)
            // ==========================================
            clienteRepo.save(new Cliente(1L, "Pablo", "García", "PabloGarcia21@gmail.com", "pablo123", "123456", "Cra 7 #40-62, Bogotá", "3001234567"));
            clienteRepo.save(new Cliente(2L, "María", "Gómez", "maria.gomez@email.com", "maria123", "maria2024", "Cl 45 #12-30, Medellín", "3019876543"));
            clienteRepo.save(new Cliente(3L, "Andrés", "Martínez", "andres.martinez@email.com", "andres123", "andres789", "Av 68 #23-10, Cali", "3024567890"));
            clienteRepo.save(new Cliente(4L, "Laura", "Ramírez", "laura.ramirez@email.com", "laura123", "lauraPass", "Cra 15 #88-21, Barranquilla", "3106543210"));
            clienteRepo.save(new Cliente(5L, "Camilo", "Torres", "camilo.torres@email.com", "camilito20", "camilo123", "Cl 100 #19-50, Bucaramanga", "3157891234"));
            clienteRepo.save(new Cliente(6L, "Valentina", "López", "valentina.lopez@email.com", "vale456", "valePass1", "Cra 50 #32-15, Pereira", "3201234567"));
            clienteRepo.save(new Cliente(7L, "Santiago", "Hernández", "santiago.hernandez@email.com", "santi789", "santiClave", "Cl 72 #10-45, Cartagena", "3112345678"));
            clienteRepo.save(new Cliente(8L, "Daniela", "Castro", "daniela.castro@email.com", "dani321", "daniSecure", "Av 30 #15-80, Manizales", "3009876543"));
            clienteRepo.save(new Cliente(9L, "Sebastián", "Morales", "sebastian.morales@email.com", "sebas007", "sebasKey", "Cra 25 #60-12, Santa Marta", "3178901234"));
            clienteRepo.save(new Cliente(10L, "Carolina", "Díaz", "carolina.diaz@email.com", "caro2024", "caroPass", "Cl 85 #42-30, Ibagué", "3145678901"));

            System.out.println("🏎️ ¡Semáforo en verde! Base de datos de LaVueltaRapida inicializada correctamente.");
        };
    }
}