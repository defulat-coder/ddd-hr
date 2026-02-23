package com.cy.hr.personnel.infrastructure;

/**
 * 文件说明：SimpleEmployeeNoGenerator
 */
import com.cy.hr.personnel.domain.EmployeeNoGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SimpleEmployeeNoGenerator implements EmployeeNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public String next() {
        String date = LocalDate.now().format(FORMATTER);
        return "EMP" + date + String.format("%04d", sequence.getAndIncrement());
    }
}
