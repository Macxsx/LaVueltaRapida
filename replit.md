# La Vuelta Rápida — F1 Pizzería

Spring Boot + Thymeleaf web app for an F1-themed artisan pizzeria.

## Stack
- **Backend**: Spring Boot 3, Spring MVC, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5.3.3, Swiper.js 11 (CDN)
- **Database**: H2 in-memory (`lavueltarapida`), `create-drop` on restart
- **Port**: 5000

## Key URLs
| URL | Description |
|-----|-------------|
| `/` | Landing page |
| `/producto/menu` | Full menu grid |
| `/producto/{id}` | Product detail page |
| `/registro` | User registration |
| `/login` | Login (admin: `admin`/`admin`) |
| `/producto/menutabla` | Admin product management |
| `/h2-console` | H2 DB console (JDBC: `jdbc:h2:mem:lavueltarapida;DB_CLOSE_DELAY=-1`) |

## Project Structure
```
demo/src/main/
  java/com/example/demo/
    controller/   RestauranteController, LoginController, ProductoController, RegistroController
    model/        Comida, Categoria, Adicional, User
    repository/   ComidaRepository, CategoriaRepository, AdicionalRepository
    service/      ComidaService, CategoriaService
  resources/
    templates/    index.html, product-detail.html, menu.html, fragmentos.html, ...
    static/
      landing.css / landing.js    — Landing page styles & scripts
      product.css / product-detail.js — Product detail styles & scripts
      Images/                     — Product images + restaurant photos (resto-*.png)
```

## Landing Page Features
- Hero with F1 pizza tire visual, animated stat counters, trust badges
- 2×1 Tuesday promo banner (animated)
- Swiper.js carousel showing full menu (40 items), autoplay
- Experience section: feature cards, OpenStreetMap embed, restaurant photo gallery
- Footer with nav + branding

## Architecture Notes
- Categories ordered by `findAllByOrderByIdAsc()`
- Same-category recommendations via `findByCategoryIdAndIdNot(Long, Long)`
- Restaurant uses `findAll()` (not findTop5) for the full carousel
- Product detail: dynamic subtotal JS counter, same-category recommendations (max 5)
