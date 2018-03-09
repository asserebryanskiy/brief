package com.name.brief.web.controller;

import com.name.brief.exception.GameSessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(final Throwable throwable, final Model model) {
        if (throwable != null) {
            throwable.printStackTrace();
            String errorMessage = throwable.getMessage();
            model.addAttribute("errorMessage", errorMessage);
        }
        return "error";
    }

    @ExceptionHandler(GameSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String noSuchGameSession() {
        return "index";
    }
}
