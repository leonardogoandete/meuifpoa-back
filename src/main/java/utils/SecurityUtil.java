package utils;

import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SecurityUtil {
    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static boolean verifyBCryptPassword(String bCryptPasswordHash, String passwordToVerify) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {

        WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();

        // 1. Create a BCrypt Password Factory
        PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);

        // 2. Decode the hashed user password
        Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);

        // 3. Translate the decoded user password object to one which is consumable by this factory.
        Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);

        // Verify existing user password you want to verify
        return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());

    }
}