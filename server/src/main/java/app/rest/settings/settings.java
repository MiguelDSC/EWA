package app.rest.settings;

import app.models.Image;
import app.models.Settings;
import app.models.SettingsAPIResponse;
import app.repositories.Settings.SettingsRepository;
import app.repositories.user.UserRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@RestController
public class settings {

    @Autowired
    SettingsRepository settingsRepository;

    @Autowired
    UserRepositoryJPA userRepository;

    @GetMapping("/settings/{id}")
    public SettingsAPIResponse getSettings(@PathVariable Long id) {
        System.out.println(id);
        Settings foundSettings = settingsRepository.findById(id);
        return SettingsAPIResponse.fromSettingsModel(
                foundSettings
        );
    }

    @PostMapping("/settings/{id}")
    public void updateSettings(@RequestBody SettingsAPIResponse updatedSettings, @PathVariable Long id) {
        System.out.printf("body: \n%s\nid: %d\n", updatedSettings, id);
        Settings newSettings = SettingsAPIResponse.toSettingsModel(updatedSettings);
        newSettings.id = id;
        settingsRepository.save(newSettings);
    }

    //https://spring.io/guides/gs/uploading-files/
    @PutMapping("/settings/{id}/picture")
    public ResponseEntity<byte[]> updateProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws Exception {
        System.out.printf("File: %s\n", file.getBytes());
        System.out.println(file.getContentType());
        Image image = Image.fromMultiPartFile(file);
        System.out.println(image.mime);
        SettingsRepository.images.put(id, image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType()));
        return new ResponseEntity<>(
                file.getBytes(),
                headers,
                HttpStatus.OK
        );
    }

    @GetMapping("/settings/{id}/picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) throws Exception {
        Image file = SettingsRepository.images.get(id);
        if (file == null) file = Image.fromMultiPartFile(new MockMultipartFile(
                "default-avatar", "default-avatar.jpg", MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(new File("assets/default-avatar.jpg").toPath())
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.mime));

        return new ResponseEntity<>(
                file.bytes,
                headers,
                HttpStatus.OK
        );
    }
}
