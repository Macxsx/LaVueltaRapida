package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;

@Repository
public class ComidaRepository {


    private final Map<Long, Comida> comidas = new HashMap<>();
    
    public ComidaRepository(CategoriaRepository categoriaRepository) {
    Categoria clasicas = categoriaRepository.findById(1L);
    Categoria especiales = categoriaRepository.findById(2L);
    Categoria picantes = categoriaRepository.findById(3L);
    Categoria bebidas = categoriaRepository.findById(4L);
    Categoria postres = categoriaRepository.findById(5L);


        // ==========================
        // 🍕 CLASICAS (1–10)
        // ==========================
        comidas.put(1L, new Comida(1L, "Margherita Monza",
                "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.",
                48900,
                "https://images.unsplash.com/photo-1600891964599-f61ba0e24092",
                true, clasicas));

        comidas.put(2L, new Comida(2L, "Pepperoni Paddock",
                "Salsa de tomate, mozzarella y pepperoni italiano crujiente.",
                54900,
                "https://images.unsplash.com/photo-1594007654729-407eedc4be65",
                true, clasicas));

        comidas.put(3L, new Comida(3L, "Napolitana Nürburgring",
                "Tomate, mozzarella, anchoas, aceitunas negras y orégano.",
                57900,
                "https://cocina-casera.com/wp-content/uploads/2023/06/pizza-napolitana.jpeg",
                true, clasicas));

        comidas.put(4L, new Comida(4L, "Cuatro Quesos Qualy",
                "Mozzarella, gorgonzola, parmesano y provolone.",
                59900,
                "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47",
                true, clasicas));

        comidas.put(5L, new Comida(5L, "Hawaiana Hungaroring",
                "Jamón ahumado, piña asada y mozzarella.",
                54900,
                "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3",
                true, clasicas));

        comidas.put(6L, new Comida(6L, "Vegetariana Valencia",
                "Pimientos, champiñones, cebolla morada, aceitunas y mozzarella.",
                56900,
                "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e",
                true, clasicas));

        comidas.put(7L, new Comida(7L, "Bianca Barcelona",
                "Base blanca, ricotta, mozzarella y espinaca fresca.",
                57900,
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",
                true, clasicas));

        comidas.put(8L, new Comida(8L, "Prosciutto Pole Position",
                "Prosciutto italiano, rúcula fresca y parmesano.",
                64900,
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                true, clasicas));

        comidas.put(9L, new Comida(9L, "Suprema Silverstone",
                "Pepperoni, jamón, champiñones y pimientos.",
                59900,
                "https://recetinas.com/wp-content/uploads/2022/06/pizza-suprema.jpg",
                true, clasicas));

        comidas.put(10L, new Comida(10L, "Clásica Circuito Central",
                "Salsa de tomate, doble mozzarella y orégano especial.",
                51900,
                "https://images.unsplash.com/photo-1590947132387-155cc02f3212",
                true, clasicas));

        // ==========================
        // ⭐ ESPECIALES (11–15)
        // ==========================
        comidas.put(11L, new Comida(11L, "Turbo Trufa GP",
                "Salsa blanca, mozzarella, aceite de trufa y champiñones.",
                74900,
                "https://www.calfruitos.com/img/posts/9/0/l/pizza-de-ricotta-tofona-i-poma-1694516318.jpg",
                true, especiales));

        comidas.put(12L, new Comida(12L, "Carbonara Chicane",
                "Salsa cremosa, panceta crujiente, parmesano y huevo central.",
                71900,
                "https://comedera.com/wp-content/uploads/sites/9/2022/04/pizza-carbonara.jpg",
                true, especiales));

        comidas.put(13L, new Comida(13L, "BBQ Bahrain Boost",
                "Salsa BBQ, pollo grillado y cebolla caramelizada.",
                68900,
                "https://images.unsplash.com/photo-1541745537411-b8046dc6d66c",
                true, especiales));

        comidas.put(14L, new Comida(14L, "Pesto Pit Stop",
                "Base pesto, mozzarella, tomates cherry y burrata fresca.",
                70900,
                "https://www.bonella.com.ec/-/media/Project/Upfield/Brands/Rama/Rama-EC/Assets/Recipes/sync-img/0174e966-37d0-44ee-9ff2-035d9038799e.jpg?rev=5b53b329aa004f169295c9c9a7824a23",
                true, especiales));

        comidas.put(15L, new Comida(15L, "Deluxe DRS",
                "Carne premium, mozzarella, cebolla crispy y salsa especial.",
                75900,
                "https://images.unsplash.com/photo-1594007654729-407eedc4be65",
                true, especiales));
        // ==========================
        // 🌶 PICANTES (16–20)
        // ==========================
        comidas.put(16L, new Comida(16L, "Diablo Drag Zone",
                "Salsa picante, pepperoni, jalapeños y chile seco.",
                69900,
                "https://sharemastro.com/wp-content/uploads/SOFMAS21003_April_May_SocialApril_2_DiabloPizza_1170x618.jpg",
                true, picantes));

        comidas.put(17L, new Comida(17L , "Red Flag Fire",
                "Salsa arrabbiata, salami picante y guindillas.",
                71900,
                "https://i.ytimg.com/vi/st1N9wmyTbk/maxresdefault.jpg",
                true, picantes));

        comidas.put(18L, new Comida(18L, "México Monaco Heat",
                "Chorizo picante, jalapeños frescos y salsa roja intensa.",
                68900,
                "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3",
                true, picantes));

        comidas.put(19L, new Comida(19L, "Inferno Imola",
                "Salsa picante extrema, carne sazonada y chile habanero.",
                72900,
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",
                true, picantes));

        comidas.put(20L, new Comida(20L, "Scuderia Spice",
                "Pepperoni picante, pimientos asados y salsa especial.",
                69900,
                "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                true, picantes));
        // ==========================
        // 🥤 BEBIDAS (21–25)
        // ==========================
       comidas.put(21L, new Comida(21L, "Podium Cola",
                "Refresco clásico.",
                8900,
                "https://www.shutterstock.com/image-photo/full-size-cocacola-plastic-bottle-600nw-2642734713.jpg",
                true, bebidas));

        comidas.put(22L, new Comida(22L, "Energy Drink Apex",
                "Bebida energética premium.",
                12900,
                "https://images.squarespace-cdn.com/content/v1/60cb1bed3b0cf9305037362d/72803b44-751c-4aa7-9f5b-bd4fb5dddb8e/Hero+AdobeStock_505305338_Editorial_Use_Only.png?format=2500w",
                true, bebidas));

        comidas.put(23L, new Comida(23L, "Limonada Lap",
                "Limonada natural fresca.",
                10900,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTIZKj7hitzLIYc6TZ6KwFCSRkgXv77cOkkDA&s",
                true, bebidas));

        comidas.put(24L, new Comida(24L, "Agua Grid",
                "Agua mineral.",
                7900,
                "https://thumbs.dreamstime.com/b/crystalclear-sparkling-mineral-water-bottle-refreshing-siberian-scene-vibrant-blue-background-experience-invigorating-356031142.jpg",
                true, bebidas));

        comidas.put(25L, new Comida(25L, "Iced Tea Telemetry",
                "Té frío artesanal.",
                10900,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_V47jVOZzy80l7K1MaampaYRnEMLn9YuceQ&s",
                true, bebidas));
        // ==========================
        // 🍰 POSTRES (26–30)
        // ==========================
        comidas.put(26L, new Comida(26L, "Tiramisú Trophy",
                "Clásico italiano con café y mascarpone.",
                18900,
                "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9",
                true, postres));

        comidas.put(27L, new Comida(27L, "Brownie Burnout",
                "Brownie caliente con helado de vainilla.",
                20900,
                "https://images.unsplash.com/photo-1606313564200-e75d5e30476c",
                true, postres));

        comidas.put(28L, new Comida(28L, "Cheesecake Circuit",
                "Cheesecake cremoso con frutos rojos.",
                18900,
                "https://images.unsplash.com/photo-1551024601-bec78aea704b",
                true, postres));

        comidas.put(29L, new Comida(29L, "Lava Cake Launch",
                "Pastel de chocolate con centro líquido.",
                21900,
                "https://www.verybestbaking.com/sites/g/files/jgfbjl326/files/styles/large/public/recipe-thumbnail/105677-2020_06_23T12_02_56_mrs_ImageRecipes_147148lrg.jpg?itok=03TKCmE8",
                true, postres));

        comidas.put(30L, new Comida(30L, "Panna Cotta Pitlane",
                "Panna cotta con salsa de frutos del bosque.",
                17900,
                "https://images.unsplash.com/photo-1565958011703-44f9829ba187",
                true, postres));
    }

    public Collection<Comida> findAll() {
        return comidas.values();
    }

    public Comida findById(Long id) {
        return comidas.get(id);
    }

    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(Long id){
        return comidas.values().stream()
                .filter(comida -> comida.getId() > id)
                .sorted((c1, c2) -> Long.compare(c1.getId(), c2.getId()))
                .limit(2)
                .toList();
    }

    public Collection<Comida> findTop2ByIdLessThanOrderByIdDesc(Long id){
        return comidas.values().stream()
                .filter(comida -> comida.getId() < id)
                .sorted((c1, c2) -> Long.compare(c2.getId(), c1.getId()))
                .limit(2)
                .toList();
    }

    public Long count() {
        return (long) comidas.size();
    }

    public Collection<Comida> Recomendados(Long id){
        if(id >= count()-1){
        return findTop2ByIdLessThanOrderByIdDesc(id);
        }
        else{
            return findTop2ByIdGreaterThanOrderByIdAsc(id);
        }

    }

    public void save(Comida comida) {
        if(comida.getId()== null){
        Long tam = count();
        Long lastid = comidas.get(tam).getId();
        comida.setId(lastid + 1);
        comidas.put(comida.getId(), comida);
        }
        else{
            comidas.put(comida.getId(), comida);
        }
    }

    public void deleteById(Long id) {
        comidas.remove(id);
    }


    public Collection<Comida> findTop5Available(){
        return comidas.values().stream()
                .filter(Comida::isAvailable)
                .limit(5)
                .toList();
    }
    
}
