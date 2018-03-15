package com.name.brief.web.controller;

import com.name.brief.exception.GameSessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(Throwable throwable) {
        if (throwable != null) throwable.printStackTrace();
        return "error";
    }

    @ExceptionHandler(GameSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String noSuchGameSession() {
        return "index";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error404(Model model) {
        model.addAttribute("statusCode", 404);
        return "error";
    }
}
