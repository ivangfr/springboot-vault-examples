package com.mycompany.bookservice.mapper;

import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public class UUIDMapper {

    public String asString(UUID uuid) {
        return  uuid != null ? uuid.toString() : null;
    }

}
