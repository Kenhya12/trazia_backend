package com.trazia.trazia_project.entity;

public enum LabelingRegion {
    EU("European Union", "per 100g/100ml", "kJ/kcal", "salt (g)"),
    US("United States", "per serving", "kcal only", "sodium (mg)"),
    UK("United Kingdom", "per 100g + per portion", "kJ/kcal", "salt (g)"),
    CANADA("Canada", "per serving", "kcal only", "sodium (mg)"),
    LATAM("Latin America", "per 100g", "kcal", "sodium (mg)");

    private final String displayName;
    private final String servingSizeFormat;
    private final String energyFormat;
    private final String saltFormat;

    LabelingRegion(String displayName, String servingSizeFormat,
            String energyFormat, String saltFormat) {
        this.displayName = displayName;
        this.servingSizeFormat = servingSizeFormat;
        this.energyFormat = energyFormat;
        this.saltFormat = saltFormat;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getServingSizeFormat() {
        return servingSizeFormat;
    }

    public String getEnergyFormat() {
        return energyFormat;
    }

    public String getSaltFormat() {
        return saltFormat;
    }
}
