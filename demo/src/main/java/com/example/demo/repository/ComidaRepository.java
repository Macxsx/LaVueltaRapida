package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;

@Repository
public class ComidaRepository {


    private final Map<Integer, Comida> comidas = new HashMap<>();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();

    
    public ComidaRepository(CategoriaRepository categoriaRepository) {
    Categoria clasicas = categoriaRepository.findById(1);
    Categoria especiales = categoriaRepository.findById(2);
    Categoria picantes = categoriaRepository.findById(3);
    Categoria bebidas = categoriaRepository.findById(4);
    Categoria postres = categoriaRepository.findById(5);


        // ==========================
        // 🍕 CLASICAS (1–10)
        // ==========================
        comidas.put(1, new Comida(1, "Margherita Monza",
                "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.",
                48900f, "COP",
                "https://images.unsplash.com/photo-1600891964599-f61ba0e24092",
                true, clasicas));

        comidas.put(2, new Comida(2, "Pepperoni Paddock",
                "Salsa de tomate, mozzarella y pepperoni italiano crujiente.",
                54900f, "COP",
                "https://images.unsplash.com/photo-1594007654729-407eedc4be65",
                true, clasicas));

        comidas.put(3, new Comida(3, "Napolitana Nürburgring",
                "Tomate, mozzarella, anchoas, aceitunas negras y orégano.",
                57900f, "COP",
                "https://cocina-casera.com/wp-content/uploads/2023/06/pizza-napolitana.jpeg",
                true, clasicas));

        comidas.put(4, new Comida(4, "Cuatro Quesos Qualy",
                "Mozzarella, gorgonzola, parmesano y provolone.",
                59900f, "COP",
                "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47",
                true, clasicas));

        comidas.put(5, new Comida(5, "Hawaiana Hungaroring",
                "Jamón ahumado, piña asada y mozzarella.",
                54900f, "COP",
                "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3",
                true, clasicas));

        comidas.put(6, new Comida(6, "Vegetariana Valencia",
                "Pimientos, champiñones, cebolla morada, aceitunas y mozzarella.",
                56900f, "COP",
                "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e",
                true, clasicas));

        comidas.put(7, new Comida(7, "Bianca Barcelona",
                "Base blanca, ricotta, mozzarella y espinaca fresca.",
                57900f, "COP",
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",
                true, clasicas));

        comidas.put(8, new Comida(8, "Prosciutto Pole Position",
                "Prosciutto italiano, rúcula fresca y parmesano.",
                64900f, "COP",
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                true, clasicas));

        comidas.put(9, new Comida(9, "Suprema Silverstone",
                "Pepperoni, jamón, champiñones y pimientos.",
                59900f, "COP",
                "https://recetinas.com/wp-content/uploads/2022/06/pizza-suprema.jpg",
                true, clasicas));

        comidas.put(10, new Comida(10, "Clásica Circuito Central",
                "Salsa de tomate, doble mozzarella y orégano especial.",
                51900f, "COP",
                "https://images.unsplash.com/photo-1590947132387-155cc02f3212",
                true, clasicas));

        // ==========================
        // ⭐ ESPECIALES (11–15)
        // ==========================
        comidas.put(11, new Comida(11, "Turbo Trufa GP",
                "Salsa blanca, mozzarella, aceite de trufa y champiñones.",
                74900f, "COP",
                "https://www.calfruitos.com/img/posts/9/0/l/pizza-de-ricotta-tofona-i-poma-1694516318.jpg",
                true, especiales));

        comidas.put(12, new Comida(12, "Carbonara Chicane",
                "Salsa cremosa, panceta crujiente, parmesano y huevo central.",
                71900f, "COP",
                "https://comedera.com/wp-content/uploads/sites/9/2022/04/pizza-carbonara.jpg",
                true, especiales));

        comidas.put(13, new Comida(13, "BBQ Bahrain Boost",
                "Salsa BBQ, pollo grillado y cebolla caramelizada.",
                68900f, "COP",
                "https://images.unsplash.com/photo-1541745537411-b8046dc6d66c",
                true, especiales));

        comidas.put(14, new Comida(14, "Pesto Pit Stop",
                "Base pesto, mozzarella, tomates cherry y burrata fresca.",
                70900f, "COP",
                "https://www.bonella.com.ec/-/media/Project/Upfield/Brands/Rama/Rama-EC/Assets/Recipes/sync-img/0174e966-37d0-44ee-9ff2-035d9038799e.jpg?rev=5b53b329aa004f169295c9c9a7824a23",
                true, especiales));

        comidas.put(15, new Comida(15, "Deluxe DRS",
                "Carne premium, mozzarella, cebolla crispy y salsa especial.",
                75900f, "COP",
                "https://images.unsplash.com/photo-1594007654729-407eedc4be65",
                true, especiales));
        // ==========================
        // 🌶 PICANTES (16–20)
        // ==========================
        comidas.put(16, new Comida(16, "Diablo Drag Zone",
                "Salsa picante, pepperoni, jalapeños y chile seco.",
                69900f, "COP",
                "https://sharemastro.com/wp-content/uploads/SOFMAS21003_April_May_SocialApril_2_DiabloPizza_1170x618.jpg",
                true, picantes));

        comidas.put(17, new Comida(17, "Red Flag Fire",
                "Salsa arrabbiata, salami picante y guindillas.",
                71900f, "COP",
                "https://i.ytimg.com/vi/st1N9wmyTbk/maxresdefault.jpg",
                true, picantes));

        comidas.put(18, new Comida(18, "México Monaco Heat",
                "Chorizo picante, jalapeños frescos y salsa roja intensa.",
                68900f, "COP",
                "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3",
                true, picantes));

        comidas.put(19, new Comida(19, "Inferno Imola",
                "Salsa picante extrema, carne sazonada y chile habanero.",
                72900f, "COP",
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",
                true, picantes));

        comidas.put(20, new Comida(20, "Scuderia Spice",
                "Pepperoni picante, pimientos asados y salsa especial.",
                69900f, "COP",
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                true, picantes));
        // ==========================
        // 🥤 BEBIDAS (21–25)
        // ==========================
       comidas.put(21, new Comida(21, "Podium Cola",
                "Refresco clásico.",
                8900f, "COP",
                "https://www.shutterstock.com/image-photo/full-size-cocacola-plastic-bottle-600nw-2642734713.jpg",
                true, bebidas));

        comidas.put(22, new Comida(22, "Energy Drink Apex",
                "Bebida energética premium.",
                12900f, "COP",
                "https://images.squarespace-cdn.com/content/v1/60cb1bed3b0cf9305037362d/72803b44-751c-4aa7-9f5b-bd4fb5dddb8e/Hero+AdobeStock_505305338_Editorial_Use_Only.png?format=2500w",
                true, bebidas));

        comidas.put(23, new Comida(23, "Limonada Lap",
                "Limonada natural fresca.",
                10900f, "COP",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTIZKj7hitzLIYc6TZ6KwFCSRkgXv77cOkkDA&s",
                true, bebidas));

        comidas.put(24, new Comida(24, "Agua Grid",
                "Agua mineral.",
                7900f, "COP",
                "https://thumbs.dreamstime.com/b/crystalclear-sparkling-mineral-water-bottle-refreshing-siberian-scene-vibrant-blue-background-experience-invigorating-356031142.jpg",
                true, bebidas));

        comidas.put(25, new Comida(25, "Iced Tea Telemetry",
                "Té frío artesanal.",
                10900f, "COP",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_V47jVOZzy80l7K1MaampaYRnEMLn9YuceQ&s",
                true, bebidas));
        // ==========================
        // 🍰 POSTRES (26–30)
        // ==========================
        comidas.put(26, new Comida(26, "Tiramisú Trophy",
                "Clásico italiano con café y mascarpone.",
                18900f, "COP",
                "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9",
                true, postres));

        comidas.put(27, new Comida(27, "Brownie Burnout",
                "Brownie caliente con helado de vainilla.",
                20900f, "COP",
                "https://images.unsplash.com/photo-1606313564200-e75d5e30476c",
                true, postres));

        comidas.put(28, new Comida(28, "Cheesecake Circuit",
                "Cheesecake cremoso con frutos rojos.",
                18900f, "COP",
                "https://images.unsplash.com/photo-1551024601-bec78aea704b",
                true, postres));

        comidas.put(29, new Comida(29, "Lava Cake Launch",
                "Pastel de chocolate con centro líquido.",
                21900f, "COP",
                "https://www.verybestbaking.com/sites/g/files/jgfbjl326/files/styles/large/public/recipe-thumbnail/105677-2020_06_23T12_02_56_mrs_ImageRecipes_147148lrg.jpg?itok=03TKCmE8",
                true, postres));

        comidas.put(30, new Comida(30, "Panna Cotta Pitlane",
                "Panna cotta con salsa de frutos del bosque.",
                17900f, "COP",
                "https://images.unsplash.com/photo-1565958011703-44f9829ba187",
                true, postres));
    }

    public Collection<Comida> findAll() {
        return comidas.values();
    }

    public Comida findById(int id) {
        return comidas.get(id);
    }

    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(int id){
        return comidas.values().stream()
                .filter(comida -> comida.getId() > id)
                .sorted((c1, c2) -> Integer.compare(c1.getId(), c2.getId()))
                .limit(2)
                .toList();
    }
}