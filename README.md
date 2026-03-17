# 🏁🏎️ 🍕La Vuelta Rápida 🍕🏎️🏁



> **"Donde la pasión por la Fórmula 1 se encuentra con la pizza perfecta."**  
**"La Vuelta Rápida"** es una pizzería temática única. Nuestro menú está diseñado como una **"Grilla de Partida"**, donde cada pizza, bebida o postre rinde homenaje a los circuitos, pilotos y elementos icónicos de las carreras más rápidas del mundo.
---
<p align="center">
  <img src="demo\src\main\resources\static\Images\HeroLogo.png" alt="Logo La Vuelta Rápida" width="500">
</p>


## 🏎️ Descripción del Proyecto
Esta plataforma permite a los usuarios navegar por un menú dividido en categorías "de circuito" (Clásicas, Especiales, Picantes, Bebidas y Postres), gestionar sus perfiles de usuario y recibir recomendaciones de platos.

## 🛠️ Características del Sistema
Nuestra plataforma está construida con una arquitectura robusta para gestionar la operación del restaurante:

* **🚦 Grilla de Partida (Menú):** Gestión de productos categorizados en *Clásicas, Especiales, Picantes, Bebidas y Postres*.
* **🏎️ Pilotos (Clientes):** Sistema de registro y autenticación de usuarios. 
* **💾 Boxes (Persistencia):** Manejo eficiente de datos mediante **Spring Data JPA**.

## 🖥️ Landing Page
[![Visita nuestra web](https://img.shields.io/badge/Landing%20Page-Ver%20Proyecto-red?style=for-the-badge&logo=googlechrome&logoColor=white)](http://localhost:5000)

<p align="center">
  <img src="demo\src\main\resources\static\Images\landingPage.jpeg" alt="Vista previa de la Landing Page" width="600">
</p>


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
