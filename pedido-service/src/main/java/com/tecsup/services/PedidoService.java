package com.tecsup.services;

import com.tecsup.client.ClienteClient;
import com.tecsup.client.ProductoClient;
import com.tecsup.dto.ClienteDTO;
import com.tecsup.dto.PedidoDTO;
import com.tecsup.dto.PedidoResponseDTO;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServiceUnavailableException;
import com.tecsup.model.EstadoPedido;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private ProductoClient productoClient;

    // ----------------------------------------------------------- consultas

    public List<PedidoResponseDTO> listar() {
        return repository.findAll().stream()
                .map(p -> construir(p, buscarCliente(p.getClienteId()), buscarProducto(p.getProductoId())))
                .toList();
    }

    /**
     * 1. Busca el pedido en su propia base de datos.
     * 2. Obtiene el cliente mediante Feign.
     * 3. Obtiene el producto mediante Feign.
     * 4. Construye la respuesta completa.
     */
    public PedidoResponseDTO obtener(Long id) {
        Pedido pedido = buscarPedido(id);
        return construir(pedido, buscarCliente(pedido.getClienteId()), buscarProducto(pedido.getProductoId()));
    }

    // ----------------------------------------------------------- registrar

    public PedidoResponseDTO registrar(PedidoDTO dto) {
        ClienteDTO cliente = obtenerClienteObligatorio(dto.getClienteId());
        if (cliente.getEstado() != null && !"ACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new BusinessException("El cliente " + cliente.getNombre() + " no está activo");
        }

        ProductoDTO producto = obtenerProductoObligatorio(dto.getProductoId());
        if (producto.getStock() != null && dto.getCantidad() > producto.getStock()) {
            throw new BusinessException("Stock insuficiente: solo hay " + producto.getStock()
                    + " unidades de " + producto.getNombre());
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());
        pedido.setProductoId(dto.getProductoId());
        pedido.setCantidad(dto.getCantidad());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        return construir(repository.save(pedido), cliente, producto);
    }

    // ----------------------------------------------------------- estados (no se elimina)

    public PedidoResponseDTO cambiarEstado(Long id, EstadoPedido nuevo) {
        Pedido pedido = buscarPedido(id);
        if (!pedido.getEstado().puedeCambiarA(nuevo)) {
            throw new BusinessException("No se puede cambiar un pedido de " + pedido.getEstado() + " a " + nuevo);
        }
        pedido.setEstado(nuevo);
        repository.save(pedido);
        return construir(pedido, buscarCliente(pedido.getClienteId()), buscarProducto(pedido.getProductoId()));
    }

    public PedidoResponseDTO anular(Long id) {
        return cambiarEstado(id, EstadoPedido.ANULADO);
    }

    // ----------------------------------------------------------- auxiliares

    private Pedido buscarPedido(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }

    private PedidoResponseDTO construir(Pedido p, ClienteDTO cliente, ProductoDTO producto) {
        Double total = null;
        if (producto != null && producto.getPrecio() != null) {
            total = Math.round(producto.getPrecio() * p.getCantidad() * 100.0) / 100.0;
        }
        return new PedidoResponseDTO(p.getId(), p.getFecha(), p.getCantidad(), p.getEstado(),
                total, cliente, producto);
    }

    // Para registrar: el cliente y el producto DEBEN existir
    private ClienteDTO obtenerClienteObligatorio(Long id) {
        try {
            return clienteClient.obtenerCliente(id);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        } catch (FeignException e) {
            throw new ServiceUnavailableException("cliente-service no está disponible");
        }
    }

    private ProductoDTO obtenerProductoObligatorio(Long id) {
        try {
            return productoClient.obtenerProducto(id);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        } catch (FeignException e) {
            throw new ServiceUnavailableException("producto-service no está disponible");
        }
    }

    // Para consultar: si un servicio no responde, el pedido se muestra igual (sin ese dato)
    private ClienteDTO buscarCliente(Long id) {
        try {
            return clienteClient.obtenerCliente(id);
        } catch (FeignException e) {
            return null;
        }
    }

    private ProductoDTO buscarProducto(Long id) {
        try {
            return productoClient.obtenerProducto(id);
        } catch (FeignException e) {
            return null;
        }
    }
}
