package sk.stasko.ecomerce.common.service;

import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;

public interface CrudService<T, ID> {
    PaginationDto<T> findAll(PaginationRequest paginationRequest);
    T findById(ID id);
    void save(T entity);
    boolean update(ID id, T entity);
    boolean deleteById(ID id);
}
