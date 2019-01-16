package com.lpdm.msuser.proxies;

import com.lpdm.msuser.msproduct.CategoryBean;
import com.lpdm.msuser.msproduct.ProductBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(name = "zuul-server", url = "https://zuul.lpdm.kybox.fr")
@RibbonClient(name = "ms-order")
public interface MsProductProxy {

    @GetMapping(value = "/ms-product/products")
    List<ProductBean> listProduct();

    @GetMapping(value="/ms-product/products/{id}")
    ProductBean findProduct(@PathVariable("id") int id);

    @PostMapping(value = "/ms-product/products")
    void addProduct(@RequestBody ProductBean product);

    @DeleteMapping(value="/ms-product/products/{id}")
    void deleteStock(@PathVariable("id") int id);

    @PutMapping(value="/ms-product/products")
    void updateStock(@RequestBody ProductBean product);

    @GetMapping(value = "${lpdm.product.name}/categories")
    List<CategoryBean> findAllCotegories();
}
