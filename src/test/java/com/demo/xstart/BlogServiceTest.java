package com.demo.xstart;

import org.junit.jupiter.api.Test;

class BlogServiceTest {
    private final BlogService svc = new BlogService();

    @Test
    void preconditionTest() {
        svc.precondition();
    }

    @Test
    void orderingTest() {
        svc.ordering();
    }

    @Test
    void immutableTest() {
        svc.immutable();
    }

    @Test
    void fileOpTest() {
        svc.fileOp();
    }

    @Test
    void transformTest() {
        svc.transform();
    }

    @Test
    void showLogTest() {
        svc.showlog();
    }
}
