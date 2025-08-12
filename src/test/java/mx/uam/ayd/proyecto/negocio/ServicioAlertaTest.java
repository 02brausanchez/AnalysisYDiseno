package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.datos.AlertaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Alerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

public class ServicioAlertaTest {

    private ServicioAlerta servicioAlerta;

    @Mock
    private AlertaRepository alertaRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        servicioAlerta = new ServicioAlerta(alertaRepository);
    }

    @Test
    public void crearAlerta_CuandoUmbralNoTieneAlerta_CreaNuevaAlerta() {
        Umbral umbral = mock(Umbral.class);
        when(umbral.getAlerta()).thenReturn(null);

        // Se debe verificar que se setee la alerta en el umbral
        doAnswer(invocation -> {
            Alerta arg = invocation.getArgument(0);
            when(umbral.getAlerta()).thenReturn(arg);
            return null;
        }).when(umbral).setAlerta(any(Alerta.class));

        String correo = "correo@test.com";
        String mensaje = "Mensaje de prueba";
        LocalDateTime fecha = LocalDateTime.now();

        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alerta alerta = servicioAlerta.crearAlerta(umbral, correo, mensaje, fecha);

        assertNotNull(alerta);
        assertEquals(mensaje, alerta.getMensajePersonalizado());
        assertTrue(alerta.isEnviadoPorCorreo());
        assertEquals(fecha, alerta.getFechaHoraEnvio());
        assertEquals(umbral, alerta.getUmbral());
        verify(umbral).setAlerta(alerta);
        verify(alertaRepository).save(alerta);
    }

    @Test
    public void crearAlerta_CuandoUmbralTieneAlerta_LanzaException() {
        Umbral umbral = mock(Umbral.class);
        when(umbral.getAlerta()).thenReturn(new Alerta());

        assertThrows(IllegalArgumentException.class, () -> {
            servicioAlerta.crearAlerta(umbral, "correo@test.com", "msg", LocalDateTime.now());
        });
    }

    @Test
    public void editarAlerta_CuandoExisteAlerta_ActualizaCampos() {
        Alerta alertaGuardada = new Alerta();
        alertaGuardada.setMensajePersonalizado("Mensaje viejo");
        alertaGuardada.setEnviadoPorCorreo(false);
        alertaGuardada.setFechaHoraEnvio(LocalDateTime.now().minusDays(1));

        long id = 1L;
        when(alertaRepository.findById(id)).thenReturn(Optional.of(alertaGuardada));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String correoNuevo = "nuevo@correo.com";
        String mensajeNuevo = "Mensaje nuevo";
        LocalDateTime fechaNueva = LocalDateTime.now();

        Alerta alertaEditada = servicioAlerta.editarAlerta(id, correoNuevo, mensajeNuevo, fechaNueva);

        assertEquals(mensajeNuevo, alertaEditada.getMensajePersonalizado());
        assertTrue(alertaEditada.isEnviadoPorCorreo());
        assertEquals(fechaNueva, alertaEditada.getFechaHoraEnvio());
    }

    @Test
    public void editarAlerta_CuandoNoExisteAlerta_LanzaException() {
        when(alertaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            servicioAlerta.editarAlerta(999L, "correo@test.com", "msg", LocalDateTime.now());
        });
    }

    @Test
    public void crearAlertaSiNecesaria_CuandoStockMenorQueMinimoYNoHayAlerta_CreaAlerta() {
        Producto producto = mock(Producto.class);
        when(producto.getCantidadStock()).thenReturn(5);
        when(producto.getNombre()).thenReturn("ProductoX");

        Umbral umbral = mock(Umbral.class);
        when(umbral.getValorMinimo()).thenReturn(10);
        when(umbral.getAlerta()).thenReturn(null);

        doAnswer(invocation -> {
            Alerta arg = invocation.getArgument(0);
            when(umbral.getAlerta()).thenReturn(arg);
            return null;
        }).when(umbral).setAlerta(any(Alerta.class));

        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alerta alerta = servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

        assertNotNull(alerta);
        assertTrue(alerta.getMensajePersonalizado().contains("ProductoX"));
    }

    @Test
    public void crearAlertaSiNecesaria_CuandoStockMenorQueMinimoYYaHayAlerta_RetornaAlertaExistente() {
        Producto producto = mock(Producto.class);
        when(producto.getCantidadStock()).thenReturn(5);

        Umbral umbral = mock(Umbral.class);
        when(umbral.getValorMinimo()).thenReturn(10);

        Alerta alertaExistente = new Alerta();
        when(umbral.getAlerta()).thenReturn(alertaExistente);

        Alerta alerta = servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

        assertEquals(alertaExistente, alerta);
    }

    @Test
    public void crearAlertaSiNecesaria_CuandoStockNoEsMenorQueMinimo_NoCreaAlerta() {
        Producto producto = mock(Producto.class);
        when(producto.getCantidadStock()).thenReturn(15);

        Umbral umbral = mock(Umbral.class);
        when(umbral.getValorMinimo()).thenReturn(10);

        Alerta alerta = servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

        assertNull(alerta);
    }
}
