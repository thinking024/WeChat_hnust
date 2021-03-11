package controller;

import org.springframework.web.bind.annotation.*;
import utils.Encode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping(value = "/clearcookie", produces={"application/json;charset=UTF-8"})
    public String clearCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("auth", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "66";
    }
}
