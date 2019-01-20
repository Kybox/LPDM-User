package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.Storage;
import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.msauthentication.AppUserBean;
import com.lpdm.msuser.msproduct.ProductBean;
import com.lpdm.msuser.services.admin.AdminService;
import feign.FeignException;
import org.apache.catalina.mbeans.UserMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AdminService adminService;

    @Autowired
    public ProductAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = {"", "/"})
    public ModelAndView adminProducts(){

        LocalDate date = LocalDate.now();
        OrderStats currentYear = adminService.findOrderedProductsStatsByYear(date.getYear());
        OrderStats lastYear = adminService.findOrderedProductsStatsByYear(date.getYear() - 1);
        OrderStats average = adminService.getAverageStats(currentYear, lastYear);

        OrderStats catCurrentYear = adminService.findOrderedProductsStatsByYearAndCategory(date.getYear());
        OrderStats catLastYear = adminService.findOrderedProductsStatsByYearAndCategory(date.getYear() - 1);
        OrderStats catAverage = adminService.getAverageStats(currentYear, lastYear);

        return new ModelAndView("/admin/fragments/products")
                .addObject("pageTitle", "Admin products")
                .addObject("statsCurrentYear", currentYear)
                .addObject("statsLastYear", lastYear)
                .addObject("statsAverageYear", average)
                .addObject("statsCatCurrentYear", catCurrentYear)
                .addObject("statsCatLastYear", catLastYear)
                .addObject("statsCatAverageYear", catAverage)
                .addObject("content", "stats");
    }

    @GetMapping(value = {"/search", "/search/"})
    public ModelAndView searchProduct(){
        return new ModelAndView("/admin/fragments/products")
                .addObject("pageTitle", "Search product")
                .addObject("content", "searchPage")
                .addObject("searchForm", new SearchForm());
    }

    @PostMapping(value = {"/search", "/search/"})
    public ModelAndView searchProductResult(
            @Valid @ModelAttribute("searchForm") SearchForm searchForm){

        log.info("Key[" + searchForm.getSearchValue() + "] : " + searchForm.getKeyword());
        String keyword = searchForm.getKeyword();
        Object result = null;
        try{
            switch (searchForm.getSearchValue()){
                case 1:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches()) {
                        result = new ArrayList<ProductBean>();
                        ((ArrayList) result).add(adminService.findProductById(Integer.valueOf(keyword)));
                    }
                    else result = 500;
                    break;
                case 2:
                    result = adminService.findAllOrdersByUserEmail(keyword);
                    break;
                    /*
                case 3:
                    result = adminService.findAllOrdersByUserEmail(keyword);
                    break;
                case 4:
                    result = adminService.findAllOrdersByUserLastName(keyword);
                    */
            }
        }
        catch (FeignException e ){
            log.warn(e.getMessage());
            result = e.status();
        }

        return new ModelAndView("/admin/fragments/products")
                .addObject("pageTitle", "Search product")
                .addObject("content", "searchPage")
                .addObject("result", result)
                .addObject("categories", adminService.findAllCategories())
                .addObject("searchForm", new SearchForm());
    }

    @PostMapping(value = {"/upload", "/upload/"},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public String getUploadForm(@RequestParam Map<String, String> data){

        log.info("user bean = " + data.get("id"));
        AppUserBean user = new AppUserBean();
        user.setId(Integer.parseInt(data.get("id")));
        return adminService.getUploadPictureForm(user);

    }

    @GetMapping(value = {"/picture/owner/{id}", "/picture/owner/{id}/"})
    public Storage getLatestFileUploaded(@PathVariable int id){
        return adminService.findLatestFileUploadedByOwnerId(id);
    }

    @PostMapping(value = {"/update", "update/"})
    public ProductBean updateProduct(@RequestBody ProductBean productBean){

        log.info("Product update : " + productBean.toString());
        adminService.updateProduct(productBean);
        return productBean;
    }
}