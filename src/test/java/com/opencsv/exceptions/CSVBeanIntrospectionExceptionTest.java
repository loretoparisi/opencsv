package com.opencsv.exceptions;


import com.opencsv.bean.mocks.MockBean;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class CSVBeanIntrospectionExceptionTest {
    private static final String TEST_MESSAGE = "some test message";

    @Test
    public void defaultExceptionHasNoMessage() {
        CsvBeanIntrospectionException exception = new CsvBeanIntrospectionException();
        assertNull(exception.getMessage());
        assertNull(exception.getBean());
        assertNull(exception.getField());
    }

    @Test
    public void exceptionWithOnlyAMessage() {
        CsvBeanIntrospectionException exception = new CsvBeanIntrospectionException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getBean());
        assertNull(exception.getField());
    }

    @Test
    public void exceptionWithNoMessageButHasBeanAndField() {
        MockBean bean = new MockBean();
        Field field = bean.getClass().getDeclaredFields()[0];

        assertNotNull(bean);
        assertNotNull(field);

        CsvBeanIntrospectionException exception = new CsvBeanIntrospectionException(bean, field);
        String message = exception.getMessage();
        System.out.println(message);
        assertTrue(message.contains(bean.getClass().getCanonicalName()));
        assertTrue(message.contains(field.getName()));

        assertEquals(bean, exception.getBean());
        assertEquals(field, exception.getField());
    }

    @Test
    public void exceptionWithMessageBeanAndFieldWillReturnMessage() {
        MockBean bean = new MockBean();
        Field field = bean.getClass().getDeclaredFields()[0];

        assertNotNull(bean);
        assertNotNull(field);

        CsvBeanIntrospectionException exception = new CsvBeanIntrospectionException(bean, field, TEST_MESSAGE);
        assertEquals(bean, exception.getBean());
        assertEquals(field, exception.getField());
        assertEquals(TEST_MESSAGE, exception.getMessage());
    }
}
