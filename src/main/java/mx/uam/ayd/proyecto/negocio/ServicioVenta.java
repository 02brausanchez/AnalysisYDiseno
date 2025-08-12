package mx.uam.ayd.proyecto.negocio;

import java.util.List;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.datos.DetalleVentaRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;


import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * Servicio para gestionar operaciones relacionadas con ventas,
 * incluyendo creación, almacenamiento, actualización de stock,
 * generación de reportes y exportación a PDF.
 */
@Service
public class ServicioVenta {

    private static final Logger log = LoggerFactory.getLogger(ServicioVenta.class);

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventaRepository repositorio para operaciones de ventas
     * @param detalleVentaRepository repositorio para detalles de venta
     * @param productoRepository repositorio para productos
     */
    @Autowired
    public ServicioVenta(VentaRepository ventaRepository,
                         DetalleVentaRepository detalleVentaRepository,
                         ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Crea una nueva instancia de venta.
     *
     * @return venta nueva instancia de {@link Venta}
     */
    public Venta crearVenta() {
        log.info("Creando venta");
        return new Venta();
    }

    /**
     * Actualiza el stock de un producto restando la cantidad vendida.
     *
     * @param producto producto a actualizar
     * @param cantidadVendida cantidad vendida que se debe descontar
     * @throws IllegalArgumentException si el producto es nulo o cantidad es inválida
     * @throws IllegalStateException si la cantidad vendida es mayor al stock disponible
     */
    public void actualizarStock(Producto producto, int cantidadVendida){
        if(producto == null){
            throw new IllegalArgumentException("Producto no puede ser nulo");
        }
        if(cantidadVendida <= 0){
            throw new IllegalArgumentException("La cantidad vendida no puede ser menor a 0");
        }
        if(cantidadVendida > producto.getCantidadStock()){
            throw new IllegalStateException("La cantidad vendida no puede ser mayor al stock");
        }
        int nuevaCantidad = producto.getCantidadStock() - cantidadVendida;
        producto.setCantidadStock(nuevaCantidad);
        productoRepository.save(producto);
    }

    /**
     * Guarda una venta estableciendo su monto total y fecha actual.
     *
     * @param venta la venta a guardar
     * @param montoTotal monto total calculado para la venta
     * @throws IllegalArgumentException si la venta es nula o el monto es inválido
     */
    public void guardarVenta(Venta venta, double montoTotal){
        if(venta == null){
            throw new IllegalArgumentException("La venta no puede ser nulo");
        }
        if (montoTotal <= 0) {
            throw new IllegalArgumentException("El monto total no puede ser menor o igual a 0");
        }
        venta.setMontoTotal(montoTotal);
        venta.setFecha(LocalDate.now());
        ventaRepository.save(venta);
    }

    /**
     * Guarda una lista de detalles de venta asegurándose que no existan productos duplicados.
     *
     * @param detallesVenta lista con los detalles de la venta
     * @throws IllegalStateException si la lista está vacía o contiene productos duplicados
     */
    public void agregarDetallesVenta(List<DetalleVenta> detallesVenta) {
        if(detallesVenta.isEmpty()){
            throw new IllegalStateException("La lista no puede estar vacia");
        }
        Set<Producto> productos = new HashSet<>();
        for(DetalleVenta detalleVenta : detallesVenta){
            if(!productos.add(detalleVenta.getProducto())){
                throw new IllegalStateException("La lista de detalles contiene productos duplicados");
            }
        }
        detalleVentaRepository.saveAll(detallesVenta);
    }

    /**
     * Genera un documento PDF con la información de la venta y sus detalles,
     * y permite al usuario seleccionar dónde guardarlo.
     *
     * @param detallesVenta lista de detalles de la venta
     * @param venta venta correspondiente a los detalles
     */
    public void crearDocumento(List<DetalleVenta> detallesVenta, Venta venta){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("venta_" + venta.getIdVenta() + ".pdf");

        // Establecer carpeta Descargas como inicial
        String userHome = System.getProperty("user.home");
        File carpetaDescargas = new File(userHome, "Downloads");
        if (carpetaDescargas.exists()) {
            fileChooser.setInitialDirectory(carpetaDescargas);
        }
        File archivo = fileChooser.showSaveDialog(new Stage());
        if (archivo != null) {
            String ruta = archivo.getAbsolutePath();
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }
            try {
                PdfWriter writer = new PdfWriter(ruta);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Título
                document.add(new Paragraph("Venta_" + venta.getIdVenta()).setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

                // Encabezados
                table.addCell("idProducto");
                table.addCell("Nombre_producto");
                table.addCell("Marca");
                table.addCell("Precio");
                table.addCell("Cantidad");
                table.addCell("Subtotal");

                for (DetalleVenta detalleVenta : detallesVenta) {
                    table.addCell(String.valueOf(detalleVenta.getProducto().getIdProducto()));
                    table.addCell(detalleVenta.getProducto().getNombre());
                    table.addCell(String.valueOf(detalleVenta.getProducto().getMarcaProducto()));
                    table.addCell(String.valueOf(detalleVenta.getProducto().getPrecio()));
                    table.addCell(String.valueOf(detalleVenta.getCantidadVendida()));
                    table.addCell(String.valueOf(detalleVenta.getSubtotal()));
                }
                document.add(table);
                document.add(new Paragraph("Total: " + venta.getMontoTotal()).setFontSize(14));
                document.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Recupera un listado de reportes de ventas agrupados por periodicidad y filtrados por fechas y tipo de producto.
     *
     * @param desde fecha de inicio del rango para el reporte
     * @param hasta fecha final del rango para el reporte
     * @param tipoProducto tipo de producto para filtrar
     * @param periodicidad "Mensual" o cualquier otro valor para diario
     * @return lista de objetos {@link ReporteVentaDTO} con la información del reporte
     */
    public List<ReporteVentaDTO> recuperarVenta(LocalDate desde, LocalDate hasta, TipoProducto tipoProducto, String periodicidad) {
        if(periodicidad.equals("Mensual")){
            return ventaRepository.obtenerReporteVentasMensual(desde, hasta, tipoProducto);
        }
        else{
            return ventaRepository.obtenerReporteVentasDiario(desde, hasta, tipoProducto);
        }
    }

    /**
     * Permite descargar un reporte de ventas en formato PDF
     * y permite al usuario seleccionar dónde guardarlo
     *
     * @param ventas lista con los datos del reporte a descargar
     */
    public void descargarReporte(List<ReporteVentaDTO> ventas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_de_venta.pdf");

        // Establecer carpeta Descargas como inicial
        String userHome = System.getProperty("user.home");
        File carpetaDescargas = new File(userHome, "Downloads");
        if (carpetaDescargas.exists()) {
            fileChooser.setInitialDirectory(carpetaDescargas);
        }
        File archivo = fileChooser.showSaveDialog(new Stage());
        if (archivo != null) {
            String ruta = archivo.getAbsolutePath();
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }
            try {
                PdfWriter writer = new PdfWriter(ruta);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Título
                document.add(new Paragraph("Reporte de venta").setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

                // Encabezados
                table.addCell("Fecha");
                table.addCell("Nombre_producto");
                table.addCell("Tipo");
                table.addCell("Vendidos");
                table.addCell("Total");

                for (ReporteVentaDTO venta : ventas) {
                    table.addCell(String.valueOf(venta.getFecha()));
                    table.addCell(venta.getNombreProducto());
                    table.addCell(String.valueOf(venta.getTipoProducto()));
                    table.addCell(String.valueOf(venta.getCantidadVendida()));
                    table.addCell(String.valueOf(venta.getTotalVenta()));
                }
                document.add(table);
                document.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}