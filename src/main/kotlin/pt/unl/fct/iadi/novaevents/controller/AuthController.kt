package pt.unl.fct.iadi.novaevents.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AuthController {

    @GetMapping("/login")
    fun loginPage(
        @RequestParam(required = false) error: String?,
        model: Model
    ): String {
        model.addAttribute("hasError", error != null)
        return "auth/login"
    }
}
