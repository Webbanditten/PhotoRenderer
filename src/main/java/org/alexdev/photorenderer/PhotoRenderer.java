package org.alexdev.photorenderer;

import com.google.common.io.LittleEndianDataInputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Arrays;

public class PhotoRenderer {
    public BufferedImage createImage(byte[] photoData, Color[] paletteData, PhotoRenderOption option) throws Exception {
        int CAST_PROPERTIES_OFFSET = 28;

        var bigEndianStream = new DataInputStream(new ByteArrayInputStream(photoData));
        var littleEndianStream = new LittleEndianDataInputStream(bigEndianStream);

        littleEndianStream.skip(CAST_PROPERTIES_OFFSET);

        int totalWidth = bigEndianStream.readShort() & 0x7FFF;

        int top = bigEndianStream.readShort();
        int left = bigEndianStream.readShort();
        int bottom = bigEndianStream.readShort();
        int right = bigEndianStream.readShort();

        Rectangle rectangle = new Rectangle(left, top, right - left, bottom - top);

        littleEndianStream.read();
        littleEndianStream.skip(7);
        bigEndianStream.readShort();
        bigEndianStream.readShort();
        littleEndianStream.read();

        int bitDepth = littleEndianStream.read();

        if (bitDepth != 8)
            throw new Exception("illegal");

        int palette = bigEndianStream.readInt() - 1; //Make sure that this one equals -3 = Grayscale

        if (palette != -3)
            throw new Exception("illegal");

        littleEndianStream.readInt(); // No idea! Lmao
        littleEndianStream.skip(4); // Reversed, should equal BITD

        int length = littleEndianStream.readInt();
        int position = 0;

        var data = new int[totalWidth * rectangle.height];

        while (littleEndianStream.available() > 0) {
            int marker = littleEndianStream.read();

            if (marker >= 128) {
                int fill = littleEndianStream.read();

                for (int i = 0; i < 257 - marker; i++) {
                    data[position] = fill;
                    position++;
                }

            } else {
                int[] buffer = new int[marker + 1];

                for (int i = 0; i < buffer.length; i++) {
                    data[position] = littleEndianStream.read();
                    position++;
                }
            }
        }

        var image = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < rectangle.height; y++) {
            int[] row = Arrays.copyOfRange(data, y * totalWidth, (y * totalWidth) + totalWidth);

            if (row.length > 0) {
                for (int x = 0; x < rectangle.width; x++) {
                    int index = row[x];
                    var rgb = paletteData[index];

                    int r = rgb.getRed();
                    int g = rgb.getGreen();
                    int b = rgb.getBlue();

                    Color color = new Color(r, g, b);
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }

        littleEndianStream.close();
        bigEndianStream.close();

        if (option == PhotoRenderOption.SEPIA) {
            int[] palettes = {
                    0xffb85e2f,
                    0xffc06533,
                    0xfff08b46,
                    0xff681f10,
                    0xff88381c,
                    0xffc86b36,
                    0xffffd169,
                    0xffe07e3f,
                    0xffffb159,
                    0xffffde6f,
                    0xff702513,
                    0xffffea75,
                    0xffffd269
            };

            IndexColorModel colorModel = new IndexColorModel(8,         // bits per pixel
                    palettes.length,         // size of color component array
                    palettes,   // color map
                    0,         // offset in the map
                    false,      // has alpha
                    0,         // the pixel value that should be transparent
                    DataBuffer.TYPE_BYTE);

            BufferedImage img = new BufferedImage(
                    image.getWidth(), image.getHeight(), // match source
                    BufferedImage.TYPE_BYTE_BINARY, // required to work
                    colorModel); // TYPE_BYTE_BINARY color model (i.e. palette)

            Graphics2D g2 = img.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            return img;
        }

        return image;
    }

    public Color[] getCachedPalette() {
        return new Color[] {
                new Color( 255, 255, 255),
                new Color( 254, 254, 254),
                new Color( 255, 255, 255),
                new Color( 253, 253, 253),
                new Color( 252, 252, 252),
                new Color( 251, 251, 251),
                new Color( 250, 250, 250),
                new Color( 249, 249, 249),
                new Color( 248, 248, 248),
                new Color( 247, 247, 247),
                new Color( 246, 246, 246),
                new Color( 245, 245, 245),
                new Color( 244, 244, 244),
                new Color( 243, 243, 243),
                new Color( 255, 255, 255),
                new Color( 242, 242, 242),
                new Color( 241, 241, 241),
                new Color( 240, 240, 240),
                new Color( 239, 239, 239),
                new Color( 238, 238, 238),
                new Color( 237, 237, 237),
                new Color( 236, 236, 236),
                new Color( 235, 235, 235),
                new Color( 234, 234, 234),
                new Color( 233, 233, 233),
                new Color( 232, 232, 232),
                new Color( 255, 255, 255),
                new Color( 231, 231, 231),
                new Color( 230, 230, 230),
                new Color( 229, 229, 229),
                new Color( 228, 228, 228),
                new Color( 227, 227, 227),
                new Color( 223, 223, 223),
                new Color( 222, 222, 222),
                new Color( 255, 255, 255),
                new Color( 221, 221, 221),
                new Color( 220, 220, 220),
                new Color( 219, 219, 219),
                new Color( 218, 218, 218),
                new Color( 217, 217, 217),
                new Color( 216, 216, 216),
                new Color( 215, 215, 215),
                new Color( 214, 214, 214),
                new Color( 213, 213, 213),
                new Color( 212, 212, 212),
                new Color( 211, 211, 211),
                new Color( 255, 255, 255),
                new Color( 210, 210, 210),
                new Color( 209, 209, 209),
                new Color( 208, 208, 208),
                new Color( 207, 207, 207),
                new Color( 206, 206, 206),
                new Color( 205, 205, 205),
                new Color( 204, 204, 204),
                new Color( 203, 203, 203),
                new Color( 202, 202, 202),
                new Color( 201, 201, 201),
                new Color( 200, 200, 200),
                new Color( 255, 255, 255),
                new Color( 199, 199, 199),
                new Color( 198, 198, 198),
                new Color( 197, 197, 197),
                new Color( 196, 196, 196),
                new Color( 195, 195, 195),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 255, 255, 255),
                new Color( 191, 191, 191),
                new Color( 190, 190, 190),
                new Color( 255, 255, 255),
                new Color( 189, 189, 189),
                new Color( 188, 188, 188),
                new Color( 187, 187, 187),
                new Color( 186, 186, 186),
                new Color( 185, 185, 185),
                new Color( 184, 184, 184),
                new Color( 183, 183, 183),
                new Color( 182, 182, 182),
                new Color( 181, 181, 181),
                new Color( 180, 180, 180),
                new Color( 179, 179, 179),
                new Color( 255, 255, 255),
                new Color( 178, 178, 178),
                new Color( 177, 177, 177),
                new Color( 176, 176, 176),
                new Color( 175, 175, 175),
                new Color( 174, 174, 174),
                new Color( 173, 173, 173),
                new Color( 172, 172, 172),
                new Color( 171, 171, 171),
                new Color( 170, 170, 170),
                new Color( 169, 169, 169),
                new Color( 168, 168, 168),
                new Color( 255, 255, 255),
                new Color( 167, 167, 167),
                new Color( 166, 166, 166),
                new Color( 165, 165, 165),
                new Color( 164, 164, 164),
                new Color( 163, 163, 163),
                new Color( 159, 159, 159),
                new Color( 158, 158, 158),
                new Color( 255, 255, 255),
                new Color( 157, 157, 157),
                new Color( 156, 156, 156),
                new Color( 155, 155, 155),
                new Color( 154, 154, 154),
                new Color( 153, 153, 153),
                new Color( 152, 152, 152),
                new Color( 151, 151, 151),
                new Color( 150, 150, 150),
                new Color( 149, 149, 149),
                new Color( 148, 148, 148),
                new Color( 147, 147, 147),
                new Color( 255, 255, 255),
                new Color( 146, 146, 146),
                new Color( 145, 145, 145),
                new Color( 144, 144, 144),
                new Color( 143, 143, 143),
                new Color( 142, 142, 142),
                new Color( 141, 141, 141),
                new Color( 140, 140, 140),
                new Color( 139, 139, 139),
                new Color( 138, 138, 138),
                new Color( 137, 137, 137),
                new Color( 136, 136, 136),
                new Color( 255, 255, 255),
                new Color( 135, 135, 135),
                new Color( 134, 134, 134),
                new Color( 133, 133, 133),
                new Color( 132, 132, 132),
                new Color( 131, 131, 131),
                new Color( 127, 127, 127),
                new Color( 126, 126, 126),
                new Color( 255, 255, 255),
                new Color( 125, 125, 125),
                new Color( 124, 124, 124),
                new Color( 123, 123, 123),
                new Color( 122, 122, 122),
                new Color( 121, 121, 121),
                new Color( 120, 120, 120),
                new Color( 119, 119, 119),
                new Color( 118, 118, 118),
                new Color( 117, 117, 117),
                new Color( 116, 116, 116),
                new Color( 115, 115, 115),
                new Color( 255, 255, 255),
                new Color( 114, 114, 114),
                new Color( 113, 113, 113),
                new Color( 112, 112, 112),
                new Color( 111, 111, 111),
                new Color( 110, 110, 110),
                new Color( 109, 109, 109),
                new Color( 108, 108, 108),
                new Color( 107, 107, 107),
                new Color( 106, 106, 106),
                new Color( 105, 105, 105),
                new Color( 104, 104, 104),
                new Color( 255, 255, 255),
                new Color( 103, 103, 103),
                new Color( 102, 102, 102),
                new Color( 101, 101, 101),
                new Color( 100, 100, 100),
                new Color( 99, 99, 99),
                new Color( 95, 95, 95),
                new Color( 94, 94, 94),
                new Color( 255, 255, 255),
                new Color( 93, 93, 93),
                new Color( 92, 92, 92),
                new Color( 91, 91, 91),
                new Color( 90, 90, 90),
                new Color( 89, 89, 89),
                new Color( 88, 88, 88),
                new Color( 87, 87, 87),
                new Color( 86, 86, 86),
                new Color( 85, 85, 85),
                new Color( 84, 84, 84),
                new Color( 83, 83, 83),
                new Color( 255, 255, 255),
                new Color( 82, 82, 82),
                new Color( 81, 81, 81),
                new Color( 80, 80, 80),
                new Color( 79, 79, 79),
                new Color( 78, 78, 78),
                new Color( 77, 77, 77),
                new Color( 76, 76, 76),
                new Color( 75, 75, 75),
                new Color( 74, 74, 74),
                new Color( 73, 73, 73),
                new Color( 72, 72, 72),
                new Color( 255, 255, 255),
                new Color( 71, 71, 71),
                new Color( 70, 70, 70),
                new Color( 69, 69, 69),
                new Color( 68, 68, 68),
                new Color( 67, 67, 67),
                new Color( 63, 63, 63),
                new Color( 62, 62, 62),
                new Color( 255, 255, 255),
                new Color( 61, 61, 61),
                new Color( 60, 60, 60),
                new Color( 59, 59, 59),
                new Color( 58, 58, 58),
                new Color( 57, 57, 57),
                new Color( 56, 56, 56),
                new Color( 55, 55, 55),
                new Color( 54, 54, 54),
                new Color( 53, 53, 53),
                new Color( 52, 52, 52),
                new Color( 51, 51, 51),
                new Color( 255, 255, 255),
                new Color( 50, 50, 50),
                new Color( 49, 49, 49),
                new Color( 48, 48, 48),
                new Color( 47, 47, 47),
                new Color( 46, 46, 46),
                new Color( 45, 45, 45),
                new Color( 44, 44, 44),
                new Color( 43, 43, 43),
                new Color( 42, 42, 42),
                new Color( 41, 41, 41),
                new Color( 40, 40, 40),
                new Color( 255, 255, 255),
                new Color( 39, 39, 39),
                new Color( 38, 38, 38),
                new Color( 37, 37, 37),
                new Color( 36, 36, 36),
                new Color( 35, 35, 35)
        };
    }

    public Color[] readPalette(String paletteFileName) throws Exception {
        var input = new LittleEndianDataInputStream(new FileInputStream(paletteFileName));
        new String(input.readNBytes(4));

        input.readInt();

        new String(input.readNBytes(4));
        new String(input.readNBytes(4));

        input.readInt();
        input.readShort();

        Color[] colors = new Color[input.readShort()];

        for (int i = 0; i < colors.length; i++) {
            int r = input.read();
            int g = input.read();
            int b = input.read();
            colors[i] = new Color(r, g, b);
            input.readByte();
            //System.out.println("new Color( " + r + ", " + g + ", " + b + "),");
        }

        return colors;
    }
}
