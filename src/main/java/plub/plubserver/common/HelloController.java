package plub.plubserver.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloController {
    @GetMapping
    public String hello() {
        return "PLUB REST API Server is running!!";
    }

}
