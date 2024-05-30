package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=de5e9863";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository serieRepository) {
        this.repository = serieRepository;
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar Series Por Titulo
                    5 - Top 5 Mejores Series
                    6 - Buscar Serie Por Genero/Categoria
                    7 - Buscar Series Por Temporada y Evaluacion
                    8 - Buscar Episodios Por Nombre
                    9 - Top Episodios Por Serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioPorNombre();
                    break;
                case 9:
                    top5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }




    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        //DatosSerie datosSerie = getDatosSerie();
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            System.out.println("Serie encontrada");
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }


        }


    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repository.save(serie);
        //datosSeries.add(datos);
        System.out.println(datosSeries);
    }

    private void mostrarSeriesBuscadas() {
        series = repository.findAll();
//        //esta lista va a se igual a los datos de nuestra serie mapeandolo donde cada tipo de dato serie se va a convertir en
//        series = datosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList()); //convertimos a nueva lista

        //ordenamos nuestra serie por genero
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escriba el titulo de la serie a buscar");
        var nombreSerie = teclado.nextLine();

        serieBuscada = repository.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()){
            System.out.println("La seire buscada es: " + serieBuscada.get());
        }else {
            System.out.println("Serie no encontrada");
        }
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + " Evaluacion " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escriba el genero/categoria de la serie que desea buscar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero); //usamos el metodo del enum para convertir el genero pasado por el usuario a categoria
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);  //le pasamos la categoria al metodo find
        System.out.println("Las series de la categoria " + genero); //imprimimos el genero
        seriesPorCategoria.forEach(System.out::println);  //con recorremos las series del genero buscado y las imprimimos

    }

    private void filtrarSeriesPorTemporadaYEvaluacion() {
        System.out.println("Filtrar series con cuantas temporadas");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Con evaluacion apartir de cual");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries = repository.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion);
        System.out.println(" Series Filtradas");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo() + " Evaluacion " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorNombre() {
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorNombre(nombreEpisodio);
        System.out.println("Episodios encontrados");
        episodiosEncontrados.forEach(System.out::println);
    }

    private void top5EpisodiosPorSerie() {
        buscarSeriesPorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repository.top5Episodios(serie);
            topEpisodios.forEach(System.out::println);
        }
    }

}

