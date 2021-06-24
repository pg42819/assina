package eu.assina.sa.model;

public interface AssinaSigner {
    byte[] signHash(byte[] pdfHash);
}
