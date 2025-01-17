# PhotoRenderer
A tool used to render old Habbo photos. This supports the Habbo camera used from 2005-2009. How it works is that it essentially converts back the binary blob from the MUS data from BITD to a PNG file, and gives either the option of using greyscale or the traditional Sepia style.

## Output

Traditional Sepia theme:

![](https://i.imgur.com/x0dLwN6.png)

Greyscale theme:

![](https://i.imgur.com/2M8qYJJ.png)

## How to use

```java
// Feel free to read your own PAL files by using PaletteUtils.readPalette("palette/greyscale.pal")
// and adding it to the constructor instead
PhotoRenderer photoViewer = new PhotoRenderer(GreyscalePalette.getPalette(), RenderOption.SEPIA);

var photoData = Files.readAllBytes(Path.of("photo.bin"));
var src = photoViewer.createImage(photoData);

ImageIO.write(src, "PNG", new File("output_sepia.png"));
```


java -cp PhotoRenderer-1.0-SNAPSHOT.jar org.webbanditten.server.Server



## Contributors

@PaulusParssinen - Originally wrote this in C# (Greyscale only) so I ported it to Java and created the Sepia/official Habbo output.
