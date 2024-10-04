package com.module.idw_signage.mainHandler;
/*
    Created At 05/09/2024
    Author @Hubino
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.module.idw_signage.Utils.Https;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class HandlerInterceptorImpl implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptorImpl.class);

    private Https https;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("HandlerInterceptorImpl:: preHandle :: Entered into preHandle method..");

        String auth = request.getHeader("Authorization");
        JsonNode jsonResponse = Https.doPost(auth);
        String statusCode = String.valueOf(jsonResponse.get("statusCode"));

        if (!statusCode.equals("200")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            return false;
        } else {
            JsonNode dataNode = jsonResponse.get("data");
            if (dataNode != null) {
                request.setAttribute("userId", dataNode.get("userId").asText());
                request.setAttribute("role", dataNode.get("role").asText());
            }
            return true;
        }
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("HandlerInterceptorImpl:: postHandle :: Entered into postHandle method..");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        logger.info("HandlerInterceptorImpl:: afterCompletion :: Entered into afterCompletion method..");
    }
}
