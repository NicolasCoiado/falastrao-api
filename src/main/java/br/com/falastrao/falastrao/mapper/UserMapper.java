package br.com.falastrao.falastrao.mapper;

import br.com.falastrao.falastrao.dto.request.UserRequest;
import br.com.falastrao.falastrao.dto.response.UserResponse;
import br.com.falastrao.falastrao.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User toEntity(UserRequest request);

    @Mapping(target = "externalId", source = "externalId")
    UserResponse toResponse(User user);
}