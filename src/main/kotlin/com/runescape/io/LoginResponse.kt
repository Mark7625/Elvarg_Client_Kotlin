package com.runescape.io

enum class LoginResponse(val opcode: Int, val message1: String, val message2: String) {
    DEFUALT(-1, "Enter your username & password.", ""),
    USERNAME(-1, "", "Your username is too short."),
    NO_RESPONSE(-1, "No response from server", "Please try using a different world."),
    PASSWORD(-1, "", "Your password is too short."),
    CONNECTING(-1, "", "Connecting to server..."),
    CONNECTING_SERVER(0, "", ""),
    LOGIN(2, "", ""),
    ILLEGAL(29, "Username or password contains illegal", "characters. Try other combinations"),
    INVALID_LOGIN(3, "", "Invalid username or password"),
    BANNED(4, "Your account has been banned", ""),
    COMPUTER_BANNED(22, "Your computer has been banned", ""),
    NETWORK_BANNED(27, "Your host-address has been banned", ""),
    LOGGED_IN(5, "Your account is already logged in", "Try again in 60 secs..."),
    UPDATING(6, "Server is being Updated", "Try again in 60 secs..."),
    FULL(7, "The world is currently full", ""),
    OFFLINE(8, "Unable to connect", "Login server offline"),
    LOGIN_LIMIT(9, "Login limit exceeded", "Too many connections from your address."),
    BAD_SESSION(10, "Unable to connect. Bad session id.", "Try again in 60 secs..."),
    REJECTED(11, "Login server rejected session", "Try again in 60 secs..."),
    UUID(22, "Your computer has been UUID banned.", "Please appeal on the forums."),
    NONE(-1, "Issue Connecting to server", "Please report this in discord");

    companion object {

        val map = values().associateBy(LoginResponse::opcode)

        fun getResponse(type: Int) = map.getOrDefault(type, NONE)
    }
}
