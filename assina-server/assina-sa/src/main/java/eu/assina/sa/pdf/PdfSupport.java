package eu.assina.sa.pdf;

import eu.assina.sa.model.AssinaSigner;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * An example for signing a PDF with bouncy castle.
 * A keystore can be created with the java keytool, for example:
 *
 * {@code keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias test -validity 365
 *        -v -keyalg RSA -keystore keystore.p12 }
 *
 * @author Thomas Chojecki
 * @author Vakhtang Koroghlishvili
 * @author John Hewson
 */
public class PdfSupport {

    private AssinaSigner signer;

    public PdfSupport(AssinaSigner contentSigner) {
        this.signer = contentSigner;
    }

    public byte[] signPdfContent(InputStream content) throws IOException, NoSuchAlgorithmException {
        byte[] pdfContentBytes = IOUtils.toByteArray(content);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] pdfHash = digest.digest(pdfContentBytes);
        byte[] signedHash = signer.signHash(pdfHash);
        return signedHash;
    }

    /**
     * Signs the given PDF file.
     *
     * @param inFile  input PDF file
     * @param outFile output PDF file
     * @throws IOException if the input file could not be read
     */
    public void signDetached(File inFile, File outFile) throws IOException, NoSuchAlgorithmException {
        if (inFile == null || !inFile.exists()) {
            throw new FileNotFoundException("Document for signing does not exist");
        }

        FileOutputStream fos = new FileOutputStream(outFile);

        // sign
        PDDocument doc = null;
        try {
            doc = PDDocument.load(inFile);
            signDetached(doc, fos);
        } finally {
            IOUtils.closeQuietly(doc);
            IOUtils.closeQuietly(fos);
        }
    }

    public void signDetached(PDDocument document, OutputStream output) throws IOException, NoSuchAlgorithmException {
        int accessPermissions = getMDPPermission(document);
        if (accessPermissions == 1) {
            throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
        }

        // create signature dictionary
        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName("Example User");
        signature.setLocation("Los Angeles, CA");
        signature.setReason("Testing");
        // TODO extract the above details from the signing certificate? Reason as a parameter?

        // the signing date, needed for valid signature
        signature.setSignDate(Calendar.getInstance());

        // Optional: certify
        if (accessPermissions == 0) {
            setMDPPermission(document, signature, 2);
        }

        document.addSignature(signature);
        ExternalSigningSupport externalSigning = document.saveIncrementalForExternalSigning(output);
        // invoke external signature service
        byte[] cmsSignature = signPdfContent(externalSigning.getContent());

        // set signature bytes received from the service
        externalSigning.setSignature(cmsSignature);
//        } else {
//            SignatureOptions signatureOptions = new SignatureOptions();
//             Size can vary, but should be enough for purpose.
//            signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);
//             register signature dictionary and sign interface
//            document.addSignature(signature, this, signatureOptions);
//
//             write incremental (only for signing purpose)
//            document.saveIncremental(output);
//        }
    }
    // Utilities for permissions Copied from the Apache Pdf-Box examples

    /**
     * Get the access permissions granted for this document in the DocMDP transform parameters
     * dictionary. Details are described in the table "Entries in the DocMDP transform parameters
     * dictionary" in the PDF specification.
     *
     * @param doc document.
     * @return the permission value. 0 means no DocMDP transform parameters dictionary exists. Other
     * return values are 1, 2 or 3. 2 is also returned if the DocMDP transform parameters dictionary
     * is found but did not contain a /P entry, or if the value is outside the valid range.
     */
    public static int getMDPPermission(PDDocument doc) {
        COSBase base = doc.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.PERMS);
        if (base instanceof COSDictionary) {
            COSDictionary permsDict = (COSDictionary) base;
            base = permsDict.getDictionaryObject(COSName.DOCMDP);
            if (base instanceof COSDictionary) {
                COSDictionary signatureDict = (COSDictionary) base;
                base = signatureDict.getDictionaryObject(COSName.REFERENCE);
                if (base instanceof COSArray) {
                    COSArray refArray = (COSArray) base;
                    for (int i = 0; i < refArray.size(); ++i) {
                        base = refArray.getObject(i);
                        if (base instanceof COSDictionary) {
                            COSDictionary sigRefDict = (COSDictionary) base;
                            if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject(COSName.TRANSFORM_METHOD))) {
                                base = sigRefDict.getDictionaryObject(COSName.TRANSFORM_PARAMS);
                                if (base instanceof COSDictionary) {
                                    COSDictionary transformDict = (COSDictionary) base;
                                    int accessPermissions = transformDict.getInt(COSName.P, 2);
                                    if (accessPermissions < 1 || accessPermissions > 3) {
                                        accessPermissions = 2;
                                    }
                                    return accessPermissions;
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Set the "modification detection and prevention" permissions granted for this document in the
     * DocMDP transform parameters dictionary. Details are described in the table "Entries in the
     * DocMDP transform parameters dictionary" in the PDF specification.
     *
     * @param doc               The document.
     * @param signature         The signature object.
     * @param accessPermissions The permission value (1, 2 or 3).
     * @throws IOException if a signature exists.
     */
    public static void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions) throws IOException {
        for (PDSignature sig : doc.getSignatureDictionaries()) {
            // "Approval signatures shall follow the certification signature if one is present"
            // thus we don't care about timestamp signatures
            if (COSName.DOC_TIME_STAMP.equals(sig.getCOSObject().getItem(COSName.TYPE))) {
                continue;
            }
            if (sig.getCOSObject().containsKey(COSName.CONTENTS)) {
                throw new IOException("DocMDP transform method not allowed if an approval signature exists");
            }
        }

        COSDictionary sigDict = signature.getCOSObject();

        // DocMDP specific stuff
        COSDictionary transformParameters = new COSDictionary();
        transformParameters.setItem(COSName.TYPE, COSName.TRANSFORM_PARAMS);
        transformParameters.setInt(COSName.P, accessPermissions);
        transformParameters.setName(COSName.V, "1.2");
        transformParameters.setNeedToBeUpdated(true);

        COSDictionary referenceDict = new COSDictionary();
        referenceDict.setItem(COSName.TYPE, COSName.SIG_REF);
        referenceDict.setItem(COSName.TRANSFORM_METHOD, COSName.DOCMDP);
        referenceDict.setItem(COSName.DIGEST_METHOD, COSName.getPDFName("SHA1"));
        referenceDict.setItem(COSName.TRANSFORM_PARAMS, transformParameters);
        referenceDict.setNeedToBeUpdated(true);

        COSArray referenceArray = new COSArray();
        referenceArray.add(referenceDict);
        sigDict.setItem(COSName.REFERENCE, referenceArray);
        referenceArray.setNeedToBeUpdated(true);

        // Catalog
        COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
        COSDictionary permsDict = new COSDictionary();
        catalogDict.setItem(COSName.PERMS, permsDict);
        permsDict.setItem(COSName.DOCMDP, signature);
        catalogDict.setNeedToBeUpdated(true);
        permsDict.setNeedToBeUpdated(true);
    }

    public static String TEST_HASH =
            "0\ufffd\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0007\u0002\ufffd\ufffd0\ufffd\u0002\u0001\u00011\u000f0\r\u0006\t`\ufffdH\u0001e\u0003\u0004\u0002\u0001\u0005\u00000\ufffd\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0007\u0001\ufffd\ufffd$\ufffd\u0004\u000ethis-is-a-hash\u0000\u0000\u0000\u0000\u0000\u0000\ufffd\ufffd0\ufffd\u0002\ufffd0\ufffd\u0001\ufffd\ufffd\u0003\u0002\u0001\u0002\u0002\u0006\u0001z+b9Z0\r\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0001\u000b\u0005\u00000\u00141\u00120\u0010\u0006\u0003U\u0004\u0003\f\tassina.eu0\u001e\u0017\r210620214455Z\u0017\r220620214455Z0\u00111\u000f0\r\u0006\u0003U\u0004\u0003\f\u0006carlos0\ufffd\u0001\"0\r\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0001\u0001\u0005\u0000\u0003\ufffd\u0001\u000f\u00000\ufffd\u0001\n\u0002\ufffd\u0001\u0001\u0000\ufffd\ufffdq@F\ufffd\ufffd\ts\ufffd)\ufffd\\\u001a\ufffd\u0016\ufffd\"h/\ufffd\ufffd\ufffdf،k^\ufffdO!\ufffdG\ufffd\ufffdkq$\ufffd\\\ufffd\ufffd2\ufffd\u0010iA@\ufffd\u001e::\ufffdF\u00140\u001c\ufffd\ufffd\u0003\ufffd'\ufffd\ufffd\ufffd\ufffdE\ufffd\ufffdjDXcr\ufffd\ufffd\"\u0018\ufffd\u0018v+k)~,\ufffd\u000f\ufffdbI\f\\\ufffdj@\ufffd&\t7\ufffd\u000bUxlb*\t\u0005\ufffdd\ufffd\u0001wk\ufffd\ufffdظ\ufffdo\ufffd\ufffd\ufffd\u0005\ufffd\ufffd\\\ufffd:\ufffdc\ufffd\b\ufffd\ufffd\u007f\ufffd\r\ufffd}\ufffd\ufffd\ufffd\ufffdNI\ufffdH;\ufffdE=\ufffd\ufffdu\ufffd\ufffdL\ufffd@\ufffd\ufffd\ufffd:\ufffd@jQ̕\ufffdJ\u0000\ufffd=\ufffdؿ\"\ufffd\ufffd-Qeۼ\u0014\ufffd\ufffd\ufffda\ufffd\u0004 eK\ufffd\ufffd\ufffd\ufffdB\n\ufffdf\ufffdd\ufffd\t\u001b_&\ufffdeV\ufffd\ufffd\ufffdΑ\ufffd\ufffd\ufffdp\ufffd\ufffdб\ufffd\ufffd̵\u0018\ufffdU\ufffd\ufffdO\ufffd|٫F R\ufffd\b\ufffd\u0002\u0003\u0001\u0000\u0001\ufffd\u00130\u00110\u000f\u0006\u0003U\u001d\u0013\u0001\u0001\ufffd\u0004\u00050\u0003\u0001\u0001\ufffd0\r\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0001\u000b\u0005\u0000\u0003\ufffd\u0001\u0001\u0000V{\f\ufffd\u0006\ufffd\ufffd\ufffd\u0013\ufffd\ufffdƂ\u0007\u001d.9\ufffd\ufffd\u0010*ÿ\ufffd)\ufffd\ufffd\u0011\ufffd̶\ufffd{\ufffdVݚJ|=\ufffd\u0006Q\ufffd\ufffd\ufffd\ufffd\ufffd\u001d\ufffd\ufffdP@\ufffd\ufffdjE\u0012K\ufffd\ufffd\ufffd\u000b·|\ufffds\ufffd\ufffd\ufffd6@w~l\ufffd\ufffd\\\t\ufffd/C\ufffd֦'\ufffdx'\u0019\ufffd\ufffdm\ufffdl\ufffd#\u001eNk*hx\ufffdx\"Qj\ufffd(\ufffd\ufffd\ufffdh\ufffd\ufffd&/\u0010\ufffd\te /\ufffdV<\ufffdɦa\ufffdZ\ufffd\ufffd\ufffdo;C\ufffd\ufffd\ufffd\ufffd\u0005\u007f\u0004n\ufffde]~\ufffd\u001d\ufffdT\ufffd\ufffd\ufffd\ufffd\ufffd,\u0010\ufffd!\ufffd\u000b\ufffd\ufffd-ɘ\ufffd\ufffd\ufffd}\ufffdj\ufffdg\re\ufffd\ufffd$\u001c\ufffd\ufffd9\ufffd\u0018\ufffd\u0019O\ufffdJ\ufffd66!\ufffd5\ufffd6\ufffdD\ufffd\ufffd\ufffdx\ufffd\ufffd\u001f\ufffd\u0019\ufffd\u0001S\u0000\ufffd\ufffd[bnzB\ufffd\u001cU\ufffd4n\ufffdDؔ3\ufffdX/=\ufffd\ufffdRv\n\u0013\ufffdu\ufffd\u0000\u00001\ufffd\u0001\ufffd0\ufffd\u0001\ufffd\u0002\u0001\u00010\u001e0\u00141\u00120\u0010\u0006\u0003U\u0004\u0003\f\tassina.eu\u0002\u0006\u0001z+b9Z0\r\u0006\t`\ufffdH\u0001e\u0003\u0004\u0002\u0001\u0005\u0000\ufffd\ufffd\ufffd0\u0018\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\t\u00031\u000b\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0007\u00010\u001c\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\t\u00051\u000f\u0017\r210620214831Z0-\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\t41 0\u001e0\r\u0006\t`\ufffdH\u0001e\u0003\u0004\u0002\u0001\u0005\u0000\ufffd\r\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0001\u000b\u0005\u00000/\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\t\u00041\"\u0004 \ufffd\"0#\ufffd\ufffd[\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdUDэ\\=\t\u0013\ufffd\ufffd\ufffdIqڠ\ufffd\ufffd\ufffd0\r\u0006\t*\ufffdH\ufffd\ufffd\r\u0001\u0001\u000b\u0005\u0000\u0004\ufffd\u0001\u0000À{\u0014G?\u0019\ufffd\ufffd\f\ufffd\f<۹HWs\ufffd\ufffd\ufffd_\ufffdzZ\ufffd6dGP\ufffd\b\ufffdE\ufffd\ufffd\ufffd\ufffd\ufffd\u0007\ufffd\\\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdX\ufffdx\ufffd\ufffdP\ufffdZ\ufffd,Yc\ufffd\ufffd\ufffd\b'j\ufffdN6\ufffd\ufffd\u007f\ufffd\ufffd\ufffd}@W\ufffd\ufffd䪙j\f\ufffd\ufffd\u0015af\ufffd\ufffd>uT:9\ufffde7N\ufffd\ufffd\ufffda\ufffd\f\u00131\u000b\ufffdhl\ufffd* \ufffd\ufffd\ufffdT\ufffdq\u001f@\ufffdʡU\f\u0004\u0004\ufffdO\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd6m\u001d \u001d\ufffd\\\ufffd\ufffd\ufffd\ufffdh\ufffd\ufffd5ϕ\ufffd\ufffdL|\ufffd\u000e\ufffd\ufffdw\ufffd5\ufffd\u0013\ufffd\u0007\ufffd\ufffd\ufffdg0\u0004\ufffd\u0005\ufffdFyTY}R-\ufffd\u0000C\r\u000f\ufffd\ufffd\ufffd.\ufffd{\ufffdO\ufffd\ufffd\ufffd:\ufffd\ufffd\ufffdw\ufffd\ufffdK\ufffd\u0007\ufffd\"R\ufffd\ufffdM\u0011\ufffd\u0010=H3\u0012\ufffdaq\u000es\f\u001c\ufffd%\ufffd\u007fŗԿ\ufffd\ufffd\ufffd\ufffda\ufffd\ufffd\r\ufffdԻ\ufffd\ufffd\u0000\u0000\u0000\u0000\u0000\u0000";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        PdfSupport signing = new PdfSupport(new AssinaSigner() {
            @Override
            public byte[] signHash(byte[] pdfHash) {
                return TEST_HASH.getBytes(StandardCharsets.UTF_8);
            }
        });

        File inFile = new File(args[0]);
        String name = inFile.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));

        File outFile = new File(inFile.getParent(), substring + "_signed.pdf");
        signing.signDetached(inFile, outFile);
    }
}
