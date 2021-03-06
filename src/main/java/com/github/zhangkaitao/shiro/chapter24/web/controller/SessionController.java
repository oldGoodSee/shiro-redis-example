package com.github.zhangkaitao.shiro.chapter24.web.controller;

import com.github.zhangkaitao.shiro.chapter24.Constants;
import com.github.zhangkaitao.shiro.chapter24.dao.CachingShiroSessionDao;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-3-16
 * <p>Version: 1.0
 */

@RequiresPermissions("session:*")
@Controller
@RequestMapping("/sessions")
public class SessionController {
    @Autowired
    private CachingShiroSessionDao sessionDao;

    @RequestMapping()
    public String list(Model model) {

        Collection<Session> sessions = sessionDao.getActiveSessions();
        model.addAttribute("sessions", sessions);
        model.addAttribute("sessionCount", sessions.size());
        return "sessions/list";
    }

    @RequestMapping("/{sessionId}/forceLogout")
    public String forceLogout(
            @PathVariable("sessionId") String sessionId, RedirectAttributes redirectAttributes) {
        try {
            Session session = sessionDao.readSession(sessionId);
            if (session != null) {
                session.setAttribute(Constants.SESSION_FORCE_LOGOUT_KEY, Boolean.TRUE);
            }
        } catch (Exception e) {/*ignore*/}
        redirectAttributes.addFlashAttribute("msg", "强制退出成功！");
        return "redirect:/sessions";
    }



    @RequestMapping("/active")
    @ResponseBody
    public Collection<Session> getActiveSessions() {
        return sessionDao.getActiveSessions();
    }

    @RequestMapping("/read")
    @ResponseBody
    public Session readSession(Serializable sessionId) {
        return sessionDao.doReadSessionWithoutExpire(sessionId);
    }

}
