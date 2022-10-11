package plub.plubserver.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping
    public String hello() {
        return "PLUB REST API Server is running!";
    }
}
