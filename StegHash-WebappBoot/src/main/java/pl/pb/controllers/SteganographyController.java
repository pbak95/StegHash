package pl.pb.controllers;

import org.springframework.http.ResponseEntity;

/**
 * Created by Patryk on 10/18/2017.
 */
public interface SteganographyController {

    ResponseEntity<String> hideMessage();
}
