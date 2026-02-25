package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.demo.entitys.Comida;

@Repository
public class ComidaRepository {
    
    private Map<Integer, Comida> comidas = new HashMap<>();
    
    public ComidaRepository() {
        comidas.put(1, new Comida(1, "Margherita Monza", "Salsa de tomate, mozzarella fresca, albahaca y aceite de oliva extra virgen.", 48900f, "COP", "https://images.unsplash.com/photo-1600891964599-f61ba0e24092", true));
        comidas.put(2, new Comida(2, "Pepperoni Paddock", "Salsa de tomate, mozzarella y pepperoni italiano crujiente.", 54900f, "COP", "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true));
        comidas.put(3, new Comida(3, "Napolitana Nürburgring", "Tomate, mozzarella, anchoas, aceitunas negras y orégano.", 57900f, "COP", "https://cocina-casera.com/wp-content/uploads/2023/06/pizza-napolitana.jpeg", true));
        comidas.put(4, new Comida(4, "Cuatro Quesos Qualy", "Mozzarella, gorgonzola, parmesano y provolone.", 59900f, "COP", "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47", true));
        comidas.put(5, new Comida(5, "Hawaiana Hungaroring", "Jamón ahumado, piña asada y mozzarella.", 54900f, "COP", "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true));
        comidas.put(6, new Comida(6, "Vegetariana Valencia", "Pimientos, champiñones, cebolla morada, aceitunas y mozzarella.", 56900f, "COP", "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e", true));
        comidas.put(7, new Comida(7, "Bianca Barcelona", "Base blanca, ricotta, mozzarella y espinaca fresca.", 57900f, "COP", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true));
        comidas.put(8, new Comida(8, "Prosciutto Pole Position", "Prosciutto italiano, rúcula fresca y parmesano.", 64900f, "COP", "https://images.unsplash.com/photo-1513104890138-7c749659a591", true));
        comidas.put(9, new Comida(9, "Suprema Silverstone", "Pepperoni, jamón, champiñones y pimientos.", 59900f, "COP", "https://recetinas.com/wp-content/uploads/2022/06/pizza-suprema.jpg", true));
        comidas.put(10, new Comida(10, "Clásica Circuito Central", "Salsa de tomate, doble mozzarella y orégano especial.", 51900f, "COP", "https://images.unsplash.com/photo-1590947132387-155cc02f3212", true));
        comidas.put(11, new Comida(11, "Turbo Trufa GP", "Salsa blanca, mozzarella, aceite de trufa y champiñones.", 74900f, "COP", "https://www.calfruitos.com/img/posts/9/0/l/pizza-de-ricotta-tofona-i-poma-1694516318.jpg", true));
        comidas.put(12, new Comida(12, "Carbonara Chicane", "Salsa cremosa, panceta crujiente, parmesano y huevo central.", 71900f, "COP", "https://comedera.com/wp-content/uploads/sites/9/2022/04/pizza-carbonara.jpg", true));
        comidas.put(13, new Comida(13, "BBQ Bahrain Boost", "Salsa BBQ, pollo grillado y cebolla caramelizada.", 68900f, "COP", "https://images.unsplash.com/photo-1541745537411-b8046dc6d66c", true));
        comidas.put(14, new Comida(14, "Pesto Pit Stop", "Base pesto, mozzarella, tomates cherry y burrata fresca.", 70900f, "COP", "https://www.bonella.com.ec/-/media/Project/Upfield/Brands/Rama/Rama-EC/Assets/Recipes/sync-img/0174e966-37d0-44ee-9ff2-035d9038799e.jpg?rev=5b53b329aa004f169295c9c9a7824a23", true));
        comidas.put(15, new Comida(15, "Deluxe DRS", "Carne premium, mozzarella, cebolla crispy y salsa especial.", 75900f, "COP", "https://images.unsplash.com/photo-1594007654729-407eedc4be65", true));
        comidas.put(16, new Comida(16, "Diablo Drag Zone", "Salsa picante, pepperoni, jalapeños y chile seco.", 69900f, "COP", "https://sharemastro.com/wp-content/uploads/SOFMAS21003_April_May_SocialApril_2_DiabloPizza_1170x618.jpg", true));
        comidas.put(17, new Comida(17, "Red Flag Fire", "Salsa arrabbiata, salami picante y guindillas.", 71900f, "COP", "https://i.ytimg.com/vi/st1N9wmyTbk/maxresdefault.jpg", true));
        comidas.put(18, new Comida(18, "México Monaco Heat", "Chorizo picante, jalapeños frescos y salsa roja intensa.", 68900f, "COP", "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", true));
        comidas.put(19, new Comida(19, "Inferno Imola", "Salsa picante extrema, carne sazonada y chile habanero.", 72900f, "COP", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", true));
        comidas.put(20, new Comida(20, "Scuderia Spice", "Pepperoni picante, pimientos asados y salsa especial.", 69900f, "COP", "https://images.unsplash.com/photo-1513104890138-7c749659a591", true));
        comidas.put(21, new Comida(21, "Podium Cola", "Refresco clásico.", 8900f, "COP", "https://images.unsplash.com/photo-1581006852262-e4307cf6283a", true));
        comidas.put(22, new Comida(22, "Energy Drink Apex", "Bebida energética premium.", 12900f, "COP", "https://images.unsplash.com/photo-1554866585-cd94860890b7", true));
        comidas.put(23, new Comida(23, "Limonada Lap", "Limonada natural fresca.", 10900f, "COP", "https://images.unsplash.com/photo-1523374228107-6e44bd2b524e", true));
        comidas.put(24, new Comida(24, "Agua Grid", "Agua mineral.", 7900f, "COP", "https://images.unsplash.com/photo-1564419320461-6870880221ad", true));
        comidas.put(25, new Comida(25, "Iced Tea Telemetry", "Té frío artesanal.", 10900f, "COP", "https://images.unsplash.com/photo-1497534446932-c925b458314e", true));
        comidas.put(26, new Comida(26, "Tiramisú Trophy", "Clásico italiano con café y mascarpone.", 18900f, "COP", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9", true));
        comidas.put(27, new Comida(27, "Brownie Burnout", "Brownie caliente con helado de vainilla.", 20900f, "COP", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c", true));
        comidas.put(28, new Comida(28, "Cheesecake Circuit", "Cheesecake cremoso con frutos rojos.", 18900f, "COP", "https://images.unsplash.com/photo-1551024601-bec78aea704b", true));
        comidas.put(29, new Comida(29, "Lava Cake Launch", "Pastel de chocolate con centro líquido.", 21900f, "COP", "https://www.verybestbaking.com/sites/g/files/jgfbjl326/files/styles/large/public/recipe-thumbnail/105677-2020_06_23T12_02_56_mrs_ImageRecipes_147148lrg.jpg?itok=03TKCmE8", true));
        comidas.put(30, new Comida(30, "Panna Cotta Pitlane", "Panna cotta con salsa de frutos del bosque.", 17900f, "COP", "https://images.unsplash.com/photo-1565958011703-44f9829ba187", true));
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
