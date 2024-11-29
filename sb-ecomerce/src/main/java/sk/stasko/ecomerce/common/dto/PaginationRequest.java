package sk.stasko.ecomerce.common.dto;

import lombok.Builder;

@Builder
public record PaginationRequest(
        int page, int limit, String sortBy, String sortOrder
) {}
