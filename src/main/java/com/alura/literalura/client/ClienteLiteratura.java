package com.alura.literalura.client;

import com.alura.literalura.entity.AutorEntity;
import com.alura.literalura.entity.LibroEntity;
import com.alura.literalura.mapper.ConvierteDatos;
import com.alura.literalura.model.Libro;
import com.alura.literalura.model.Respuesta;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;

import java.util.List;
import java.util.Scanner;

public class ClienteLiteratura {

    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApí = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    private LibroRepository libroRepositorio;
    private AutorRepository autorRepositorio;

    public ClienteLiteratura (LibroRepository libroRepositorio, AutorRepository autorRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void menu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    Elija una opcion:
                        1- Buscar libro por titulo
                        2- Listar libros registrados
                        3- Listar autores registrados
                        4- Listar autores vivos en un determinado año
                        5- Listar libros por idioma
                        0- Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;
                case 2:
                    buscarLibros();
                    break;
                case 3:
                    buscarAutores();
                    break;
                case 4:
                    buscarAutoresVivos();
                    break;
                case 5:
                    buscarPorIdiomas();
                    break;
                case 0:
                    System.out.println("Gracias por usar la aplicacion");
                    break;
                default:
                    System.out.println("Opcion Invalida");
            }
        }
    }

    private void buscarLibros(){
        List<LibroEntity> libros = libroRepositorio.findAll();
        
        if (!libros.isEmpty()){

            for (LibroEntity libro : libros) {
                System.out.println("\n-------- LIBROS ----------\n");
                System.out.println(" Titulo: " + libro.getTitulo());
                System.out.println(" Autor: " + libro.getAutor().getNombre());
                System.out.println(" Idioma: " + libro.getLenguaje());
                System.out.println(" Descargas: " + libro.getDescargas());
                System.out.println("\n----------------------------\n");
            }
        } else {
            System.out.println("\n --------- NO SE ENCONTRO EL LIBRO ---------\n");
        }
    }

    private void buscarAutores() {
        List<AutorEntity> autores = autorRepositorio.findAll();

        if (!autores.isEmpty()){
            for (AutorEntity autor : autores) {
                System.out.println("\n-------- AUTORES ----------\n");
                System.out.println(" Nombre: " + autor.getNombre());
                System.out.println(" Fecha de Nacimiento: " + autor.getFechaNacimiento());
                System.out.println(" Fecha de Fallecimiento: " + autor.getFechaFallecimiento());
                System.out.println(" Libros: " + autor.getLibros().getTitulo());
                System.out.println("\n----------------------------\n");
            }
        } else {
            System.out.println("\n --------- NO SE ENCONTRO EL AUTOR ---------\n");
        }
    }

    private void buscarAutoresVivos() {
        System.out.println("Escriba el año en el que vivio el autor: ");
        var anio = teclado.nextInt();
        teclado.nextLine();

        List<AutorEntity> autores = autorRepositorio.findForYear(anio);

        if (!autores.isEmpty()) {
            for (AutorEntity autor : autores) {
                System.out.println("\n------ AUTORES VIVOS ------\n");
                System.out.println(" Nombre: " + autor.getNombre());
                System.out.println(" Fecha de Nacimiento: " + autor.getFechaNacimiento());
                System.out.println(" Fecha de Fallecimiento: " + autor.getFechaFallecimiento());
                System.out.println(" Libros: " + autor.getLibros().getTitulo());
                System.out.println("\n----------------------------\n");
            }
        } else {
            System.out.println("\n --------- NO SE ENCONTRO AUTORES ---------\n");
        }
    }

    private void buscarPorIdiomas() {
        var menu = """
                Seleccione un Idioma;
                 1. Español (ES)
                 2. Ingles (EN)
                 
                """;
        System.out.println(menu);
        var idioma = teclado.nextInt();
        teclado.nextLine();

        String seleccion = "";

        if (idioma == 1) {
            seleccion = "es";
        } else if (idioma == 2) {
            seleccion = "en";
        }

        List<LibroEntity> libros = libroRepositorio.findForLanguaje(seleccion);

        if (!libros.isEmpty()) {
            for (LibroEntity libro : libros) {
                System.out.println("\n------ LIBROS POR IDIOMA -------\n");
                System.out.println(" Titulo: " + libro.getTitulo());
                System.out.println(" Autor: " + libro.getAutor().getNombre());
                System.out.println(" Idioma: " + libro.getLenguaje());
                System.out.println(" Descargas: " + libro.getDescargas());
                System.out.println("\n----------------------------\n");
            }
        } else {
            System.out.println("\n -------- NO SE ENCONTRARON LIBROS --------\n");
        }
    }

    private void buscarLibroWeb() {
        Respuesta datos = getDatosSerie();

        if (!datos.results().isEmpty()) {
            LibroEntity libro = new LibroEntity(datos.results().get(0));
            libro = libroRepositorio.save(libro);
        }
    }

    private Respuesta getDatosSerie() {
        System.out.println("Ingrese el nombre del libro que deseas buscar: ");
        var titulo = teclado.nextLine();
        titulo = titulo.replace(" ", "%20");
        System.out.println("Titulo: " + titulo);
        System.out.println(URL_BASE + titulo);
        var json = consumoApí.obtenerDatos(URL_BASE + titulo);
        System.out.println(json);
        Respuesta datos = conversor.obtenerDatos(json, Respuesta.class);
        return datos;
    }
}
