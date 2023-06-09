package org.travelling.ticketer.utility;

import io.nayuki.qrcodegen.QrCode;
import org.springframework.stereotype.Component;
import org.travelling.ticketer.business.QrContentBuilder;
import org.travelling.ticketer.constants.Filenames;
import org.travelling.ticketer.dto.QrCodeContentDTO;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Component
public class QrCodeImageGenerator {

    private final QrContentBuilder qrContentBuilder;

    public QrCodeImageGenerator(QrContentBuilder qrContentBuilder) {
        this.qrContentBuilder = qrContentBuilder;
    }

    public void createQrCodeImage(QrCodeContentDTO qrCodeContentDTO, String iv) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException {
        String qrCodeContent = qrContentBuilder.build(qrCodeContentDTO, iv);
        QrCode qrCode = QrCode.encodeText(qrCodeContent, QrCode.Ecc.HIGH);
        BufferedImage img = toImage(qrCode, 10, 4);
        ImageIO.write(img, "png", new File(Filenames.QR_CODE));
    }

    public QrCodeContentDTO parseContent(String qrCodeData) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, KeyStoreException, IOException, InvalidKeyException {
        return qrContentBuilder.parseContent(qrCodeData);

    }

    private static BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    private static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0)
            throw new IllegalArgumentException("Value out of range");
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale)
            throw new IllegalArgumentException("Scale or border too large");

        BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

}
