package com.trazia.trazia_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelPrintDTO {

    // Información general del producto
    private String productName;
    private String brand;
    private String companyName;
    private String companyAddress;
    private String countryOfOrigin;

    // Información de trazabilidad
    private String lote;  // Número de lote del producto terminado
    private LocalDate fechaProduccion;
    private LocalDate fechaCaducidad;

    // Ingredientes y alérgenos
    private String ingredientes;
    private String alergenosDestacados;

    // Información nutricional por 100g y por porción
    private BigDecimal energiaPor100g;
    private BigDecimal energiaPorPorcion;
    private BigDecimal grasas;
    private BigDecimal grasasSaturadas;
    private BigDecimal hidratosCarbono;
    private BigDecimal azucares;
    private BigDecimal proteinas;
    private BigDecimal sal;
    private BigDecimal fibra;

    // Porcentaje del Valor Diario (%VD)
    private BigDecimal vdEnergia;
    private BigDecimal vdGrasas;
    private BigDecimal vdAzucares;
    private BigDecimal vdProteinas;
    private BigDecimal vdSal;

    // Etiquetas de dieta / estilo de vida
    private boolean aptoVegano;            // Indica si el producto es apto para veganos
    private boolean aptoVegetariano;       // Indica si el producto es apto para vegetarianos
    private boolean aptoKeto;              // Indica si el producto es apto para dieta keto
    private boolean aptoPaleo;             // Indica si el producto es apto para dieta paleo
    private boolean aptoNaturista;         // Indica si el producto es apto para naturistas / whole foods
    private boolean sinGluten;             // Indica si el producto es sin gluten
    private boolean sinLactosa;            // Indica si el producto es libre de lactosa / dairy free
    private boolean organico;              // Indica si el producto es orgánico
    private boolean bajoEnAzucar;          // Indica si el producto es bajo en azúcar
    private boolean sinAzucarAñadida;      // Indica si el producto no tiene azúcar añadida
    private boolean sinAditivos;           // Indica si el producto no contiene aditivos
    private boolean sinConservantes;       // Indica si el producto no contiene conservantes
    private boolean aptoOtros;             // Indica si el producto cumple con otras etiquetas dietéticas

    // Extras
    private String codigoBarras;
    private String qrCode;
    private String nutriScore;
}