package com.axelor.apps.store.controller;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.store.db.Category;
import com.axelor.apps.store.db.StoreProduct;
import com.axelor.apps.store.dto.CartDTO;
import com.axelor.apps.store.dto.ProductDTO;
import com.axelor.apps.store.mappers.Mappers;
import com.axelor.apps.store.service.CartService;
import com.axelor.apps.store.service.ProductService;
import com.axelor.apps.store.service.StoreService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.google.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/public")
@Produces(MediaType.APPLICATION_JSON)
public class PublicWebService {

    private final StoreService storeService;
    private final ProductService productService;
    private final CartService cartService;

    @Inject
    public PublicWebService(StoreService storeService, ProductService productService, CartService cartService) {
        this.storeService = storeService;
        this.productService = productService;
        this.cartService = cartService;
    }

    @GET
    @Path("/categories")
    public Response getCategoriesList(@Context HttpServletRequest request) {
        List<Category> categories = storeService.getCategoriesList();
        return Response.ok(categories).build();
    }

    @GET
    @Path("/products")
    public Response getProductsList(
            @Context HttpServletRequest request,
            @QueryParam("category") String category,
            @QueryParam("brand") String brand,
            @QueryParam("priceMin") String priceMin,
            @QueryParam("priceMax") String priceMax,
            @QueryParam("sort") String sortBy
            ) {
        try {
            List<ProductDTO> list = productService.getProductsList(category, brand, priceMin, priceMax, sortBy)
                    .stream()
                    .map(Mappers::toProductDto)
                    .collect(Collectors.toList());

            return Response.ok(list).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }
    @GET
    @Path("/product/{id}")
    public Response getProduct(@PathParam("id") Long id) {
        try {
            StoreProduct product = productService.getProduct(id);
            return Response.ok(Mappers.toProductDto(product)).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(404).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @GET
    @Path("/cart")
    public Response getCart() {
        try {
            User user = AuthUtils.getUser();
            CartDTO cart = cartService.getCart(user);
            return Response.ok(cart).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }

}
