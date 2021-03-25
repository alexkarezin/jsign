/**
 * Copyright 2021 Emmanuel Bourg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jsign;

import java.io.File;
import java.util.zip.CRC32;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class SignerHelperTest {

    @Test
    public void testDetachedSignature() throws Exception {
        File sourceFile = new File("target/test-classes/wineyes.exe");
        File targetFile = new File("target/test-classes/wineyes-signed-detached.exe");

        File detachedSignatureFile = new File("target/test-classes/wineyes-signed-detached.exe.sig");
        detachedSignatureFile.delete();

        FileUtils.copyFile(sourceFile, targetFile);

        SignerHelper signer = new SignerHelper(new StdOutConsole(2), "parameter")
                .keystore("target/test-classes/keystores/keystore.jks")
                .keypass("password");

        // sign and detach
        signer.sign(targetFile);

        assertFalse("Signature was detached", detachedSignatureFile.exists());

        signer.alg("SHA-512").detached(true);
        signer.sign(targetFile);

        assertTrue("Signature wasn't detached", detachedSignatureFile.exists());

        // attach the signature
        File targetFile2 = new File("target/test-classes/wineyes-signed-attached.exe");
        FileUtils.copyFile(sourceFile, targetFile2);
        File detachedSignatureFile2 = new File("target/test-classes/wineyes-signed-attached.exe.sig");
        detachedSignatureFile2.delete();
        detachedSignatureFile.renameTo(detachedSignatureFile2);

        signer = new SignerHelper(new StdOutConsole(2), "parameter").detached(true);
        signer.sign(targetFile2);

        assertEquals(FileUtils.checksum(targetFile, new CRC32()).getValue(), FileUtils.checksum(targetFile2, new CRC32()).getValue());
    }
}
