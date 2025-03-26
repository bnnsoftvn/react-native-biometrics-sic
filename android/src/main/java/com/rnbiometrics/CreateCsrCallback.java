package com.rnbiometrics;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.rnbiometrics.security.pkcs.PKCS10;
import com.rnbiometrics.security.x509.X500Name;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class CreateCsrCallback extends BiometricPrompt.AuthenticationCallback {
    private Promise promise;
    private String cn;
    private String ou;
    private String o;
    private String l;
    private String st;
    private String c;
    private PublicKey publickey;
    private Signature sig;

    public CreateCsrCallback(Promise promise,PublicKey publickey, Signature sig, String cn, String ou, String o, String l, String st, String c) {
        super();
        this.promise = promise;
        this.cn = cn;
        this.ou   = ou;
        this.o   = o;
        this.l   = l;
        this.st   = st;
        this.c   = c;
        this.publickey = publickey;
        this.sig = sig;
    }

    @Override
    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED ) {
            WritableMap resultMap = new WritableNativeMap();
            resultMap.putBoolean("success", false);
            resultMap.putString("error", "User cancellation");
            this.promise.resolve(resultMap);
        } else {
            WritableMap resultMap = new WritableNativeMap();
            resultMap.putBoolean("success", false);
            resultMap.putString("error", errString.toString());
            this.promise.resolve(resultMap);
        }
    }

    @Override
    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        try {
            X500Name x500Name = new X500Name(cn,ou,o,l,st,c);
            PKCS10 pkcs10 = new PKCS10(publickey);
            pkcs10.encodeAndSign(x500Name,sig);

            String csr = Base64.encodeToString(pkcs10.getEncoded(), Base64.DEFAULT);

            WritableMap resultMap = new WritableNativeMap();
            resultMap.putBoolean("success", true);
            resultMap.putString("csr", csr);
            promise.resolve(resultMap);
        } catch (Exception e) {
            WritableMap resultMap = new WritableNativeMap();
            resultMap.putBoolean("success", false);
            resultMap.putString("error", "Error creating signature: " + e.getMessage());
            promise.resolve(resultMap);
        }
    }
}
