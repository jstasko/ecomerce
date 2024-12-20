package sk.stasko.ecomerce.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CartOperation {
    ADD(1),
    DELETE(-1);

    // Method to get the quantity change based on operation type
    private final int quantityChange;

    // Static method to check if the operation is valid
    public static boolean isValid(String operation) {
        try {
            CartOperation.valueOf(operation.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Static method to get the operation enum from a string
    public static int getQuantityChangeFromString(String operation) {
        return CartOperation.valueOf(operation.toUpperCase()).getQuantityChange();
    }
}
