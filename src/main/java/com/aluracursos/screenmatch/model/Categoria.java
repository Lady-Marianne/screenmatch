package com.aluracursos.screenmatch.model;

public enum Categoria {
    AVENTURA("Adventure", "Aventura"),
    ACCION("Action", "Acción"),
    ANIMACION("Animation", "Animación"),
    BIOGRAFIA("Biography", "Biografía"),
    CIENCIA_FICCION("Sci-Fi", "Ciencia ficción"),
    COMEDIA("Comedy", "Comedia"),
    CRIMEN("Crime", "Crimen"),
    DRAMA("Drama", "Drama"),
    FANTASIA("Fantasy", "Fantasía"),
    MISTERIO("Mistery", "Misterio"),
    ROMANCE("Romance", "Romance");

    private String categoriaOmdb;
    private String categoriaEspanol;
    Categoria (String categoriaOmdb, String categoriaEspanol){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEspanol = categoriaEspanol;
    }

//    Nota: Si agrego categorías, debo actualizar la base de datos de la siguiente manera:
//    ALTER TABLE series
//    DROP CONSTRAINT series_genero_check;
//    ALTER TABLE series
//    ADD CONSTRAINT series_genero_check CHECK (genero IN ('AVENTURA',
//                                              'ACCION', 'ANIMACION', 'BIOGRAFIA', 'CIENCIA_FICCION', 'COMEDIA',
//                                                      'CRIMEN', 'DRAMA', 'FANTASIA','MISTERIO', 'ROMANCE'));

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    public static Categoria fromEspanol(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEspanol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
