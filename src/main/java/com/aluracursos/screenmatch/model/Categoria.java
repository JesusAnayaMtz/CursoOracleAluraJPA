package com.aluracursos.screenmatch.model;

public enum Categoria {
    COMEDIA("Comedy", "Comedia"),
    ACCION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    DRAMA("Drama", "Drama"),
    CRIMEN("Crime", "Crimen");

    //se crea un atributo y un constructor ya que no se reconoce el argumento que que se le coloca a las variables del enum
    private String categoriaOmdb;
    private String categoriaEspanol;

    //recibira el atributo del enum (comedy, accion, crime etc..)
    Categoria (String categoriaOmdb, String categoriaEspanol){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEspanol = categoriaEspanol;
    }

    //metodo para tranformar el string a un tipo de dato de Categoria
    public static Categoria fromString(String text){
        for (Categoria categoria : Categoria.values()){    //recorremos la categorias que tenemos
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)){  //si la categoria coincide con la categoria mapeada en el enum
                return categoria;  //retorna la categoria encontrad
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    //metodo para tranformar el string a un tipo de dato de Categoria
    public static Categoria fromEspanol(String text){
        for (Categoria categoria : Categoria.values()){    //recorremos la categorias que tenemos
            if (categoria.categoriaEspanol.equalsIgnoreCase(text)){  //si la categoria coincide con la categoria mapeada en el enum
                return categoria;  //retorna la categoria encontrad
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
