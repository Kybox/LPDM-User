package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.store.Store;
import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.services.admin.AdminService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.lpdm.msuser.utils.admin.ValueType.*;

@RestController
@RequestMapping(STORE_ADMIN_PATH)
public class StoreAdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;

    @Autowired
    public StoreAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = DEFAULT_PATH)
    public ModelAndView adminStore(){

        return new ModelAndView(STORE_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STORE_PAGE_TITLE);
    }

    @GetMapping(value = "/search/{id}")
    public ModelAndView searchStoreById(@PathVariable int id){

        Object result = new ArrayList<Store>();
        ((ArrayList) result).add(adminService.findStoreById(id));

        return new ModelAndView(STORE_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STORE_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @GetMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView searchStore(){

        return new ModelAndView(STORE_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STORE_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView searchOrderResult(
            @Valid @ModelAttribute("searchForm") SearchForm searchForm){

        String keyword = searchForm.getKeyword();
        Object result = null;
        try{
            switch (searchForm.getSearchValue()){
                case SEARCH_STORE_BY_ID:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches()) {
                        result = new ArrayList<Store>();
                        ((ArrayList) result).add(adminService.findStoreById(Integer.valueOf(keyword)));
                    }
                    else result = 500;
                    break;
                case SEARCH_STORE_BY_NAME:
                    result = adminService.findStoreByName(keyword);
                    break;
            }
        }
        catch (FeignException e ){ result = e.status(); }

        return new ModelAndView(STORE_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STORE_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_RESULT_OBJECT, result);
    }

    @PutMapping(value = {"/update", "/update/"})
    public Store updateStore(@Valid @RequestBody Store store){

        log.info("Update store : " + store);

        return adminService.updateStore(store);
    }

    @GetMapping(value = {"/add", "/add/"})
    public ModelAndView addStoreForm() {

        return new ModelAndView(STORE_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STORE_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, "addPage");
    }

    @PostMapping(value = {"/add", "/add/"})
    public Store addNewStore(@Valid @RequestBody Store store) {

        log.info("Add new store : " + store);

        return adminService.addNewStore(store);
    }
}
