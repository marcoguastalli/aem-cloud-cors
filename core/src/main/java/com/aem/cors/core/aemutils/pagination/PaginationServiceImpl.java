package com.aem.cors.core.aemutils.pagination;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.aem.cors.core.utils.pagination.PaginationService;

@Component(service = PaginationService.class)
public class PaginationServiceImpl implements PaginationService {

    @Override
    public <T> List<T> paginate(final List<T> items, final int itemsPerPage, final int pageNumber) {
        if (itemsPerPage <= 0 || pageNumber <= 0) {
            return Collections.emptyList();
        }
        int startIndex = (pageNumber - 1) * itemsPerPage;
        int endIndex = calculateEndIndex(items.size(), itemsPerPage, pageNumber, startIndex);
        if (startIndex > items.size()) {
            // wrong input parameter, return an empty list
            startIndex = 0;
            endIndex = 0;
        }
        return Collections.unmodifiableList(items.subList(startIndex, endIndex));
    }

    @NotNull
    private int calculateEndIndex(final int totalItems, final int itemsPerPage, final int pageNumber, final int startIndex) {
        final int previousPageItems = itemsPerPage * (pageNumber - 1);
        final int remainingItems = totalItems - previousPageItems;
        if (remainingItems < itemsPerPage) {
            return startIndex + remainingItems;
        }
        return startIndex + itemsPerPage;
    }

}
