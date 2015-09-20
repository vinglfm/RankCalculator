package com.ranks;

import com.ranks.configuration.BatchConfiguration;
import com.ranks.configuration.PropertyConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Application {

    @RequestMapping("/calculate")
    public String topStrongman(@RequestParam(value = "param") String param) {
        return param;
    }
}
