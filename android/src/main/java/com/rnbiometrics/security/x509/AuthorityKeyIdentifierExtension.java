/*
 * Copyright (c) 1997, 2009, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.rnbiometrics.security.x509;

import com.rnbiometrics.security.util.DerOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * This class represents the Authority Key Identifier Extension.
 *
 * <p>The authority key identifier extension provides a means of
 * identifying the particular public key used to sign a certificate.
 * This extension would be used where an issuer has multiple signing
 * keys (either due to multiple concurrent key pairs or due to
 * changeover).
 * <p>
 * The ASN.1 syntax for this is:
 * <pre>
 * AuthorityKeyIdentifier ::= SEQUENCE {
 *    keyIdentifier             [0] KeyIdentifier           OPTIONAL,
 *    authorityCertIssuer       [1] GeneralNames            OPTIONAL,
 *    authorityCertSerialNumber [2] CertificateSerialNumber OPTIONAL
 * }
 * KeyIdentifier ::= OCTET STRING
 * </pre>
 * @author Amit Kapoor
 * @author Hemma Prafullchandra
 * @see com.rnbiometrics.security.x509.Extension
 * @see com.rnbiometrics.security.x509.CertAttrSet
 */
public class AuthorityKeyIdentifierExtension extends Extension
implements CertAttrSet<String> {
    /**
     * Identifier for this attribute, to be used with the
     * get, set, delete methods of Certificate, x509 type.
     */
    public static final String IDENT =
                         "x509.info.extensions.AuthorityKeyIdentifier";
    /**
     * Attribute names.
     */
    public static final String NAME = "AuthorityKeyIdentifier";
    public static final String KEY_ID = "key_id";
    public static final String AUTH_NAME = "auth_name";
    public static final String SERIAL_NUMBER = "serial_number";

    // Private data members
    private static final byte TAG_ID = 0;
    private static final byte TAG_NAMES = 1;
    private static final byte TAG_SERIAL_NUM = 2;

    private com.rnbiometrics.security.x509.KeyIdentifier id = null;
    private com.rnbiometrics.security.x509.GeneralNames names = null;
    private com.rnbiometrics.security.x509.SerialNumber serialNum = null;

    // Encode only the extension value
    private void encodeThis() throws IOException {
        if (id == null && names == null && serialNum == null) {
            this.extensionValue = null;
            return;
        }
        com.rnbiometrics.security.util.DerOutputStream seq = new com.rnbiometrics.security.util.DerOutputStream();
        com.rnbiometrics.security.util.DerOutputStream tmp = new com.rnbiometrics.security.util.DerOutputStream();
        if (id != null) {
            com.rnbiometrics.security.util.DerOutputStream tmp1 = new com.rnbiometrics.security.util.DerOutputStream();
            id.encode(tmp1);
            tmp.writeImplicit(com.rnbiometrics.security.util.DerValue.createTag(com.rnbiometrics.security.util.DerValue.TAG_CONTEXT,
                              false, TAG_ID), tmp1);
        }
        try {
            if (names != null) {
                com.rnbiometrics.security.util.DerOutputStream tmp1 = new com.rnbiometrics.security.util.DerOutputStream();
                names.encode(tmp1);
                tmp.writeImplicit(com.rnbiometrics.security.util.DerValue.createTag(com.rnbiometrics.security.util.DerValue.TAG_CONTEXT,
                                  true, TAG_NAMES), tmp1);
            }
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
        if (serialNum != null) {
            com.rnbiometrics.security.util.DerOutputStream tmp1 = new DerOutputStream();
            serialNum.encode(tmp1);
            tmp.writeImplicit(com.rnbiometrics.security.util.DerValue.createTag(com.rnbiometrics.security.util.DerValue.TAG_CONTEXT,
                              false, TAG_SERIAL_NUM), tmp1);
        }
        seq.write(com.rnbiometrics.security.util.DerValue.tag_Sequence, tmp);
        this.extensionValue = seq.toByteArray();
    }

    /**
     * The default constructor for this extension.  Null parameters make
     * the element optional (not present).
     *
     * @param id the KeyIdentifier associated with this extension.
     * @param names the GeneralNames associated with this extension
     * @param serialNum the CertificateSerialNumber associated with
     *         this extension.
     * @exception IOException on error.
     */
    public AuthorityKeyIdentifierExtension(com.rnbiometrics.security.x509.KeyIdentifier kid, com.rnbiometrics.security.x509.GeneralNames name,
                                           com.rnbiometrics.security.x509.SerialNumber sn)
    throws IOException {
        this.id = kid;
        this.names = name;
        this.serialNum = sn;

        this.extensionId = com.rnbiometrics.security.x509.PKIXExtensions.AuthorityKey_Id;
        this.critical = false;
        encodeThis();
    }

    /**
     * Create the extension from the passed DER encoded value of the same.
     *
     * @param critical true if the extension is to be treated as critical.
     * @param value an array of DER encoded bytes of the actual value.
     * @exception ClassCastException if value is not an array of bytes
     * @exception IOException on error.
     */
    public AuthorityKeyIdentifierExtension(Boolean critical, Object value)
    throws IOException {
        this.extensionId = com.rnbiometrics.security.x509.PKIXExtensions.AuthorityKey_Id;
        this.critical = critical.booleanValue();

        this.extensionValue = (byte[]) value;
        com.rnbiometrics.security.util.DerValue val = new com.rnbiometrics.security.util.DerValue(this.extensionValue);
        if (val.tag != com.rnbiometrics.security.util.DerValue.tag_Sequence) {
            throw new IOException("Invalid encoding for " +
                                  "AuthorityKeyIdentifierExtension.");
        }

        // Note that all the fields in AuthorityKeyIdentifier are defined as
        // being OPTIONAL, i.e., there could be an empty SEQUENCE, resulting
        // in val.data being null.
        while ((val.data != null) && (val.data.available() != 0)) {
            com.rnbiometrics.security.util.DerValue opt = val.data.getDerValue();

            // NB. this is always encoded with the IMPLICIT tag
            // The checks only make sense if we assume implicit tagging,
            // with explicit tagging the form is always constructed.
            if (opt.isContextSpecific(TAG_ID) && !opt.isConstructed()) {
                if (id != null)
                    throw new IOException("Duplicate KeyIdentifier in " +
                                          "AuthorityKeyIdentifier.");
                opt.resetTag(com.rnbiometrics.security.util.DerValue.tag_OctetString);
                id = new com.rnbiometrics.security.x509.KeyIdentifier(opt);

            } else if (opt.isContextSpecific(TAG_NAMES) &&
                       opt.isConstructed()) {
                if (names != null)
                    throw new IOException("Duplicate GeneralNames in " +
                                          "AuthorityKeyIdentifier.");
                opt.resetTag(com.rnbiometrics.security.util.DerValue.tag_Sequence);
                names = new com.rnbiometrics.security.x509.GeneralNames(opt);

            } else if (opt.isContextSpecific(TAG_SERIAL_NUM) &&
                       !opt.isConstructed()) {
                if (serialNum != null)
                    throw new IOException("Duplicate SerialNumber in " +
                                          "AuthorityKeyIdentifier.");
                opt.resetTag(com.rnbiometrics.security.util.DerValue.tag_Integer);
                serialNum = new com.rnbiometrics.security.x509.SerialNumber(opt);
            } else
                throw new IOException("Invalid encoding of " +
                                      "AuthorityKeyIdentifierExtension.");
        }
    }

    /**
     * Return the object as a string.
     */
    public String toString() {
        String s = super.toString() + "AuthorityKeyIdentifier [\n";
        if (id != null) {
            s += id.toString();     // id already has a newline
        }
        if (names != null) {
            s += names.toString() + "\n";
        }
        if (serialNum != null) {
            s += serialNum.toString() + "\n";
        }
        return (s + "]\n");
    }

    /**
     * Write the extension to the OutputStream.
     *
     * @param out the OutputStream to write the extension to.
     * @exception IOException on error.
     */
    public void encode(OutputStream out) throws IOException {
        com.rnbiometrics.security.util.DerOutputStream tmp = new com.rnbiometrics.security.util.DerOutputStream();
        if (this.extensionValue == null) {
            extensionId = PKIXExtensions.AuthorityKey_Id;
            critical = false;
            encodeThis();
        }
        super.encode(tmp);
        out.write(tmp.toByteArray());
    }

    /**
     * Set the attribute value.
     */
    public void set(String name, Object obj) throws IOException {
        if (name.equalsIgnoreCase(KEY_ID)) {
            if (!(obj instanceof com.rnbiometrics.security.x509.KeyIdentifier)) {
              throw new IOException("Attribute value should be of " +
                                    "type KeyIdentifier.");
            }
            id = (KeyIdentifier)obj;
        } else if (name.equalsIgnoreCase(AUTH_NAME)) {
            if (!(obj instanceof com.rnbiometrics.security.x509.GeneralNames)) {
              throw new IOException("Attribute value should be of " +
                                    "type GeneralNames.");
            }
            names = (GeneralNames)obj;
        } else if (name.equalsIgnoreCase(SERIAL_NUMBER)) {
            if (!(obj instanceof com.rnbiometrics.security.x509.SerialNumber)) {
              throw new IOException("Attribute value should be of " +
                                    "type SerialNumber.");
            }
            serialNum = (SerialNumber)obj;
        } else {
          throw new IOException("Attribute name not recognized by " +
                        "CertAttrSet:AuthorityKeyIdentifier.");
        }
        encodeThis();
    }

    /**
     * Get the attribute value.
     */
    public Object get(String name) throws IOException {
        if (name.equalsIgnoreCase(KEY_ID)) {
            return (id);
        } else if (name.equalsIgnoreCase(AUTH_NAME)) {
            return (names);
        } else if (name.equalsIgnoreCase(SERIAL_NUMBER)) {
            return (serialNum);
        } else {
          throw new IOException("Attribute name not recognized by " +
                        "CertAttrSet:AuthorityKeyIdentifier.");
        }
    }

    /**
     * Delete the attribute value.
     */
    public void delete(String name) throws IOException {
        if (name.equalsIgnoreCase(KEY_ID)) {
            id = null;
        } else if (name.equalsIgnoreCase(AUTH_NAME)) {
            names = null;
        } else if (name.equalsIgnoreCase(SERIAL_NUMBER)) {
            serialNum = null;
        } else {
          throw new IOException("Attribute name not recognized by " +
                        "CertAttrSet:AuthorityKeyIdentifier.");
        }
        encodeThis();
    }

    /**
     * Return an enumeration of names of attributes existing within this
     * attribute.
     */
    public Enumeration<String> getElements() {
        com.rnbiometrics.security.x509.AttributeNameEnumeration elements = new AttributeNameEnumeration();
        elements.addElement(KEY_ID);
        elements.addElement(AUTH_NAME);
        elements.addElement(SERIAL_NUMBER);

        return (elements.elements());
    }

    /**
     * Return the name of this attribute.
     */
    public String getName() {
        return (NAME);
    }
}
