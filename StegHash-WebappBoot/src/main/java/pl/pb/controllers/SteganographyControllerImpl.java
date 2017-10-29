package pl.pb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pb.controllers.SteganographyController;

/**
 * Created by Patryk on 10/18/2017.
 */
@RestController
@RequestMapping("/steg")
public class SteganographyControllerImpl implements SteganographyController {

    @Override
    public ResponseEntity<String> hideMessage() {
        return null;
    }
}
