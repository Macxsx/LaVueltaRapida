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


## 💾 Estructura de Datos (Base de Datos)
Para que nada falle cuando el semáforo se pone en verde, diseñamos un sistema donde cada pieza encaja perfectamente. Aquí se puede ver cómo se conecta toda la información de nuestro negocio:

<p align="center">
  <img src="Diagrama de entidad relacion.png" alt="Diagrama de Entidad Relación" width="1000">
</p>

> **Nota:** Este mapa muestra cómo se conecta todo en nuestra pista: desde los **Clientes** y sus **Carritos**, hasta los **Domiciliarios** que hacen la entrega final. Todo está organizado con identificadores `BIGINT` para que la búsqueda de tus pizzas y sus **Adicionales** sea tan rápida como un cambio de neumáticos en los boxes.


## ⚙️ Cómo Poner el Motor en Marcha

### Requisitos previos
- Java 17+
- Maven (o usar el wrapper incluido `./mvnw`)

### Pasos

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/la-vuelta-rapida.git
   cd la-vuelta-rapida
   ```

2. **Configurar las variables de entorno:**
   ```bash
   cp demo/.env.example demo/.env
   ```
   Edita `demo/.env` y completa las credenciales de Mercado Pago y SMTP.

3. **Levantar el backend desde la carpeta `demo/`:**
   ```bash
   cd demo
   ./mvnw spring-boot:run
   ```
   La API queda disponible en **http://localhost:8090**

4. **Consola H2 (base de datos en memoria):**
   | Campo | Valor |
   |---|---|
   | URL | http://localhost:8090/h2 |
   | JDBC URL | `jdbc:h2:mem:lavueltarapida` |
   | Usuario | `sa` |
   | Contraseña | *(dejar en blanco)* |



Desarrollado con ❤️ para los fanáticos de la velocidad 🏁 y la buena comida 🍕.
