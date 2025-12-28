package util;

import backend.*;

public class TestRunner {
    public static void runTests() {
        System.out.println("=== RUNNING UNIT TESTS ===");
        testValidasiNomor();
        testPencarian();
        testDuplikat();
        System.out.println("=== TESTS COMPLETED ===");
    }
    
    private static void testValidasiNomor() {
        try {
            new Kontak("Test", "123");
            System.out.println("❌ TEST FAILED: Should throw exception");
        } catch (Exception e) {
            System.out.println("✅ Validasi nomor: " + e.getMessage());
        }
    }
    
    private static void testPencarian() {
        // Implementasi testing pencarian
    }
    
    private static void testDuplikat() {
        // Implementasi testing duplikat
    }
}