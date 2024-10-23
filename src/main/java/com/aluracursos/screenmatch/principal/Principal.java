package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?type=series&t=";
    private final String API_KEY = "&apikey=ea0e2456";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<DatosSerie>();
    private List<Serie> series = new ArrayList<>();
    private SerieRepository repository;
    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \n1 - Buscar series de OMDB por título
                    2 - Buscar episodios
                    3 - Mostrar series buscadas  
                    4 - Buscar series de mi base de datos por título
                    5 - Top 5 series de mi base de datos
                    6 - Buscar series por categoría de mi base de datos
                    7 - Filtrar series                                
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
                    buscarSeriePorTitulo();
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
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar en OMDB:");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE +
                nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        // Verificar si la serie ya existe en la base de datos:
        Optional<Serie> serieExistente = repository.findByTituloContainingIgnoreCase(serie.getTitulo());
        if (serieExistente.isPresent()) {
            // Mostrar los datos de la serie existente e indicar que ya está en la base de datos:
            System.out.println("La serie ya existe en la base de datos:");
            System.out.println(serieExistente.get());
        } else {
            // Guardar la nueva serie y mostrarla:
            repository.save(serie);
            System.out.println("Serie guardada en la base de datos:");
            System.out.println(serie);
        }
    }

    private void buscarEpisodioPorSerie(){
        mostrarSeriesBuscadas();
        System.out.println("Escriba el nombre de la serie que desea ver");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nombreSerie);

        if (serie.isPresent()){
            var serieBuscada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieBuscada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE +
                        serieBuscada.getTitulo().replace(" ", "+") + "&season=" + i
                        + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
        }
    }

    private void mostrarSeriesBuscadas() {
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escriba el nombre de la serie que desea buscar en su base de datos:");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serieBuscada = repository.findByTituloContainingIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()){
            System.out.println("Datos de la serie: " + serieBuscada.get());
        } else {
            System.out.println("Datos no encontrados");
        }
    }

    public void buscarTop5Series(){
        List<Serie> topSeries = repository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: "
                + s.getTitulo() + " Evaluación: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Ingrese un género de series para buscar en su base de datos: ");
        var nombreGenero = teclado.nextLine();
        Categoria categoria = Categoria.fromEspanol(nombreGenero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Series de la categoría " + nombreGenero + ":");
        seriesPorCategoria.forEach(System.out::println);
    }

    public void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("Filtrar series con X cantidad de temporadas (ingrese X):");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("Filtrar series con cierta evaluación mínima (ingrese dicha evaluación:");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries =
                repository.findByTotalDeTemporadasLessThanEqualAndEvaluacionGreaterThanEqual
                        (totalTemporadas, evaluacion);
        System.out.println("*** SERIES FILTRADAS: ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - Evaluación: " + s.getEvaluacion()));
    }

}

