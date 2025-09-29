package com.grzegorzkartasiewicz.app;

public record UserRegistrationRequest(String firstName, String lastName, String email,
                                      String password) {

}
