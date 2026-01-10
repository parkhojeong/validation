package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // Item item = clazz;
        // Item item = (Item) clazz;
        // item class and child class
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getQuantity() > 1_000_000){
            errors.rejectValue("price", "range", new Object[]{1000, 1_000_000}, null); // range.item.price
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9_999){
            errors.rejectValue("quantity", "max", new Object[]{9_999}, null); // max.item.quantity
        }

        // complex rule
        if(item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000){
            int resultPrice = item.getPrice() * item.getQuantity();
            errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }
}
