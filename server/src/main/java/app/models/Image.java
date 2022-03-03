package app.models;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class Image {
    public byte[] bytes;
    public String mime;

    public Image(byte[] bytes, String mime) {
        this.bytes = bytes;
        this.mime = mime;
    }

    public static Image fromMultiPartFile(MultipartFile f) throws Exception {
        return new Image(f.getBytes(), f.getContentType());
    }

    public MultipartFile toMultiPartFile() {
        return new MockMultipartFile("",null, this.mime, this.bytes);
    }
}
