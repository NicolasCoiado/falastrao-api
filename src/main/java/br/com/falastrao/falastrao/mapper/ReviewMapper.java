package br.com.falastrao.falastrao.mapper;

import br.com.falastrao.falastrao.dto.request.ReviewRequest;
import br.com.falastrao.falastrao.dto.response.ReviewAuthorResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.model.User;
import org.mapstruct.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true) // gerado pelo @PrePersist
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "topics", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(target = "externalId", source = "externalId")
    @Mapping(target = "author", source = "user", qualifiedByName = "mapAuthor")
    @Mapping(target = "publishedAt", source = "publishedAt", qualifiedByName = "mapDate")
    @Mapping(target = "topics", source = "topics", qualifiedByName = "mapTopics")
    ReviewResponse toResponse(Review review);

    @Named("mapAuthor")
    default ReviewAuthorResponse mapAuthor(User user) {
        if (user == null) return null;
        return new ReviewAuthorResponse(
                user.getExternalId(),
                user.getUsername(),
                mapMemberSince(user.getCreatedAt())
        );
    }

    @Named("mapMemberSince")
    default String mapMemberSince(OffsetDateTime createdAt) {
        if (createdAt == null) return null;
        return String.valueOf(createdAt.getYear());
    }

    @Named("mapDate")
    default String mapDate(OffsetDateTime date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Named("mapTopics")
    default Set<String> mapTopics(Set<Topic> topics) {
        if (topics == null) return null;
        return topics.stream()
                .map(Topic::getSubject)
                .collect(Collectors.toSet());
    }
}