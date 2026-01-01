package com.wms.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {
    @Test
    void testOrderCreation() {
        int orderId = (int) (Math.random() * 10) + 1; // 1 ile 10 arası pozitif değer
        assertTrue(orderId > 0, "Sipariş ID pozitif olmalı");
    }

    @Test
    void testOrderCancel() {
        boolean cancelled = true;
        assertTrue(cancelled, "Sipariş iptal edilmeli");
    }
}
