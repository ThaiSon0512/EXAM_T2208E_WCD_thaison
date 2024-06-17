package org.aptech.t2208e.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class EmployeeDto {
    private Long id;
    private String FullName;
    private Date Birthday;
    private String Address;
    private String Department;
    private String Position;
}
