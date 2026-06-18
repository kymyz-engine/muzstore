package com.axelor.apps.store.controller;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.store.db.Category;
import com.axelor.apps.store.db.StoreProduct;
import com.axelor.apps.store.dto.*;
import com.axelor.apps.store.mappers.Mappers;
import com.axelor.apps.store.service.CartService;
import com.axelor.apps.store.service.OrderService;
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
    private final OrderService orderService;

    @Inject
    public PublicWebService(StoreService storeService, ProductService productService, CartService cartService, OrderService orderService) {
        this.storeService = storeService;
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GET
    @Path("/categories")
    public Response getCategoriesList(@Context HttpServletRequest request) {
        List<Category> categories = storeService.getCategoriesList();
        List<CategoryDTO> result = categories.stream()
                .map(Mappers::toCategoryDto)
                .collect(Collectors.toList());
        return Response.ok(result)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .build();
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

    @POST
    @Path("/cart/items")
    public Response addItem(@QueryParam("productId") Long productId,
                            @QueryParam("quantity") Integer quantity) {
        try {
            User user = AuthUtils.getUser();
            CartDTO cart = cartService.addItem(user, productId, quantity != null ? quantity : 1);
            return Response.ok(cart).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/cart/items/{itemId}")
    public Response updateQuantity(@PathParam("itemId") Long itemId,
                                   @QueryParam("quantity") Integer quantity) {
        try {
            User user = AuthUtils.getUser();
            CartDTO cart = cartService.updateQuantity(user, itemId, quantity);
            return Response.ok(cart).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/cart/items/{itemId}")
    public Response removeItem(@PathParam("itemId") Long itemId) {
        try {
            User user = AuthUtils.getUser();
            CartDTO cart = cartService.removeItem(user, itemId);
            return Response.ok(cart).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/cart")
    public Response clearCart() {
        try {
            User user = AuthUtils.getUser();
            cartService.clearCart(user);
            return Response.ok(Map.of("message", "Корзина очищена")).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }
    
    @POST
    @Path("/orders")
    public Response createOrder(OrderCreateDTO dto) {
        try {
            User user = AuthUtils.getUser();
            StoreOrderDTO order = orderService.createOrder(user, dto);
            return Response.ok(order).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @GET
    @Path("/orders")
    public Response getOrders() {
        try {
            User user = AuthUtils.getUser();
            List<StoreOrderDTO> orders = orderService.getOrders(user);
            return Response.ok(orders).build();
        } catch (Exception e) {
            TraceBackService.trace(e);
            return Response.status(500).entity(Map.of("message", e.getMessage())).build();
        }
    }

}
