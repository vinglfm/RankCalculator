package com.ranks;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Application {

    @RequestMapping("/calculate")
    public String topStrongman (@RequestParam(value = "param") String param) {
        return param;
    }
}
