package pl.pb.services;

/**
 * Created by Patryk on 10/18/2017.
 */
public interface StegPublisherService {

    boolean publishHiddenMessage(String message, String from, String to);
}
