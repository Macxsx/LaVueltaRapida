# 🏁🏎️ 🍕La Vuelta Rápida 🍕🏎️🏁

> **"Donde la pasión por la Fórmula 1 se encuentra con la pizza perfecta."**  
**"La Vuelta Rápida"** es una pizzería temática única. Nuestro menú está diseñado como una **"Grilla de Partida"**, donde cada pizza, bebida o postre rinde homenaje a los circuitos, pilotos y elementos icónicos de las carreras más rápidas del mundo.

---

## 🏎️ Descripción del Proyecto
Esta plataforma permite a los usuarios navegar por un menú dividido en categorías "de circuito" (Clásicas, Especiales, Picantes, Bebidas y Postres), gestionar sus perfiles de usuario y recibir recomendaciones de platos.

## 🛠️ Características del Sistema
Nuestra plataforma está construida con una arquitectura robusta para gestionar la operación del restaurante:

* **🚦 Grilla de Partida (Menú):** Gestión de productos categorizados en *Clásicas, Especiales, Picantes, Bebidas y Postres*.
* **🏎️ Pilotos (Clientes):** Sistema de registro y autenticación de usuarios. 
* **💾 Boxes (Persistencia):** Manejo eficiente de datos mediante **Spring Data JPA**.

## 🚀 Stack Tecnológico
| Tecnología | Uso |
| :--- | :--- |
| **Java 17** | Lenguaje Base |
| **Spring Boot 3** | Framework de Aplicación |
| **Spring Data JPA** | Acceso a Datos y ORM |
| **H2 Database** | Motor SQL en Memoria |
| **Maven** | Gestión de Ciclo de Vida |



## 📂 Arquitectura de Software
El proyecto sigue el estándar de desarrollo en capas para facilitar el mantenimiento y la escalabilidad:
- `entitys`: Modelos de datos y tablas de la base de datos.
- `repository`: Interfaces CRUD para la comunicación con SQL.
- `service`: Capa de lógica de negocio y reglas del restaurante.
- `controller`: Endpoints de la API.

## ⚙️ Cómo Poner el Motor en Marcha
Sigue estos pasos para ejecutar el proyecto localmente:

1. **Clonar el Repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/la-vuelta-rapida.git

2. **Importar en tu IDE:**
   Abre tu IDE favorito (IntelliJ, VS Code o Eclipse) e importa el proyecto como un "Existing Maven Project".

3. **Ejecutar la aplicación:**
   Puedes correr la clase DemoApplication.java directamente o usar la terminal desde la raíz del proyecto:
   ```bash
   mvn spring-boot:run

4. **Importar en tu IDE:**
   Puedes auditar las tablas y los datos en tiempo real ingresando a:
   - ***URL:*** http://localhost:8080/h2-console
   - ***JDBC URL:*** jdbc:h2:mem:testdb
   - ***Usuario:*** sa
   - ***Contraseña:*** (dejar en blanco)



Desarrollado con ❤️ para los fanáticos de la velocidad 🏁 y la buena comida 🍕.
