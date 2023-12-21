package org.vaadin.tinymce.imageuploads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * A slightly modified version of Spring Boot example to save
 * MultiPart post requests to filesystem and store them by their
 * name.
 * <p>
 *     This version returns with the JSON format expected by
 *     the TinyMCE editor that declares the final URL of the
 *     uploaded image.
 * </p>
 * <p>
 *     There is also a mechanism to talk back to the editor UI.
 *     Here the use case is rather abstract as TinyMCE will also
 *     notify of a successful upload, but in one of my hobby apps
 *     the UI also contains a list of images associated with the
 *     edited entity, that needs to be updated after uploads.
 * </p>
 */
@Controller
public class ImageUploadController {

	private final FileSystemStorageService storageService;
	private final ActiveEditors activeEditors;

	@Autowired
	public ImageUploadController(FileSystemStorageService storageService, ActiveEditors activeEditors) {
		this.storageService = storageService;
		this.activeEditors = activeEditors;
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable(name = "filename") String filename) {
		Resource file = storageService.loadAsResource(filename);
		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	final Set<String> allowedImageTypes = Set.of(
			"image/jpeg",
			"image/png",
			"image/webp"
	);

	@PostMapping("/upload/{id}")
	@ResponseBody
	public String handleFileUpload(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) {
		String contentType = file.getContentType();
		if(allowedImageTypes.contains(contentType)) {
			boolean scaledDown = storageService.store(file);

			// This is an example how REST controller can
			// "talk back" to the UI
			activeEditors.notifyOfSavedImage(id, file.getOriginalFilename(), scaledDown);

			// This JSON format is expected by tinymce to know the final
			// URL of uploaded image
			return """
				{
					"location": "/files/%s"
				}
			""".formatted(file.getOriginalFilename());
		} else {
			throw new RuntimeException("");
		}
	}

}
