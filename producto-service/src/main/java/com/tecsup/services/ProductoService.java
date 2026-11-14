package com.tecsup.services;

import com.tecsup.client.CategoriaClient;
import com.tecsup.dto.CategoriaDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServiceUnavailableException;
import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repo;

    @Autowired
    private CategoriaClient categoriaClient;

    public List<Producto> listar() {
        List<Producto> productos = repo.findAll();
        productos.forEach(this::completarCategoria);
        return productos;
    }

    public Producto guardar(Producto p) {
        // Primero se valida que la categoría exista (Feign -> categoria-service)
        CategoriaDTO categoria = obtenerCategoria(p.getCategoriaId());
        Producto producto = repo.save(p);
        producto.setCategoria(categoria);
        return producto;
    }

    public Producto obtener(Long id) {
        Producto producto = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        completarCategoria(producto);
        return producto;
    }

    public void eliminar(Long id) {
        Producto producto = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        repo.delete(producto);
    }

    /** Consulta obligatoria: si la categoría no existe o el servicio no responde, se lanza error. */
    private CategoriaDTO obtenerCategoria(Long categoriaId) {
        try {
            return categoriaClient.obtenerCategoria(categoriaId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId);
        } catch (FeignException e) {
            throw new ServiceUnavailableException("categoria-service no está disponible");
        }
    }

    /** Consulta opcional: si falla, el producto se devuelve sin la categoría. */
    private void completarCategoria(Producto producto) {
        try {
            producto.setCategoria(categoriaClient.obtenerCategoria(producto.getCategoriaId()));
        } catch (FeignException e) {
            producto.setCategoria(null);
        }
    }
}
