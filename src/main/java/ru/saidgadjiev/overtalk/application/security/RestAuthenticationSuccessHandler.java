package ru.saidgadjiev.overtalk.application.security;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.saidgadjiev.overtalk.application.model.ExtSpringUser;
import ru.saidgadjiev.overtalk.application.model.ResponseMessage;
import ru.saidgadjiev.overtalk.application.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by said on 25.03.2018.
 */
@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOGGER = Logger.getLogger(RestAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LOGGER.debug("Authentication success");
        ExtSpringUser user = (ExtSpringUser) authentication.getPrincipal();

        ResponseUtils.sendResponseMessage(response, 200, new ResponseMessage<>().setContent(user));
    }
}
