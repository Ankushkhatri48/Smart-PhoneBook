package com.smart_contact_manager.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("sessionUtilityBean")
public class SessionUtilityBean {

    public void removeMessageFromSession() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpSession session = attributes.getRequest().getSession(false);
                if (session != null) {
                    session.removeAttribute("message");
                    session.removeAttribute("messageType");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}