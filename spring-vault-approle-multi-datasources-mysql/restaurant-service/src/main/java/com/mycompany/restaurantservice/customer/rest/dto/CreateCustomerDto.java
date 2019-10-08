package com.mycompany.restaurantservice.customer.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CreateCustomerDto {

    @ApiModelProperty(example = "Ivan Franchin")
    @NotBlank
    private String name;

    @ApiModelProperty(position = 1, example = "ivan.franchin@test.com")
    @Email
    private String email;

}
