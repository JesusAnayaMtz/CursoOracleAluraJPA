package com.aluracursos.screenmatch.model;

public enum Categoria {
    COMEDIA("Comedy"),
    ACCION("Action"),
    ROMANCE("Romance"),
    DRAMA("Drama"),
    CRIMEN("Crime");

    //se crea un atributo y un constructor ya que no se reconoce el argumento que que se le coloca a las variables del enum
    private String categoriaOmdb;

    //recibira el atributo del enum (comedy, accion, crime etc..)
    Categoria (String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
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

}
