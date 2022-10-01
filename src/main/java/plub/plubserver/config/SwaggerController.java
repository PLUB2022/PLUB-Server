package plub.plubserver.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SwaggerController {
    @GetMapping("/docs")
    public String redirect() {
        return "redirect:/swagger-ui/#";
    }
}