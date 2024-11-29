package sk.stasko.ecomerce.category;

public final class CategoryConstants {

    private CategoryConstants() {
        // restrict instantiation
    }

    public static final String  PAGE_NUMBER = "0";
    public static final String PAGE_LIMIT = "10";
    public static final String SORT_BY = "categoryId";
    public static final String SORT_ORDER = "asc";



    public static final String  MESSAGE_201 = "Category created successfully";
    public static final String  MESSAGE_200 = "Request processed successfully";
    public static final String  MESSAGE_417_DELETE= "Delete operation failed. Please try again or contact Dev team";
    public static final String  MESSAGE_417_UPDATE= "Update operation failed. Please try again or contact Dev team";
}
