package sk.stasko.ecomerce.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import sk.stasko.ecomerce.common.dto.PaginationRequest;

@Slf4j
public class DbUtil {
    private DbUtil() {}

    public static Sort getSortForPagination(PaginationRequest paginationRequest) {
        // sorting
        return paginationRequest.sortOrder().equalsIgnoreCase("asc")
                ? Sort.by(paginationRequest.sortBy()).ascending() : Sort.by(paginationRequest.sortBy()).descending();
    }
}
