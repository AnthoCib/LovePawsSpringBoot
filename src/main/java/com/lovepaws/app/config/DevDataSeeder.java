package com.lovepaws.app.config;

import com.lovepaws.app.mascota.domain.Categoria;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.domain.Raza;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.EstadoMascotaRepository;
import com.lovepaws.app.mascota.repository.EspecieRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MascotaRepository mascotaRepository;
    private final EstadoMascotaRepository estadoMascotaRepository;

    @Override
    public void run(String... args) {
        seedEstados();
        seedEspeciesRazasCategorias();
        seedMascotas();
    }

    private void seedEstados() {
        saveEstadoIfMissing("DISPONIBLE", "Mascota lista para adopción");
        saveEstadoIfMissing("NO_DISPONIBLE", "Mascota no disponible temporalmente");
        saveEstadoIfMissing("ADOPTADA", "Mascota adoptada");
    }

    private void saveEstadoIfMissing(String id, String descripcion) {
        if (estadoMascotaRepository.findById(id).isEmpty()) {
            estadoMascotaRepository.save(new EstadoMascota(id, descripcion));
        }
    }

    private void seedEspeciesRazasCategorias() {
        if (especieRepository.count() == 0) {
            Especie perro = new Especie();
            perro.setNombre("Perro");
            perro.setEstado(true);

            Especie gato = new Especie();
            gato.setNombre("Gato");
            gato.setEstado(true);

            Especie conejo = new Especie();
            conejo.setNombre("Conejo");
            conejo.setEstado(true);

            especieRepository.saveAll(List.of(perro, gato, conejo));
        }

        if (razaRepository.count() == 0) {
            Especie perro = especieRepository.findAll().stream()
                    .filter(e -> "Perro".equalsIgnoreCase(e.getNombre()))
                    .findFirst()
                    .orElseThrow();
            Especie gato = especieRepository.findAll().stream()
                    .filter(e -> "Gato".equalsIgnoreCase(e.getNombre()))
                    .findFirst()
                    .orElseThrow();
            Especie conejo = especieRepository.findAll().stream()
                    .filter(e -> "Conejo".equalsIgnoreCase(e.getNombre()))
                    .findFirst()
                    .orElseThrow();

            List<Raza> razas = new ArrayList<>();
            razas.add(buildRaza("Mestizo", perro));
            razas.add(buildRaza("Labrador", perro));
            razas.add(buildRaza("Pastor Alemán", perro));
            razas.add(buildRaza("Criollo", gato));
            razas.add(buildRaza("Siamés", gato));
            razas.add(buildRaza("Persa", gato));
            razas.add(buildRaza("Cabeza de león", conejo));
            razas.add(buildRaza("Mini Rex", conejo));
            razaRepository.saveAll(razas);
        }

        if (categoriaRepository.count() == 0) {
            List<Categoria> categorias = List.of(
                    buildCategoria("Cachorro", "Mascotas pequeñas, ideal para familias activas"),
                    buildCategoria("Joven", "Mascotas con energía moderada y fácil adaptación"),
                    buildCategoria("Adulto", "Mascotas calmadas, recomendadas para adopción responsable"),
                    buildCategoria("Especial", "Mascotas con necesidades especiales o rescate prioritario")
            );
            categoriaRepository.saveAll(categorias);
        }
    }

    private Raza buildRaza(String nombre, Especie especie) {
        Raza raza = new Raza();
        raza.setNombre(nombre);
        raza.setEspecie(especie);
        raza.setEstado(true);
        return raza;
    }

    private Categoria buildCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setEstado(true);
        return categoria;
    }

    private void seedMascotas() {
        if (mascotaRepository.count() >= 10) {
            return;
        }

        EstadoMascota disponible = estadoMascotaRepository.findById("DISPONIBLE").orElseThrow();
        EstadoMascota noDisponible = estadoMascotaRepository.findById("NO_DISPONIBLE").orElseThrow();
        EstadoMascota adoptada = estadoMascotaRepository.findById("ADOPTADA").orElseThrow();

        List<Raza> razas = razaRepository.findAll();
        List<Categoria> categorias = categoriaRepository.findAll();

        if (razas.isEmpty() || categorias.isEmpty()) {
            return;
        }

        List<Mascota> mascotas = List.of(
                buildMascota("Luna", findRaza(razas, "Criollo"), findCategoria(categorias, "Joven"), 2, Mascota.Sexo.H, "Cariñosa y sociable, ideal para departamento.", "/images/mascotas/luna.jpg", adoptada),
                buildMascota("Rocky", findRaza(razas, "Labrador"), findCategoria(categorias, "Adulto"), 4, Mascota.Sexo.M, "Muy noble y obediente, disfruta paseos largos.", "/images/mascotas/rocky.jpg", adoptada),
                buildMascota("Mía", findRaza(razas, "Persa"), findCategoria(categorias, "Adulto"), 3, Mascota.Sexo.H, "Tranquila, perfecta para hogares silenciosos.", "/images/mascotas/mia.jpg", adoptada),
                buildMascota("Bruno", findRaza(razas, "Mestizo"), findCategoria(categorias, "Joven"), 2, Mascota.Sexo.M, "Juguetón y protector, convivió con niños.", "/images/mascotas/bruno.jpg", adoptada),
                buildMascota("Leo", findRaza(razas, "Pastor Alemán"), findCategoria(categorias, "Adulto"), 5, Mascota.Sexo.M, "Entrenado básico, requiere espacio amplio.", "/images/mascotas/leo.jpg", adoptada),
                buildMascota("Daisy", findRaza(razas, "Siamés"), findCategoria(categorias, "Joven"), 1, Mascota.Sexo.H, "Activa y curiosa, lista para acompañarte.", "/images/mascotas/daisy.jpg", adoptada),
                buildMascota("Kira", findRaza(razas, "Mestizo"), findCategoria(categorias, "Cachorro"), 1, Mascota.Sexo.H, "Rescatada recientemente, muy afectuosa.", "/images/mascotas/kira.jpg", disponible),
                buildMascota("Toby", findRaza(razas, "Labrador"), findCategoria(categorias, "Joven"), 2, Mascota.Sexo.M, "Sano y vacunado, busca familia definitiva.", "/images/mascotas/toby.jpg", disponible),
                buildMascota("Nube", findRaza(razas, "Mini Rex"), findCategoria(categorias, "Especial"), 2, Mascota.Sexo.H, "Conejita dócil, requiere ambiente tranquilo.", "/images/mascotas/mascota-default.jpg", noDisponible),
                buildMascota("Copito", findRaza(razas, "Cabeza de león"), findCategoria(categorias, "Especial"), 1, Mascota.Sexo.M, "En observación veterinaria, pronto disponible.", "/images/mascotas/mascota-default.jpg", noDisponible)
        );

        mascotaRepository.saveAll(mascotas);
    }

    private Mascota buildMascota(String nombre,
                                 Raza raza,
                                 Categoria categoria,
                                 Integer edad,
                                 Mascota.Sexo sexo,
                                 String descripcion,
                                 String fotoUrl,
                                 EstadoMascota estado) {
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setRaza(raza);
        mascota.setCategoria(categoria);
        mascota.setEdad(edad);
        mascota.setSexo(sexo);
        mascota.setDescripcion(descripcion);
        mascota.setFotoUrl(fotoUrl);
        mascota.setEstado(estado);
        return mascota;
    }

    private Raza findRaza(List<Raza> razas, String nombre) {
        return razas.stream()
                .filter(r -> nombre.equalsIgnoreCase(r.getNombre()))
                .findFirst()
                .orElse(razas.get(0));
    }

    private Categoria findCategoria(List<Categoria> categorias, String nombre) {
        return categorias.stream()
                .filter(c -> nombre.equalsIgnoreCase(c.getNombre()))
                .findFirst()
                .orElse(categorias.get(0));
    }
}
