package com.trazia.trazia_project.constants;

/**
 * Valores diarios de referencia (IDR) basados en una dieta de 2000 kcal/día.
 * Estos valores se usan para calcular el % del Valor Diario (%VD) en el etiquetado nutricional.
 */
public class ReferenceDailyIntakes {

    public static final double CALORIES = 2000.0;          // kcal
    public static final double PROTEIN = 50.0;             // g
    public static final double CARBOHYDRATES = 275.0;      // g
    public static final double SUGARS = 50.0;              // g
    public static final double FAT = 70.0;                 // g
    public static final double SATURATED_FAT = 20.0;       // g
    public static final double FIBER = 28.0;               // g
    public static final double SODIUM = 2.3;               // g (2300 mg)
    public static final double SALT = 5.0;                 // g

    private ReferenceDailyIntakes() {
        // Constructor privado para evitar instanciación
    }
}