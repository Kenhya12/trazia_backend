# ⚙️ TraziaProject - Backend Core (API RESTful)

## 💡 Descripción del Proyecto

TraziaProject es una API RESTful diseñada para la industria alimentaria. Su función principal es automatizar el **cálculo nutricional**, la gestión de costos y el cumplimiento normativo de etiquetado. El sistema procesa la composición de una receta (Bill of Materials) para generar la información legal requerida en la etiqueta del producto terminado (Reglamento UE N.º 1169/2011).

### Flujo de Valor

1.  **Ingesta de Datos:** Gestión de Insumos (`Product`) y Lotes de Materia Prima (Trazabilidad).
2.  **Cálculo Core:** Suma ponderada de nutrientes y costos basada en la receta.
3.  **Salida Legal:** Generación de un *dataset* (`LabelPrintDTO`) con valores por $100 \text{g}$, por porción y %VD.

---

## 🔬 Aspectos Técnicos Clave para Evaluación

| Aspecto | Implementación | Propósito de Ingeniería |
| :--- | :--- | :--- |
| **Arquitectura** | Spring Boot (Arquitectura de Capas) | Desacoplamiento claro entre la Capa de Control, Servicio (Lógica de Negocio) y Repositorio (Persistencia). |
| **Seguridad** | Spring Security + JWT | Autenticación basada en *tokens* y autorización por roles definidos (`ADMIN`, `TECNICO_FORMULACION`, `ALMACEN`). |
| **Lógica Crítica** | `RecipeService.java` | Implementación del **Algoritmo de Suma Ponderada** para el cálculo nutricional y la lógica de conversión de unidades. |
| **Modelado de Datos** | Spring Data JPA | Relaciones complejas (Ej. `Receta 1:N Ingrediente`, `LoteProductoFinal N:M MateriaPrimaLote`) para soportar la trazabilidad. |
| **Cumplimiento** | `ReferenceDailyIntakes.java` | Traducción de normativas (IDRs) en constantes inmutables y su aplicación para calcular el %VD. |

---

## 🚀 Tecnologías

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3
* **Persistencia:** Spring Data JPA
* **Base de Datos:** H2
* **Construcción:** Apache Maven

### Endpoints Clave (Ejemplos)
| Recurso | Método | Descripción |
| :--- | :--- | :--- |
| `/api/recipes` | `POST` | Crea una receta y dispara el cálculo core. |
| `/api/recipes/{id}/label` | `GET` | Genera y devuelve el `LabelPrintDTO` final de la etiqueta. |
| `/api/materiaprima/lote` | `POST` | Registra un lote de MP para trazabilidad. |

---

## 🧪 Pruebas y Documentación de la API

### 🔗 Swagger UI
Puedes explorar y probar la API desde la interfaz de Swagger:

👉 **Swagger UI:**  
[http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)

---

###  Postman

https://paula-69747.postman.co/workspace/Trazia-Project~6ded8cac-712c-4f78-8409-adcb8485ab1e/collection/45994449-253a0901-e5b8-4dea-9a8c-5570d9499940?action=share&creator=45994449&

---

### ⚙️ Configuración del Servidor Local

La API estará disponible en `http://localhost:9090`.



--------------------
--------------------

Markdown

# ⚙️ TraziaProject - Backend Core (RESTful API)

## 💡 Project Description

TraziaProject is a RESTful API designed for the food industry. Its core function is to automate **nutritional calculation**, cost management, and regulatory labeling compliance. The system processes a recipe's composition (Bill of Materials) to generate the legally required information for the finished product label (EU Regulation No 1169/2011).

### Value Flow

1.  **Data Ingestion:** Management of Ingredients (`Product`) and Raw Material Batches (Traceability).
2.  **Core Calculation:** Weighted sum of nutrients and costs based on the recipe.
3.  **Legal Output:** Generation of a *dataset* (`LabelPrintDTO`) with values per $100 \text{g}$, per serving, and %DV.

---

## 🧪 Pruebas y Documentación de la API

### 🔗 Swagger UI
Puedes explorar y probar la API desde la interfaz de Swagger:

👉 **Swagger UI:**  
[http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)

---

### 📬 Colección de Postman
Para realizar pruebas de endpoints, puedes importar la colección de Postman disponible en el proyecto:

👉 **Colección Postman:**  
[https://www.postman.com/collections/TU_ID_O_ENLACE](https://www.postman.com/collections/TU_ID_O_ENLACE)

_(Sustituye el enlace anterior por el link real a tu colección publicada o al JSON que hayas exportado del proyecto Postman.)_

---

### ⚙️ Configuración del Servidor Local
La aplicación backend corre por defecto en:

---

## 🔬 Key Technical Aspects for Evaluation

| Aspect | Implementation | Engineering Purpose |
| :--- | :--- | :--- |
| **Architecture** | Spring Boot (Layered Architecture) | Clear decoupling between the Control, Service (Business Logic), and Repository (Persistence) layers. |
| **Security** | Spring Security + JWT | Token-based authentication and authorization using defined roles (`ADMIN`, `TECNICO_FORMULACION`, `ALMACEN`). |
| **Critical Logic** | `RecipeService.java` | Implementation of the **Weighted Sum Algorithm** for nutritional calculation and unit conversion logic. |
| **Data Modeling** | Spring Data JPA | Complex relationships (E.g., `Recipe 1:N Ingredient`, `FinishedProductBatch N:M RawMaterialBatch`) to support traceability. |
| **Compliance** | `ReferenceDailyIntakes.java` | Translation of regulations (RDIs) into immutable constants and their application for calculating %DV. |



-----------------------
-----------------------

# Diagrama de Clases

```mermaid

classDiagram
    direction TB

    %% ===== ENTIDADES =====
    class Usuario {
        +Long id
        +String nombre
        +String email
        +String password
        +Rol rol
    }

    class Producto {
        +Long id
        +String nombre
        +String marca
        +String codigoBarras
        +BigDecimal precio
        +ProductNutriments nutrimentos
    }

    class ProductNutriments {
        +BigDecimal calorías
        +BigDecimal proteínas
        +BigDecimal carbohidratos
        +BigDecimal azúcares
        +BigDecimal grasas
        +BigDecimal grasasSaturadas
        +BigDecimal fibra
        +BigDecimal sodio
        +BigDecimal sal
    }

    class Ingrediente {
        +Long id
        +String nombre
        +BigDecimal cantidad
        +UnidadMedida unidad
    }

    class Receta {
        +Long id
        +String nombre
        +String descripción
        +String tipoProcesamiento
        +BigDecimal rendimientoFinal
        +List~Ingrediente~ ingredientes
    }

    %% ===== MÓDULO RETENCIÓN NUTRICIONAL =====
    class RetentionFactor {
        +String nutriente
        +String métodoCocción
        +Double factorRetención
    }

    class RetentionService {
        -Map~String, RetentionFactor~ factores
        +RetentionService()
        +cargarFactoresDesdeJson(String ruta)
        +aplicarRetención(Receta receta)
    }

    class ConversionMapper {
        +BigDecimal convertir(BigDecimal valorOriginal, Double factorRetención)
    }

    %% ===== DTOs =====
    class NutrimentsRequest {
        +BigDecimal calorías
        +BigDecimal proteínas
        +BigDecimal carbohidratos
        +BigDecimal azúcares
        +BigDecimal grasas
        +BigDecimal grasasSaturadas
        +BigDecimal fibra
        +BigDecimal sodio
        +BigDecimal sal
    }

    class ProductoRequest {
        +String nombre
        +String marca
        +BigDecimal precio
        +NutrimentsRequest nutrimentos
    }

    class ProductoResponse {
        +Long id
        +String nombre
        +String marca
        +BigDecimal precio
        +NutrimentsRequest nutrimentos
    }

    %% ===== SERVICIOS =====
    class ProductoService {
        +ProductoResponse crearProducto(ProductoRequest request, Long usuarioId)
        +ProductoResponse actualizarProducto(Long productoId, ProductoRequest request, Long usuarioId)
        +void eliminarProducto(Long productoId)
        +List~ProductoResponse~ listarProductos()
    }

    class RecetaService {
        +Receta crearReceta(Receta receta)
        +Receta calcularNutrición(Receta receta)
    }

    %% ===== MAPPERS =====
    class ProductoMapper {
        +ProductoResponse toResponse(Producto producto)
        +Producto toEntity(ProductoRequest request)
    }

    %% ===== CONTROLADORES =====
    class ProductoController {
        +crearProducto()
        +actualizarProducto()
        +eliminarProducto()
        +listarProductos()
    }

    class RecetaController {
        +crearReceta()
        +obtenerReceta()
        +calcularNutrición()
    }

    %% ===== RELACIONES =====
    Usuario "1" --> "N" Producto
    Producto "1" --> "1" ProductNutriments
    Receta "1" --> "N" Ingrediente
    Receta "1" --> "1" RetentionService : aplica factores
    ProductoService --> ProductoMapper
    ProductoController --> ProductoService
    RecetaController --> RecetaService
    RetentionService --> RetentionFactor
    RecetaService --> RetentionService
    ConversionMapper --> RetentionService

	```
