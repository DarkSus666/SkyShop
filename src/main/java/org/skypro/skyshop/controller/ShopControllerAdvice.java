package org.skypro.skyshop.controller;

import org.skypro.skyshop.model.ShopError;
import org.skypro.skyshop.model.exeptions.NoSuchProductException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ShopControllerAdvice {
    @ExceptionHandler(NoSuchProductException.class)
    public ResponseEntity<ShopError> handleNoSuchProductException (NoSuchProductException e){
        return ResponseEntity.badRequest().body(new ShopError("404", e.getMessage()));
    }
}
