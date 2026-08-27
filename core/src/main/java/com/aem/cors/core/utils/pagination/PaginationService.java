package com.aem.cors.core.utils.pagination;

import java.util.List;

/** Generic Service used to paginate element List */
public interface PaginationService {

    /** @param items to paginate
     * @param itemsPerPage number of items per page
     * @param pageNumber page number
     * @return a List of paginated elements
     * @param <T> a generic object */
    <T> List<T> paginate(List<T> items, int itemsPerPage, int pageNumber);
}
