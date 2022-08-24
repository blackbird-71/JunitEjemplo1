package org.oscar.junit5.ejemplos.models;

import org.junit.jupiter.api.*;
import org.oscar.junit5.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest(){
        this.cuenta=new Cuenta("Andres", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el método");
    }

    @AfterEach
    void tearDown(){
        System.out.println("Finaliando el método de prueba.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {
        String esperado = "Andres";
        String real = cuenta.getPersona();

        assertNotNull(real, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba");
        assertTrue(real.equals("Andres"), () -> "Nombre cuenta esperada debe ser igual a la real");
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
    void testSaldoCuenta() {
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testeando referencias que sean iguales con el metodo equals.")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1000.12345"));

        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuenta() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    //@Disabled
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    void testRelacionBancoCuentas() {
        //fail();
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()),
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del estado", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Andres"))
                        .findFirst()
                        .get().getPersona()),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Andres"))
                        .findFirst()
                        .get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Jhon Doe")))
        );

    }
}