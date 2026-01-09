package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    // parameter order: BindingResult after @ModelAttribute
    // @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", "item name is required"));
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getQuantity() > 1_000_000){
            bindingResult.addError(new FieldError("item", "price", "price must be 1,000 ~ 1,000,000"));
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9_999){
            bindingResult.addError(new FieldError("item", "quantity", "quantity must be 1 ~ 9,999"));
        }

        // complex rule
        if(item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000){
            bindingResult.addError(new ObjectError("item", "price * quantity must be 10,000 or more"));
        }

        if(bindingResult.hasErrors()){
            log.info("validation error : {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "item name is required"));
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getQuantity() > 1_000_000){
            System.out.println("item.getPrice() = " + item.getPrice());
            System.out.println("bindingResult = " + bindingResult.getFieldErrors());
            bindingResult.addError(new FieldError("item", "price", null,false, null,null,"price must be 1,000 ~ 1,000,000"));
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9_999){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),false,null,null,"quantity must be 1 ~ 9,999"));
        }

        // complex rule
        if(item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000){
            bindingResult.addError(new ObjectError("item", "price * quantity must be 10,000 or more"));
        }

        if(bindingResult.hasErrors()){
            log.info("validation error : {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getQuantity() > 1_000_000){
            bindingResult.addError(new FieldError("item", "price", null,false, new String[]{"range.item.price"}, new Object[]{1000, 1_000_000},null));
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9_999){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),false, new String[]{"max.item.quantity"},new Object[]{9999},null));
        }

        // complex rule
        if(item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000){
            int resultPrice = item.getPrice() * item.getQuantity();
            bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
        }

        if(bindingResult.hasErrors()){
            log.info("validation error : {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

