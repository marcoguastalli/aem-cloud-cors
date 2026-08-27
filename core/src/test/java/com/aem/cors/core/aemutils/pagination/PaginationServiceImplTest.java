package com.aem.cors.core.aemutils.pagination;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PaginationServiceImplTest {

    private final PaginationServiceImpl service = new PaginationServiceImpl();

    private static final List<Integer> ITEMS = List.of(1, 2, 3, 4, 5, 6, 7);

    @Test
    void testPaginateFirstPage() {
        assertThat(service.paginate(ITEMS, 3, 1), is(List.of(1, 2, 3)));
    }

    @Test
    void testPaginateMiddlePage() {
        assertThat(service.paginate(ITEMS, 3, 2), is(List.of(4, 5, 6)));
    }

    @Test
    void testPaginateLastPartialPage() {
        assertThat(service.paginate(ITEMS, 3, 3), is(List.of(7)));
    }

    @Test
    void testPaginateInvalidItemsPerPage() {
        assertThat(service.paginate(ITEMS, 0, 1), is(List.of()));
    }

    @Test
    void testPaginateInvalidPageNumber() {
        assertThat(service.paginate(ITEMS, 3, 0), is(List.of()));
    }

    @Test
    void testPaginateStartIndexBeyondSize() {
        assertThat(service.paginate(ITEMS, 3, 10), is(List.of()));
    }
}
