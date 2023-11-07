package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class OffsetBasedPageRequestTest {

    @Test
    void constructor_WithValidArguments_ShouldCreatePageRequest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "property");
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, sort);

        assertEquals(0, pageRequest.getPageNumber());
        assertEquals(10, pageRequest.getPageSize());
        assertEquals(sort, pageRequest.getSort());
        assertEquals(0, pageRequest.getOffset());
    }

    @Test
    void constructor_WithInvalidOffset_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new OffsetBasedPageRequest(-1, 10, Sort.unsorted()));
    }

    @Test
    void constructor_WithInvalidLimit_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new OffsetBasedPageRequest(0, 0, Sort.unsorted()));
    }

    @Test
    void nextPage_ShouldReturnCorrectOffset() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 10, Sort.unsorted());
        Pageable nextPage = pageRequest.next();

        assertEquals(20, nextPage.getOffset());
        assertEquals(10, nextPage.getPageSize());
    }

    @Test
    void previousOrFirst_WhenOnFirstPage_ShouldReturnFirst() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.unsorted());
        Pageable previousOrFirst = pageRequest.previousOrFirst();

        assertEquals(0, previousOrFirst.getOffset());
    }

    @Test
    void previousOrFirst_WhenNotOnFirstPage_ShouldReturnPreviousPage() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(20, 10, Sort.unsorted());
        Pageable previousOrFirst = pageRequest.previousOrFirst();

        assertEquals(10, previousOrFirst.getOffset());
    }

    @Test
    void first_ShouldAlwaysReturnFirstPage() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(20, 10, Sort.unsorted());
        Pageable first = pageRequest.first();

        assertEquals(0, first.getOffset());
    }

    @Test
    void withPage_ShouldSetCorrectOffset() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.unsorted());
        Pageable withPage = pageRequest.withPage(2);

        assertEquals(20, withPage.getOffset());
    }

    @Test
    void hasPrevious_WhenNoPrevious_ShouldReturnFalse() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.unsorted());

        assertFalse(pageRequest.hasPrevious());
    }

    @Test
    void hasPrevious_WhenPreviousExists_ShouldReturnTrue() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(20, 10, Sort.unsorted());

        assertTrue(pageRequest.hasPrevious());
    }

    @Test
    void hashCodeAndEquals_ShouldBeConsistent() {
        OffsetBasedPageRequest pageRequest1 = new OffsetBasedPageRequest(10, 10, Sort.unsorted());
        OffsetBasedPageRequest pageRequest2 = new OffsetBasedPageRequest(10, 10, Sort.unsorted());

        assertEquals(pageRequest1.hashCode(), pageRequest2.hashCode());
        assertEquals(pageRequest1, pageRequest2);
    }

    @Test
    void toString_ShouldReturnCorrectFormat() {
        Sort sort = Sort.by(Sort.Direction.ASC, "property");
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 10, sort);

        String toString = pageRequest.toString();
        assertTrue(toString.contains("limit=10"));
        assertTrue(toString.contains("offset=10"));
        assertTrue(toString.contains("sort=" + sort));
    }
}