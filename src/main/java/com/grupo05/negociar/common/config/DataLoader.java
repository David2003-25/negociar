package com.grupo05.negociar.common.config;

import com.grupo05.negociar.model.entidades.catalogo.Categoria;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.catalogo.Proveedor;
import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.inventario.Lote;
import com.grupo05.negociar.model.entidades.seguridad.Rol;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.repository.catalogo.CategoriaRepository;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.catalogo.ProveedorRepository;
import com.grupo05.negociar.repository.catalogo.UbicacionRepository;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.inventario.LoteRepository;
import com.grupo05.negociar.repository.seguridad.RolRepository;
import com.grupo05.negociar.service.seguridad.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Carga de datos iniciales contextualizados en el negocio real de Negociar S.A.C.:
 * Importación y comercialización de insumos químicos, materias primas, reactivos de laboratorio
 * y productos médicos, con control estricto de trazabilidad por lote, vencimiento y ubicación.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final ProveedorRepository proveedorRepository;
    private final LoteRepository loteRepository;
    private final ExistenciaRepository existenciaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (rolRepository.count() == 0) {
            // ----------------------------------------------------
            // 1. ROLES Y USUARIOS
            // ----------------------------------------------------
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            rolAdmin.setDescripcion("Administrador General del Sistema y Políticas de Almacén");
            rolAdmin.setEstado(true);
            rolAdmin = rolRepository.save(rolAdmin);

            Rol rolAlmacen = new Rol();
            rolAlmacen.setNombre("ALMACENERO");
            rolAlmacen.setDescripcion("Responsable de Recepción, Picking y Despachos FEFO");
            rolAlmacen.setEstado(true);
            rolAlmacen = rolRepository.save(rolAlmacen);

            Usuario admin = new Usuario();
            admin.setNombres("Admin");
            admin.setApellidos("Sistema");
            admin.setCorreo("admin@negociar.com");
            admin.setPasswordHash("admin123");
            admin.setEstado(true);
            usuarioService.registrar(admin, rolAdmin.getIdRol());

            Usuario operador = new Usuario();
            operador.setNombres("Carlos");
            operador.setApellidos("Mendoza");
            operador.setCorreo("almacen@negociar.com");
            operador.setPasswordHash("almacen123");
            operador.setEstado(true);
            usuarioService.registrar(operador, rolAlmacen.getIdRol());

            // ----------------------------------------------------
            // 2. LÍNEAS DE NEGOCIO (CATEGORÍAS DE NEGOCIAR S.A.C.)
            // ----------------------------------------------------
            Categoria catQuimicos = new Categoria();
            catQuimicos.setNombre("QUIMICOS");
            catQuimicos.setDescripcion("Insumos químicos y solventes industriales de alta pureza");
            catQuimicos.setEstado(true);
            catQuimicos = categoriaRepository.save(catQuimicos);

            Categoria catLaboratorio = new Categoria();
            catLaboratorio.setNombre("LABORATORIO");
            catLaboratorio.setDescripcion("Reactivos analíticos grado p.a., kits de diagnóstico y medios");
            catLaboratorio.setEstado(true);
            catLaboratorio = categoriaRepository.save(catLaboratorio);

            Categoria catEspecialidades = new Categoria();
            catEspecialidades.setNombre("ESPECIALIDADES");
            catEspecialidades.setDescripcion("Materias primas especializadas, resinas, polímeros y aditivos");
            catEspecialidades.setEstado(true);
            catEspecialidades = categoriaRepository.save(catEspecialidades);

            Categoria catMedicos = new Categoria();
            catMedicos.setNombre("MEDICOS");
            catMedicos.setDescripcion("Dispositivos médicos, material quirúrgico e insumos hospitalarios estériles");
            catMedicos.setEstado(true);
            catMedicos = categoriaRepository.save(catMedicos);

            // ----------------------------------------------------
            // 3. UBICACIONES FÍSICAS (CONDICIONES DE ALMACENAMIENTO)
            // ----------------------------------------------------
            Ubicacion uQuimicos = new Ubicacion();
            uQuimicos.setCodigo("ALM-QUI-01");
            uQuimicos.setNombre("Almacén Químicos - Zona Solventes Inflamables");
            uQuimicos.setTipo("ALMACEN");
            uQuimicos.setDescripcion("Sector con ventilación forzada y contención de derrames");
            uQuimicos.setEstado(true);
            uQuimicos = ubicacionRepository.save(uQuimicos);

            Ubicacion uLab = new Ubicacion();
            uLab.setCodigo("EST-LAB-A1");
            uLab.setNombre("Estantería A1 - Reactivos Analíticos P.A.");
            uLab.setTipo("ESTANTERIA");
            uLab.setDescripcion("Estantería metálica con recubrimiento epóxico para ácidos y bases");
            uLab.setEstado(true);
            uLab = ubicacionRepository.save(uLab);

            Ubicacion uCamaraFria = new Ubicacion();
            uCamaraFria.setCodigo("CAM-FRI-01");
            uCamaraFria.setNombre("Cámara Fría - Cadena de Frío (2°C a 8°C)");
            uCamaraFria.setTipo("CAMARA_FRIA");
            uCamaraFria.setDescripcion("Control de temperatura y humedad para reactivos termolábiles");
            uCamaraFria.setEstado(true);
            uCamaraFria = ubicacionRepository.save(uCamaraFria);

            Ubicacion uMedicos = new Ubicacion();
            uMedicos.setCodigo("EST-MED-B2");
            uMedicos.setNombre("Estantería B2 - Material Médico Estéril");
            uMedicos.setTipo("ESTANTERIA");
            uMedicos.setDescripcion("Zona limpia para dispositivos y consumibles quirúrgicos");
            uMedicos.setEstado(true);
            uMedicos = ubicacionRepository.save(uMedicos);

            // ----------------------------------------------------
            // 4. PROVEEDORES DE NEGOCIAR S.A.C.
            // ----------------------------------------------------
            Proveedor provMerck = new Proveedor();
            provMerck.setRuc("20100142831");
            provMerck.setRazonSocial("Merck Peruana S.A.");
            provMerck.setDireccion("Av. Los Frutales 419, Ate, Lima");
            provMerck.setTelefono("01-6184400");
            provMerck.setCorreo("ventas.peru@merckgroup.com");
            provMerck.setEstado(true);
            proveedorRepository.save(provMerck);

            Proveedor provQuimicaSuiza = new Proveedor();
            provQuimicaSuiza.setRuc("20100062722");
            provQuimicaSuiza.setRazonSocial("Química Suiza S.A.C.");
            provQuimicaSuiza.setDireccion("Av. República de Panamá 2577, La Victoria, Lima");
            provQuimicaSuiza.setTelefono("01-2114000");
            provQuimicaSuiza.setCorreo("contacto@quimicasuiza.com.pe");
            provQuimicaSuiza.setEstado(true);
            proveedorRepository.save(provQuimicaSuiza);

            Proveedor provBD = new Proveedor();
            provBD.setRuc("20261179831");
            provBD.setRazonSocial("Becton Dickinson del Perú S.A.");
            provBD.setDireccion("Calle Dean Valdivia 148, San Isidro, Lima");
            provBD.setTelefono("01-5122300");
            provBD.setCorreo("pedidos.peru@bd.com");
            provBD.setEstado(true);
            proveedorRepository.save(provBD);

            // ----------------------------------------------------
            // 5. PRODUCTOS CON IDENTIDAD Y UNIDAD DE MEDIDA
            // ----------------------------------------------------
            Producto pAlcohol = new Producto();
            pAlcohol.setCodigo("QUI-101");
            pAlcohol.setNombre("Alcohol Isopropílico 99.8% Grado USP");
            pAlcohol.setCategoria(catQuimicos);
            pAlcohol.setUnidadPresentacion("CILINDRO 200L");
            pAlcohol.setStockMinimo(10);
            pAlcohol.setEstado(true);
            pAlcohol = productoRepository.save(pAlcohol);

            Producto pAcidoHcl = new Producto();
            pAcidoHcl.setCodigo("LAB-201");
            pAcidoHcl.setNombre("Ácido Clorhídrico 37% p.a. Reactivo ACS");
            pAcidoHcl.setCategoria(catLaboratorio);
            pAcidoHcl.setUnidadPresentacion("FRASCO 1L");
            pAcidoHcl.setStockMinimo(20);
            pAcidoHcl.setEstado(true);
            pAcidoHcl = productoRepository.save(pAcidoHcl);

            Producto pEtanol = new Producto();
            pEtanol.setCodigo("LAB-202");
            pEtanol.setNombre("Etanol Absoluto 99.5% p.a. para Análisis");
            pEtanol.setCategoria(catLaboratorio);
            pEtanol.setUnidadPresentacion("FRASCO 2.5L");
            pEtanol.setStockMinimo(15);
            pEtanol.setEstado(true);
            pEtanol = productoRepository.save(pEtanol);

            Producto pResina = new Producto();
            pResina.setCodigo("ESP-301");
            pResina.setNombre("Resina Epóxica Bisfenol A Líquida");
            pResina.setCategoria(catEspecialidades);
            pResina.setUnidadPresentacion("TAMBOR 25KG");
            pResina.setStockMinimo(8);
            pResina.setEstado(true);
            pResina = productoRepository.save(pResina);

            Producto pJeringas = new Producto();
            pJeringas.setCodigo("MED-401");
            pJeringas.setNombre("Jeringas Descartables 10ml con Aguja Estéril");
            pJeringas.setCategoria(catMedicos);
            pJeringas.setUnidadPresentacion("CAJA X 100");
            pJeringas.setStockMinimo(30);
            pJeringas.setEstado(true);
            pJeringas = productoRepository.save(pJeringas);

            Producto pGuantes = new Producto();
            pGuantes.setCodigo("MED-402");
            pGuantes.setNombre("Guantes Quirúrgicos de Nitrilo Talla M");
            pGuantes.setCategoria(catMedicos);
            pGuantes.setUnidadPresentacion("CAJA X 100");
            pGuantes.setStockMinimo(25);
            pGuantes.setEstado(true);
            pGuantes = productoRepository.save(pGuantes);

            // ----------------------------------------------------
            // 6. LOTES CON VENCIMIENTOS Y FECHAS DE CADUCIDAD
            // ----------------------------------------------------
            LocalDate hoy = LocalDate.now();

            // Lote de Alcohol Isopropílico vigente
            Lote lIsoVigente = new Lote();
            lIsoVigente.setProducto(pAlcohol);
            lIsoVigente.setCodigoLote("L-ISO-2601");
            lIsoVigente.setFechaFabricacion(hoy.minusMonths(3));
            lIsoVigente.setFechaVencimiento(hoy.plusMonths(15));
            lIsoVigente.setObservacion("Certificado de análisis conforme. Pureza 99.85%");
            lIsoVigente.setEstado(true);
            lIsoVigente = loteRepository.save(lIsoVigente);

            // Lote de Alcohol Isopropílico PRÓXIMO A VENCER (Para FEFO y Alertas)
            Lote lIsoPorVencer = new Lote();
            lIsoPorVencer.setProducto(pAlcohol);
            lIsoPorVencer.setCodigoLote("L-ISO-2508");
            lIsoPorVencer.setFechaFabricacion(hoy.minusMonths(10));
            lIsoPorVencer.setFechaVencimiento(hoy.plusDays(40));
            lIsoPorVencer.setObservacion("Lote prioritario de salida por política FEFO");
            lIsoPorVencer.setEstado(true);
            lIsoPorVencer = loteRepository.save(lIsoPorVencer);

            // Lote de Ácido Clorhídrico
            Lote lHcl = new Lote();
            lHcl.setProducto(pAcidoHcl);
            lHcl.setCodigoLote("L-HCL-26A");
            lHcl.setFechaFabricacion(hoy.minusMonths(2));
            lHcl.setFechaVencimiento(hoy.plusMonths(24));
            lHcl.setObservacion("Almacenar bajo campana extractora");
            lHcl.setEstado(true);
            lHcl = loteRepository.save(lHcl);

            // Lote de Etanol en Cámara Fría
            Lote lEtanol = new Lote();
            lEtanol.setProducto(pEtanol);
            lEtanol.setCodigoLote("L-ETA-26B");
            lEtanol.setFechaFabricacion(hoy.minusMonths(1));
            lEtanol.setFechaVencimiento(hoy.plusMonths(18));
            lEtanol.setObservacion("Cadena de frío controlada");
            lEtanol.setEstado(true);
            lEtanol = loteRepository.save(lEtanol);

            // Lote de Resina Epóxica
            Lote lResina = new Lote();
            lResina.setProducto(pResina);
            lResina.setCodigoLote("L-RES-2512");
            lResina.setFechaFabricacion(hoy.minusMonths(4));
            lResina.setFechaVencimiento(hoy.plusMonths(8));
            lResina.setObservacion("Sensible a la humedad");
            lResina.setEstado(true);
            lResina = loteRepository.save(lResina);

            // Lote de Jeringas vigentes
            Lote lJerVigente = new Lote();
            lJerVigente.setProducto(pJeringas);
            lJerVigente.setCodigoLote("L-JER-2601");
            lJerVigente.setFechaFabricacion(hoy.minusMonths(2));
            lJerVigente.setFechaVencimiento(hoy.plusYears(3));
            lJerVigente.setObservacion("Esterilización por óxido de etileno");
            lJerVigente.setEstado(true);
            lJerVigente = loteRepository.save(lJerVigente);

            // Lote de Jeringas PRÓXIMO A VENCER (Para Alertas de Caducidad)
            Lote lJerPorVencer = new Lote();
            lJerPorVencer.setProducto(pJeringas);
            lJerPorVencer.setCodigoLote("L-JER-2505");
            lJerPorVencer.setFechaFabricacion(hoy.minusMonths(11));
            lJerPorVencer.setFechaVencimiento(hoy.plusDays(18));
            lJerPorVencer.setObservacion("Alerta de caducidad crítica (< 30 días)");
            lJerPorVencer.setEstado(true);
            lJerPorVencer = loteRepository.save(lJerPorVencer);

            // Lote de Guantes
            Lote lGuantes = new Lote();
            lGuantes.setProducto(pGuantes);
            lGuantes.setCodigoLote("L-GUA-2602");
            lGuantes.setFechaFabricacion(hoy.minusMonths(3));
            lGuantes.setFechaVencimiento(hoy.plusYears(2));
            lGuantes.setObservacion("Libre de polvo");
            lGuantes.setEstado(true);
            lGuantes = loteRepository.save(lGuantes);

            // ----------------------------------------------------
            // 7. EXISTENCIAS VINCULANDO LOTE + UBICACIÓN + PRODUCTO
            // ----------------------------------------------------
            OffsetDateTime ahora = OffsetDateTime.now();

            crearExistencia(pAlcohol, lIsoPorVencer, uQuimicos, 15L, 0L, ahora);
            crearExistencia(pAlcohol, lIsoVigente, uQuimicos, 35L, 5L, ahora);
            crearExistencia(pAcidoHcl, lHcl, uLab, 45L, 0L, ahora);
            crearExistencia(pEtanol, lEtanol, uCamaraFria, 28L, 2L, ahora);
            crearExistencia(pResina, lResina, uQuimicos, 12L, 0L, ahora);
            crearExistencia(pJeringas, lJerPorVencer, uMedicos, 20L, 0L, ahora);
            crearExistencia(pJeringas, lJerVigente, uMedicos, 110L, 10L, ahora);
            crearExistencia(pGuantes, lGuantes, uMedicos, 75L, 0L, ahora);
        }
    }

    private void crearExistencia(Producto p, Lote l, Ubicacion u, Long fisico, Long reservado, OffsetDateTime fecha) {
        Existencia ex = new Existencia();
        ex.setProducto(p);
        ex.setLote(l);
        ex.setUbicacion(u);
        ex.setStockFisico(fisico);
        ex.setStockReservado(reservado);
        ex.setFechaActualizacion(fecha);
        existenciaRepository.save(ex);
    }
}
