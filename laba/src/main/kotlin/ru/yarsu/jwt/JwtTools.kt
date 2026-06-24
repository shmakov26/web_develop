package ru.yarsu.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import ru.yarsu.User
import java.time.Instant

class JwtTools(
    secret: String,
) {
    private val algorithm = Algorithm.HMAC512(secret)
    private val verifier: JWTVerifier =
        JWT
            .require(algorithm)
            .build()

    fun createJWTToken(user: User): String =
        JWT
            .create()
            .withSubject(user.id.toString())
            .withExpiresAt(Instant.now().plusSeconds(60 * 120 * 1L))
            .sign(algorithm)

    fun validateJWTToken(token: String): DecodedJWT? =
        try {
            verifier.verify(token)
        } catch (_: JWTVerificationException) {
            null
        }
}
