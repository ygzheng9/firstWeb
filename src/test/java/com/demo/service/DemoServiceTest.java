package com.demo.service;

import org.junit.jupiter.api.Test;

class DemoServiceTest {
    private final DemoService svc = new DemoService();

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
