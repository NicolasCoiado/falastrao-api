package br.com.falastrao.falastrao.security.resolver;

import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.annotation.CurrentUser;
import br.com.falastrao.falastrao.security.context.AuthenticatedUserFacade;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticatedUserFacade authenticatedUserFacade;

    public CurrentUserArgumentResolver(AuthenticatedUserFacade authenticatedUserFacade) {
        this.authenticatedUserFacade = authenticatedUserFacade;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        return authenticatedUserFacade.getCurrentUser();
    }

}
